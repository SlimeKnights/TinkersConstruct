package slimeknights.tconstruct.smeltery.block.entity.controller;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.entity.NameableBlockEntity;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.SmelteryControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock.StructureData;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockResult;
import slimeknights.tconstruct.smeltery.block.entity.tank.IDisplayFluidListener;
import slimeknights.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;
import slimeknights.tconstruct.smeltery.network.StructureUpdatePacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class HeatingStructureBlockEntity extends NameableBlockEntity implements IMasterLogic, ISmelteryTankHandler {
  private static final String TAG_STRUCTURE = "structure";
  private static final String TAG_TANK = "tank";
  private static final String TAG_INVENTORY = "inventory";
  private static final String TAG_ERROR_POS = "errorPos";

  /** Ticker instance for the serverside */
  public static final BlockEntityTicker<HeatingStructureBlockEntity> SERVER_TICKER = (level, pos, state, self) -> self.serverTick(level, pos, state);
  /** Ticker instance for the clientside */
  public static final BlockEntityTicker<HeatingStructureBlockEntity> CLIENT_TICKER = (level, pos, state, self) -> self.clientTick(level, pos, state);

  /** Sub module to detect the multiblock for this structure */
  private final HeatingStructureMultiblock<?> multiblock = createMultiblock();

  /** Position of the block causing the structure to not form */
  @Nullable @Getter
  private BlockPos errorPos;
  /** Number of ticks the error will remain visible for */
  private int errorVisibleFor = 0;
  /** Temporary hack until forge fixes {@link #onLoad()}, do a first tick listener here as drains don't tick */
  private boolean addedDrainListeners = false;

  /* Saved data, written to Tag */
  /** Current structure contents */
  @Nullable @Getter
  protected StructureData structure;
  /** Tank instance for this smeltery */
  @Getter
  protected final SmelteryTank<HeatingStructureBlockEntity> tank = new SmelteryTank<>(this);
  /** Capability to pass to drains for fluid handling */
  @Getter
  private LazyOptional<IFluidHandler> fluidCapability = LazyOptional.empty();

  /** Inventory handling melting items */
  @Getter
  protected final MeltingModuleInventory meltingInventory = createMeltingInventory();

  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> meltingInventory);

  /** Fuel module */
  @Getter
  protected final FuelModule fuelModule = new FuelModule(this, () -> structure != null ? structure.getTanks() : Collections.emptyList());
  /** Current fuel consumption rate */
  protected int fuelRate = 1;


  /** Module handling entity interaction */
  protected final EntityMeltingModule entityModule = new EntityMeltingModule(this, tank, this::canMeltEntities, this::insertIntoInventory, () -> structure == null ? null : structure.getBounds());


  /* Instance data, this data is not written to Tag */
  /** Timer to allow delaying actions based on number of ticks alive */
  protected int tick = 0;
  /** Updates every second. Once it reaches 10, checks above the smeltery for a layer to see if we can expand up */
  private int expandCounter = 0;
  /** If true, structure will check for an update next tick */
  private boolean structureUpdateQueued = false;
  /** If true, fluids have changed since the last update and should be synced to the client, synced at most once every 4 ticks */
  private boolean fluidUpdateQueued = false;
  /** Cache of the bounds for the case of no structure */
  private AABB defaultBounds;

  /* Client display */
  @Getter
  private final IModelData modelData = new SinglePropertyData<>(IDisplayFluidListener.PROPERTY);
  private final List<WeakReference<IDisplayFluidListener>> fluidDisplayListeners = new ArrayList<>();

  /* Misc helpers */
  /** Function to drop an item */
  protected final Consumer<ItemStack> dropItem = this::dropItem;

  protected HeatingStructureBlockEntity(BlockEntityType<? extends HeatingStructureBlockEntity> type, BlockPos pos, BlockState state, Component name) {
    super(type, pos, state, name);
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
      TinkerNetwork.getInstance().sendToClientsAround(new StructureErrorPositionPacket(worldPosition, errorPos), level, worldPosition);
    }
  }

  /** Handles the client tick */
  protected void clientTick(Level level, BlockPos pos, BlockState state) {
    if (errorVisibleFor > 0) {
      errorVisibleFor--;
    }
    if (!addedDrainListeners) {
      addedDrainListeners = true;
      if (structure != null) {
        structure.forEachContained(sPos -> {
          if (level.getBlockEntity(sPos) instanceof IDisplayFluidListener listener) {
            fluidDisplayListeners.add(new WeakReference<>(listener));
          }
        });
        // if we have listeners and a fluid, send a first update
        if (!fluidDisplayListeners.isEmpty()) {
          FluidStack fluid = IDisplayFluidListener.normalizeFluid(tank.getFluidInTank(0));
          if (!fluid.isEmpty()) {
            updateListeners(fluid);
          }
        }
      }
    }
  }

  /** Handles the server tick */
  protected void serverTick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      if (errorVisibleFor > 0) {
        errorVisibleFor--;
      }
      return;
    }
    // invalid state, just a safety check in case its air somehow
    if (!state.hasProperty(ControllerBlock.IN_STRUCTURE)) {
      return;
    }

    // run structure update if requested
    if (structureUpdateQueued) {
      checkStructure();
      structureUpdateQueued = false;
    }

    // if we have a structure, run smeltery logic
    if (structure != null && state.getValue(SmelteryControllerBlock.IN_STRUCTURE)) {
      // every 15 seconds, check above the smeltery to try to expand
      if (tick == 0) {
        expandCounter++;
        if (expandCounter >= 10 && structure.getInnerY() < multiblock.getMaxHeight()) {
          expandCounter = 0;
          // instead of rechecking the whole structure, just recheck the layer above and queue an update if its usable
          if (multiblock.canExpand(structure, level)) {
            updateStructure();
          } else {
            updateErrorPos();
          }
        }
      } else if (tick % 4 == 0) {
        // check the next inside position to see if its a valid inner block every other tick
        if (!multiblock.isInnerBlock(level, structure.getNextInsideCheck())) {
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
   * Drops an item into the level
   * @param stack  Item to drop
   */
  protected void dropItem(ItemStack stack) {
    assert level != null;
    if (!level.isClientSide && !stack.isEmpty()) {
      double x = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
      double y = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
      double z = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
      BlockPos pos = this.worldPosition.relative(getBlockState().getValue(ControllerBlock.FACING));
      ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, stack);
      itementity.setDefaultPickUpDelay();
      level.addFreshEntity(itementity);
    }
  }


  /* Capability */

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    this.itemCapability.invalidate();
  }

  @Nonnull
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
    if (level == null || level.isClientSide) {
      return;
    }
    boolean wasFormed = getBlockState().getValue(ControllerBlock.IN_STRUCTURE);
    StructureData oldStructure = structure;
    StructureData newStructure = multiblock.detectMultiblock(level, worldPosition, getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));

    // update block state
    boolean formed = newStructure != null;
    if (formed != wasFormed) {
      level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ControllerBlock.IN_STRUCTURE, formed));
    }

    // structure info updates
    if (formed) {
      // sync size to the client
      TinkerNetwork.getInstance().sendToClientsAround(
        new StructureUpdatePacket(worldPosition, newStructure.getMinPos(), newStructure.getMaxPos(), newStructure.getTanks()), level, worldPosition);

      // update tank capability, do first for update listeners on the drain blocks
      if (!fluidCapability.isPresent()) {
        fluidCapability = LazyOptional.of(() -> tank);
      }

      // set master positions
      newStructure.assignMaster(this, oldStructure);
      setStructure(newStructure);
    } else {
      // remove tank capability
      if (fluidCapability.isPresent()) {
        fluidCapability.invalidate();
        fluidCapability = LazyOptional.empty();
      }

      // clear positions
      if (oldStructure != null) {
        oldStructure.clearMaster(this);
      }
      setStructure(null);
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

    assert level != null;
    if (multiblock.shouldUpdate(level, structure, pos, state)) {
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

  /** Updates all fluid display listeners */
  private void updateListeners(FluidStack fluid) {
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

  /**
   * Updates the fluid displayed in the block, only used client side
   * @param fluid  Fluid
   */
  private void updateDisplayFluid(FluidStack fluid) {
    if (level != null && level.isClientSide) {
      // update ourself
      fluid = IDisplayFluidListener.normalizeFluid(fluid);
      modelData.setData(IDisplayFluidListener.PROPERTY, fluid);
      this.requestModelDataUpdate();
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 48);
      updateListeners(fluid);
    }
  }

  @Override
  public void addDisplayListener(IDisplayFluidListener listener) {
    boolean have = false;
    for (WeakReference<IDisplayFluidListener> existing : fluidDisplayListeners) {
      if (existing.get() == listener) {
        have = true;
        break;
      }
    }
    if (!have) {
      fluidDisplayListeners.add(new WeakReference<>(listener));
    }
    listener.notifyDisplayFluidUpdated(IDisplayFluidListener.normalizeFluid(tank.getFluidInTank(0)));
  }

  @Override
  public void notifyFluidsChanged(FluidChange type, FluidStack fluid) {
    if (type == FluidChange.ORDER_CHANGED) {
      updateDisplayFluid(fluid);
    } else {
      // mark that fluids need an update on the client
      fluidUpdateQueued = true;
      this.setChangedFast();
    }
  }

  @Override
  public AABB getRenderBoundingBox() {
    if (structure != null) {
      return structure.getBounds();
    } else if (defaultBounds == null) {
      defaultBounds = new AABB(worldPosition, worldPosition.offset(1, 1, 1));
    }
    return defaultBounds;
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
  public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
    return new HeatingStructureContainerMenu(id, inv, this);
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
    if (errorPos != null && this.level != null) {
      // 10 seconds after its set
      this.errorVisibleFor = 200;
    }
  }

  /** If true, the error position should be visible */
  public boolean isHighlightError() {
    return errorVisibleFor > 0;
  }

  /** If true, the given item triggers debug blocks */
  protected abstract boolean isDebugItem(ItemStack stack);

  /** If true, debug blocks should show in the TESR to the given player */
  public boolean showDebugBlockBorder(Player player) {
    return isDebugItem(player.getMainHandItem())
           || isDebugItem(player.getOffhandItem())
           || isDebugItem(player.getItemBySlot(EquipmentSlot.HEAD));
  }


  /* Tag */

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    if (nbt.contains(TAG_TANK, Tag.TAG_COMPOUND)) {
      tank.read(nbt.getCompound(TAG_TANK));
      FluidStack first = tank.getFluidInTank(0);
      if (!first.isEmpty()) {
        updateDisplayFluid(first);
      }
    }
    if (nbt.contains(TAG_INVENTORY, Tag.TAG_COMPOUND)) {
      meltingInventory.readFromTag(nbt.getCompound(TAG_INVENTORY));
    }
    if (nbt.contains(TAG_STRUCTURE, Tag.TAG_COMPOUND)) {
      setStructure(multiblock.readFromTag(nbt.getCompound(TAG_STRUCTURE)));
      if (structure != null) {
        fluidCapability = LazyOptional.of(() -> tank);
      }
    }
    // only exists to be sent server to client in update packets
    if (nbt.contains(TAG_ERROR_POS, Tag.TAG_COMPOUND)) {
      this.errorPos = NbtUtils.readBlockPos(nbt.getCompound(TAG_ERROR_POS));
    }
    fuelModule.readFromTag(nbt);
  }

  @Override
  public void saveAdditional(CompoundTag compound) {
    // Tag that just writes to disk
    super.saveAdditional(compound);
    if (structure != null) {
      compound.put(TAG_STRUCTURE, structure.writeToTag());
    }
    fuelModule.writeToTag(compound);
  }

  @Override
  public void saveSynced(CompoundTag compound) {
    // Tag that writes to disk and syncs to client
    super.saveSynced(compound);
    compound.put(TAG_TANK, tank.write(new CompoundTag()));
    compound.put(TAG_INVENTORY, meltingInventory.writeToTag());
  }

  @Override
  public CompoundTag getUpdateTag() {
    // Tag that just syncs to client
    CompoundTag nbt = super.getUpdateTag();
    if (structure != null) {
      nbt.put(TAG_STRUCTURE, structure.writeClientTag());
    }
    // sync error position, not actually saved in Tag
    if (errorPos != null) {
      nbt.put(TAG_ERROR_POS, NbtUtils.writeBlockPos(errorPos));
    }
    return nbt;
  }


  /* Helpers */


  /** Handles the unchecked cast for a block entity ticker */
  @Nullable
  public static <HAVE extends HeatingStructureBlockEntity, RET extends BlockEntity> BlockEntityTicker<RET> getTicker(Level level, BlockEntityType<RET> expected, BlockEntityType<HAVE> have) {
    return BlockEntityHelper.castTicker(expected, have, level.isClientSide ? CLIENT_TICKER : SERVER_TICKER);
  }
}
