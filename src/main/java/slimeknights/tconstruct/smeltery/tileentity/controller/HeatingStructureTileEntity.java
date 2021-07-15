package slimeknights.tconstruct.smeltery.tileentity.controller;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.SmelteryControllerBlock;
import slimeknights.tconstruct.smeltery.inventory.HeatingStructureContainer;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;
import slimeknights.tconstruct.smeltery.network.StructureUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.module.EntityMeltingModule;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.HeatingStructureMultiblock.StructureData;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockResult;
import slimeknights.tconstruct.smeltery.tileentity.tank.IDisplayFluidListener;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class HeatingStructureTileEntity extends NamableTileEntity implements ITickableTileEntity, IMasterLogic, ISmelteryTankHandler {
  private static final String TAG_STRUCTURE = "structure";
  private static final String TAG_TANK = "tank";
  private static final String TAG_INVENTORY = "inventory";
  private static final String TAG_ERROR_POS = "errorPos";

  /** Sub module to detect the multiblock for this structure */
  private final HeatingStructureMultiblock<?> multiblock = createMultiblock();

  /** Position of the block causing the structure to not form */
  @Nullable @Getter
  private BlockPos errorPos;
  /** Number of ticks the error will remain visible for */
  private int errorVisibleFor = 0;

  /* Saved data, written to NBT */
  /** Current structure contents */
  @Nullable @Getter
  protected StructureData structure;
  /** Tank instance for this smeltery */
  @Getter
  protected final SmelteryTank tank = new SmelteryTank(this);
  /** Capability to pass to drains for fluid handling */
  @Getter
  private LazyOptional<IFluidHandler> fluidCapability = LazyOptional.empty();

  /** Inventory handling melting items */
  @Getter
  protected final MeltingModuleInventory meltingInventory = createMeltingInventory();

  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> meltingInventory);

  /** Fuel module */
  @Getter
  protected final FuelModule fuelModule = new FuelModule(this, () ->  structure != null ? structure.getTanks() : Collections.emptyList());
  /** Current fuel consumption rate */
  protected int fuelRate = 1;


  /** Module handling entity interaction */
  protected final EntityMeltingModule entityModule = new EntityMeltingModule(this, tank, this::canMeltEntities, this::insertIntoInventory, () -> structure == null ? null : structure.getBounds());


  /* Instance data, this data is not written to NBT */
  /** Timer to allow delaying actions based on number of ticks alive */
  protected int tick = 0;
  /** Updates every second. Once it reaches 10, checks above the smeltery for a layer to see if we can expand up */
  private int expandCounter = 0;
  /** If true, structure will check for an update next tick */
  private boolean structureUpdateQueued = false;
  /** If true, fluids have changed since the last update and should be synced to the client, synced at most once every 4 ticks */
  private boolean fluidUpdateQueued = false;
  /** Cache of the bounds for the case of no structure */
  private AxisAlignedBB defaultBounds;

  /* Client display */
  @Getter
  private final IModelData modelData = new SinglePropertyData<>(IDisplayFluidListener.PROPERTY);
  private final List<WeakReference<IDisplayFluidListener>> fluidDisplayListeners = new ArrayList<>();

  /* Misc helpers */
  /** Function to drop an item */
  protected final Consumer<ItemStack> dropItem = this::dropItem;

  protected HeatingStructureTileEntity(TileEntityType<? extends HeatingStructureTileEntity> type, ITextComponent name) {
    super(type, name);
  }

  /* Abstract methods */

  /** Creates the multiblock for this tile */
  protected abstract HeatingStructureMultiblock<?> createMultiblock();

  /** Creates the melting inventory for this structure  */
  protected abstract MeltingModuleInventory createMeltingInventory();

  /** Called while active to heat the contained items */
  protected abstract void heat();


  /* Logic */

  /** Updates the error position and syncs to the client if relevant */
  private void updateErrorPos() {
    BlockPos oldErrorPos = this.errorPos;
    this.errorPos = multiblock.getLastResult().getPos();
    if (!Objects.equals(oldErrorPos, errorPos)) {
      TinkerNetwork.getInstance().sendToClientsAround(new StructureErrorPositionPacket(pos, errorPos), world, pos);
    }
  }

  @Override
  public void tick() {
    if (world == null || world.isRemote) {
      if (errorVisibleFor > 0) {
        errorVisibleFor--;
      }
      return;
    }
    // invalid state, just a safety check in case its air somehow
    BlockState state = getBlockState();
    if (!state.hasProperty(ControllerBlock.IN_STRUCTURE)) {
      return;
    }

    // run structure update if requested
    if (structureUpdateQueued) {
      checkStructure();
      structureUpdateQueued = false;
    }

    // if we have a structure, run smeltery logic
    if (structure != null && state.get(SmelteryControllerBlock.IN_STRUCTURE)) {
      // every 15 seconds, check above the smeltery to try to expand
      if (tick == 0) {
        expandCounter++;
        if (expandCounter >= 10 && structure.getInnerY() < multiblock.getMaxHeight()) {
          expandCounter = 0;
          // instead of rechecking the whole structure, just recheck the layer above and queue an update if its usable
          if (multiblock.canExpand(structure, world)) {
            updateStructure();
          } else {
            updateErrorPos();
          }
        }
      } else if (tick % 4 == 0) {
        // check the next inside position to see if its a valid inner block every other tick
        if (!multiblock.isInnerBlock(world, structure.getNextInsideCheck())) {
          updateStructure();
        }
      }

      // main heating logic
      heat();

      // fluid update sync every four ticks, whether it has tanks or not
      if (tick % 4 == 3) {
        if (fluidUpdateQueued) {
          fluidUpdateQueued = false;
          tank.syncFluids();
        }
      }
    } else if (tick == 0) {
      updateStructure();
    }

    // update tick timer
    tick = (tick + 1) % 20;
  }

  /**
   * Drops an item into the world
   * @param stack  Item to drop
   */
  protected void dropItem(ItemStack stack) {
    assert world != null;
    if (!world.isRemote && !stack.isEmpty()) {
      double x = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
      double y = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
      double z = (double)(world.rand.nextFloat() * 0.5F) + 0.25D;
      BlockPos pos = this.pos.offset(getBlockState().get(ControllerBlock.FACING));
      ItemEntity itementity = new ItemEntity(world, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, stack);
      itementity.setDefaultPickupDelay();
      world.addEntity(itementity);
    }
  }


  /* Capability */

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    this.itemCapability.invalidate();
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return itemCapability.cast();
    }
    return super.getCapability(capability, facing);
  }


  /* Structure */

  /**
   * Marks the smeltery for a structure check
   */
  public void updateStructure() {
    structureUpdateQueued = true;
  }

  /**
   * Sets the structure and updates results of the new size, good method to override
   * @param structure  New structure
   */
  protected void setStructure(@Nullable StructureData structure) {
    this.structure = structure;
  }

  /**
   * Attempts to locate a valid smeltery structure
   */
  protected void checkStructure() {
    if (world == null || world.isRemote) {
      return;
    }
    boolean wasFormed = getBlockState().get(ControllerBlock.IN_STRUCTURE);
    StructureData oldStructure = structure;
    StructureData newStructure = multiblock.detectMultiblock(world, pos, getBlockState().get(BlockStateProperties.HORIZONTAL_FACING));

    // update block state
    boolean formed = newStructure != null;
    if (formed != wasFormed) {
      world.setBlockState(pos, getBlockState().with(ControllerBlock.IN_STRUCTURE, formed));
    }

    // structure info updates
    if (formed) {
      // sync size to the client
      TinkerNetwork.getInstance().sendToClientsAround(
        new StructureUpdatePacket(pos, newStructure.getMinPos(), newStructure.getMaxPos(), newStructure.getTanks()), world, pos);

      // set master positions
      newStructure.assignMaster(this, oldStructure);
      setStructure(newStructure);

      // update tank capability
      if (!fluidCapability.isPresent()) {
        fluidCapability = LazyOptional.of(() -> tank);
      }
    } else {
      if (oldStructure != null) {
        oldStructure.clearMaster(this);
      }
      setStructure(null);

      // update tank capability
      if (fluidCapability.isPresent()) {
        fluidCapability.invalidate();
        fluidCapability = LazyOptional.empty();
      }
    }

    // update the error position, we do on both success and failure for the sake of expanding positions
    updateErrorPos();

    // clear expand counter either way
    expandCounter = 0;
  }

  /**
   * Called when the controller is broken to invalidate the master in all servants
   */
  public void invalidateStructure() {
    if (structure != null) {
      structure.clearMaster(this);
      structure = null;
      errorPos = null;
    }
  }

  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos, BlockState state) {
    // structure invalid? can ignore this, will automatically check later
    if (structure == null) {
      return;
    }

    assert world != null;
    if (multiblock.shouldUpdate(world, structure, pos, state)) {
      updateStructure();
    }
  }

  /** Gets the last result from this multiblock */
  public MultiblockResult getStructureResult() {
    return multiblock.getLastResult();
  }

  /* Tank */

  @Override
  public void updateFluidsFromPacket(List<FluidStack> fluids) {
    tank.setFluids(fluids);
  }

  /**
   * Updates the fluid displayed in the block, only used client side
   * @param fluid  Fluid
   */
  private void updateDisplayFluid(Fluid fluid) {
    if (world != null && world.isRemote) {
      // update ourself
      modelData.setData(IDisplayFluidListener.PROPERTY, fluid);
      this.requestModelDataUpdate();
      BlockState state = getBlockState();
      world.notifyBlockUpdate(pos, state, state, 48);

      // update all listeners
      Iterator<WeakReference<IDisplayFluidListener>> iterator = fluidDisplayListeners.iterator();
      while (iterator.hasNext()) {
        IDisplayFluidListener listener = iterator.next().get();
        if (listener == null) {
          iterator.remove();
        } else {
          listener.notifyDisplayFluidUpdated(fluid);
        }
      }
    }
  }

  @Override
  public void addDisplayListener(IDisplayFluidListener listener) {
    fluidDisplayListeners.add(new WeakReference<>(listener));
    listener.notifyDisplayFluidUpdated(tank.getFluidInTank(0).getFluid());
  }

  @Override
  public void notifyFluidsChanged(FluidChange type, Fluid fluid) {
    if (type == FluidChange.ORDER_CHANGED) {
      updateDisplayFluid(fluid);
    } else {
      // mark that fluids need an update on the client
      fluidUpdateQueued = true;
      this.markDirtyFast();
    }
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    if (structure != null) {
      return structure.getBounds();
    } else if (defaultBounds == null) {
      defaultBounds = new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }
    return defaultBounds;
  }

  @Override
  public void setPos(BlockPos posIn) {
    super.setPos(posIn);
    defaultBounds = null;
  }

  @Override
  public void setWorldAndPos(World world, BlockPos pos) {
    super.setWorldAndPos(world, pos);
    defaultBounds = null;
  }

  /* Heating helpers */

  /**
   * Checks if we can melt entities
   * @return  True if we can melt entities
   */
  private boolean canMeltEntities() {
    if (fuelModule.hasFuel()) {
      return true;
    }
    return fuelModule.findFuel(false) > 0;
  }

  /**
   * Inserts an item into the inventory
   * @param stack  Stack to insert
   */
  private ItemStack insertIntoInventory(ItemStack stack) {
    return ItemHandlerHelper.insertItem(meltingInventory, stack, false);
  }


  /* UI and sync */

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    return new HeatingStructureContainer(id, inv, this);
  }

  /**
   * Sets the structure info on the client side
   * @param minPos  Min structure position
   * @param maxPos  Max structure position
   */
  public void setStructureSize(BlockPos minPos, BlockPos maxPos, List<BlockPos> tanks) {
    setStructure(multiblock.createClient(minPos, maxPos, tanks));
    fuelModule.clearCachedDisplayListeners();
    if (structure == null) {
      fluidDisplayListeners.clear();
    } else {
      fluidDisplayListeners.removeIf(reference -> {
        IDisplayFluidListener listener = reference.get();
        return listener == null || !structure.contains(listener.getListenerPos());
      });
    }
  }

  /** Updates the error position from the server */
  public void setErrorPos(@Nullable BlockPos errorPos) {
    this.errorPos = errorPos;
    if (errorPos != null && this.world != null) {
      // 10 seconds after its set
      this.errorVisibleFor = 200;
    }
  }

  /** If true, the error position should be visible */
  public boolean isHighlightError() {
    return errorVisibleFor > 0;
  }


  /* NBT */

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    if (nbt.contains(TAG_TANK, NBT.TAG_COMPOUND)) {
      tank.read(nbt.getCompound(TAG_TANK));
      Fluid first = tank.getFluidInTank(0).getFluid();
      if (first != Fluids.EMPTY) {
        updateDisplayFluid(first);
      }
    }
    if (nbt.contains(TAG_INVENTORY, NBT.TAG_COMPOUND)) {
      meltingInventory.readFromNBT(nbt.getCompound(TAG_INVENTORY));
    }
    if (nbt.contains(TAG_STRUCTURE, NBT.TAG_COMPOUND)) {
      setStructure(multiblock.readFromNBT(nbt.getCompound(TAG_STRUCTURE)));
      if (structure != null) {
        fluidCapability = LazyOptional.of(() -> tank);
      }
    }
    // only exists to be sent server to client in update packets
    if (nbt.contains(TAG_ERROR_POS, NBT.TAG_COMPOUND)) {
      this.errorPos = NBTUtil.readBlockPos(nbt.getCompound(TAG_ERROR_POS));
    }
    fuelModule.readFromNBT(nbt);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    // NBT that just writes to disk
    compound = super.write(compound);
    if (structure != null) {
      compound.put(TAG_STRUCTURE, structure.writeToNBT());
    }
    fuelModule.writeToNBT(compound);
    return compound;
  }

  @Override
  public void writeSynced(CompoundNBT compound) {
    // NBT that writes to disk and syncs to client
    super.writeSynced(compound);
    compound.put(TAG_TANK, tank.write(new CompoundNBT()));
    compound.put(TAG_INVENTORY, meltingInventory.writeToNBT());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // NBT that just syncs to client
    CompoundNBT nbt = super.getUpdateTag();
    if (structure != null) {
      nbt.put(TAG_STRUCTURE, structure.writeClientNBT());
    }
    // sync error position, not actually saved in NBT
    if (errorPos != null) {
      nbt.put(TAG_ERROR_POS, NBTUtil.writeBlockPos(errorPos));
    }
    return nbt;
  }
}
