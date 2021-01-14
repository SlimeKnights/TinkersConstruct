package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.inventory.SmelteryContainer;
import slimeknights.tconstruct.smeltery.network.SmelteryStructureUpdatedPacket;
import slimeknights.tconstruct.smeltery.tileentity.module.AlloyingModule;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.module.SmelteryAlloyTank;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockSmeltery.StructureData;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class SmelteryTileEntity extends NamableTileEntity implements ITickableTileEntity, IMasterLogic, ISmelteryTankHandler {
  private static final String TAG_STRUCTURE = "structure";
  private static final String TAG_TANK = "tank";
  private static final String TAG_INVENTORY = "inventory";
  private static final int CAPACITY_PER_BLOCK = MaterialValues.VALUE_Ingot * 8;

  /** Sub module to detect the multiblock for this structure */
  private final MultiblockSmeltery multiblock = new MultiblockSmeltery(this);


  /* Saved data, written to NBT */
  /** Current structure contents */
  @Nullable
  private MultiblockSmeltery.StructureData structure;
  /** Tank instance for this smeltery */
  @Getter
  private final SmelteryTank tank = new SmelteryTank(this);
  /** Capability to pass to drains for fluid handling */
  @Getter
  private LazyOptional<IFluidHandler> fluidCapability = LazyOptional.empty();

  /** Inventory handling melting items */
  @Getter
  private final MeltingModuleInventory meltingInventory = new MeltingModuleInventory(this, tank);

  // TODO: update properly
  @Getter
  private final int temperature = 1000;

  /* Instance data, this data is not written to NBT */
  /** Timer to allow delaying actions based on number of ticks alive */
  private int tick = 0;
  /** Updates every second. Once it reaches 10, checks above the smeltery for a layer to see if we can expand up */
  private int expandCounter = 0;
  /** If true, structure will check for an update next tick */
  private boolean updateQueued = false;

  /** Module handling alloys */
  @Getter
  private final AlloyingModule alloyingModule = new AlloyingModule(this, tank, new SmelteryAlloyTank(tank));

  /* Capability */
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> meltingInventory);

  /* Misc helpers */
  /** Function to drop an item */
  private final Consumer<ItemStack> dropItem = this::dropItem;

  public SmelteryTileEntity() {
    super(TinkerSmeltery.smeltery.get(), new TranslationTextComponent(Util.makeTranslationKey("gui", "smeltery")));
  }

  @Override
  public void tick() {
    if (world == null || world.isRemote) {
      return;
    }

    // run structure update if requested
    if (updateQueued) {
      checkStructure();
      updateQueued = false;
    }

    // if we have a structure, run smeltery logic
    if (structure != null) {
      // every 15 seconds, check above the smeltery to try to expand
      if (tick == 0) {
        expandCounter++;
        if (expandCounter >= 10) {
          expandCounter = 0;
          // instead of rechecking the whole structure, just recheck the layer above and queue an update if its usable
          if (multiblock.canExpand(structure, world)) {
            queueUpdate();
          }
        }
      } else if (tick % 4 == 0) {
        // check the next inside position to see if its a valid inner block every other tick
        if (!multiblock.isInnerBlock(world, structure.getNextInsideCheck())) {
          queueUpdate();
        }
      }

      // run in four phases alternating each tick, so each thing runs once every 4 ticks
      switch (tick % 4) {
        // first tick, find fuel if needed
        case 0:
//          if (meltingInventory.canHeat() || alloyingModule.canAlloy()) {
//            // TODO: find fuel
//          }
          break;
          // second tick: melt items
        case 1:
          meltingInventory.heatItems(temperature);
          break;
          // third tick: alloy alloys
        case 2:
          alloyingModule.doAlloy();
          break;
          // fourth tick: consume fuel
        case 3:
          // TODO: consume fuel
          break;
      }
    } else if (tick == 0) {
      queueUpdate();
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
  public void queueUpdate() {
    updateQueued = true;
  }

  /**
   * Sets the structure and updates results of the new size
   * @param structure  New structure
   */
  private void setStructure(@Nullable StructureData structure) {
    this.structure = structure;
    if (structure != null) {
      int size = structure.getInternalSize();
      tank.setCapacity(CAPACITY_PER_BLOCK * size);
      meltingInventory.resize(size, dropItem);
    }
  }

  /**
   * Attempts to locate a valid smeltery structure
   */
  protected void checkStructure() {
    if (world == null || world.isRemote) {
      return;
    }

    // TODO: validate the block is correct?
    boolean wasActive = getBlockState().get(ControllerBlock.ACTIVE);
    StructureData oldStructure = structure;
    StructureData newStructure = multiblock.detectMultiblock(world, pos, getBlockState().get(BlockStateProperties.HORIZONTAL_FACING));

    // update block state
    boolean active = newStructure != null;
    if (active != wasActive) {
      world.setBlockState(pos, getBlockState().with(ControllerBlock.ACTIVE, active));
    }

    // structure info updates
    if (active) {
      newStructure.assignMaster(this, oldStructure);
      setStructure(newStructure);
      // sync size to the client
      TinkerNetwork.getInstance().sendToClientsAround(new SmelteryStructureUpdatedPacket(pos, newStructure.getMinPos(), newStructure.getMaxPos()), world, pos);

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
      queueUpdate();
    }
  }


  /* Tank */

  @Override
  public void updateFluidsFromPacket(List<FluidStack> fluids) {
    tank.setFluids(fluids);
  }

  @Override
  public void notifyFluidsChanged(FluidChange type, Fluid fluid) {
    // adding a new fluid means recipes that previously did not match might match now
    // can ignore removing a fluid as that is handled internally by the module
    if (type == FluidChange.ADDED) {
      alloyingModule.clearCachedRecipes();
    }
  }


  /* UI and sync */

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    return new SmelteryContainer(id, inv, this);
  }

  /**
   * Sets the structure info on the client side
   * @param minPos  Min structure position
   * @param maxPos  Max structure position
   */
  public void setStructureSize(BlockPos minPos, BlockPos maxPos) {
    setStructure(multiblock.createClient(minPos, maxPos));
  }


  /* NBT */

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    if (nbt.contains(TAG_TANK, NBT.TAG_COMPOUND)) {
      tank.read(nbt.getCompound(TAG_TANK));
    }
    if (nbt.contains(TAG_INVENTORY, NBT.TAG_COMPOUND)) {
      meltingInventory.readFromNBT(nbt.getCompound(TAG_INVENTORY));
    }
    if (nbt.contains(TAG_STRUCTURE, NBT.TAG_COMPOUND)) {
      structure = multiblock.readFromNBT(nbt.getCompound(TAG_STRUCTURE));
      if (structure != null) {
        fluidCapability = LazyOptional.of(() -> tank);
      }
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    // NBT that just writes to disk
    compound = super.write(compound);
    if (structure != null) {
      compound.put(TAG_STRUCTURE, structure.writeToNBT());
    }
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
    return nbt;
  }
}
