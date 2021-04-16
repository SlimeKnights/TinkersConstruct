package slimeknights.tconstruct.smeltery.tileentity;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import lombok.Getter;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.common.config.TConfig;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.misc.IItemHandler;
import slimeknights.tconstruct.misc.ItemHandlerHelper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.inventory.SmelteryContainer;
import slimeknights.tconstruct.smeltery.network.SmelteryStructureUpdatedPacket;
import slimeknights.tconstruct.smeltery.tileentity.module.AlloyingModule;
import slimeknights.tconstruct.smeltery.tileentity.module.EntityMeltingModule;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.fluids.IFluidHandler;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.module.SmelteryAlloyTank;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockSmeltery.StructureData;
import slimeknights.tconstruct.smeltery.tileentity.tank.IDisplayFluidListener;
import slimeknights.tconstruct.smeltery.tileentity.tank.ISmelteryTankHandler;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SmelteryTileEntity extends NamableTileEntity implements Tickable, IMasterLogic, ISmelteryTankHandler, ExtendedScreenHandlerFactory {
  private static final String TAG_STRUCTURE = "structure";
  private static final String TAG_TANK = "tank";
  private static final String TAG_INVENTORY = "inventory";

  /** Fluid capacity per internal block */
  private static final int CAPACITY_PER_BLOCK = MaterialValues.INGOT.mul(8).asInt(1000);
  /** Number of wall blocks needed to increase the fuel cost by 1 */
  private static final int BLOCKS_PER_FUEL = 10;

  /** Sub module to detect the multiblock for this structure */
  private final MultiblockSmeltery multiblock = new MultiblockSmeltery(this);


  /* Saved data, written to NBT */
  /** Current structure contents */
  @Nullable
  private MultiblockSmeltery.StructureData structure;
  /** Tank instance for this smeltery */
  private final SmelteryTank tank = new SmelteryTank(this);
  /** Capability to pass to drains for fluid handling */
  private Optional<IFluidHandler> fluidCapability = Optional.empty();

  /** Inventory handling melting items */
  public final MeltingModuleInventory meltingInventory = new MeltingModuleInventory(this, tank, () -> TConfig.common.smelteryNuggetsPerOre);

  /** Fuel module */
  private final FuelModule fuelModule = new FuelModule(this, () ->  structure != null ? structure.getTanks() : Collections.emptyList());
  /** Current fuel consumption rate */
  private int fuelRate = 1;


  /* Instance data, this data is not written to NBT */
  /** Timer to allow delaying actions based on number of ticks alive */
  private int tick = 0;
  /** Updates every second. Once it reaches 10, checks above the smeltery for a layer to see if we can expand up */
  private int expandCounter = 0;
  /** If true, structure will check for an update next tick */
  private boolean structureUpdateQueued = false;
  /** If true, fluids have changed since the last update and should be synced to the client, synced at most once every 4 ticks */
  private boolean fluidUpdateQueued = false;
  /** Cache of the bounds for the case of no structure */
  private Box defaultBounds;

  /** Module handling alloys */
  private final SmelteryAlloyTank alloyTank = new SmelteryAlloyTank(tank);

  private final AlloyingModule alloyingModule = new AlloyingModule(this, tank, alloyTank);
  /** Module handling entity interaction */
  private final EntityMeltingModule entityModule = new EntityMeltingModule(this, tank, this::canMeltEntities, this::insertIntoInventory, () -> structure == null ? null : structure.getBounds());

  /* Client display */
  private final List<WeakReference<IDisplayFluidListener>> fluidDisplayListeners = new ArrayList<>();

  /* Misc helpers */
  /** Function to drop an item */
  private final Consumer<ItemStack> dropItem = this::dropItem;

  public SmelteryTileEntity() {
    super(TinkerSmeltery.smeltery, new TranslatableText(Util.makeTranslationKey("gui", "smeltery")));
  }

  @Override
  public void tick() {
    if (world == null || world.isClient) {
      return;
    }

    // run structure update if requested
    if (structureUpdateQueued) {
      checkStructure();
      structureUpdateQueued = false;
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
            updateStructure();
          }
        }
      } else if (tick % 4 == 0) {
        // check the next inside position to see if its a valid inner block every other tick
        if (!multiblock.isInnerBlock(world, structure.getNextInsideCheck())) {
          updateStructure();
        }
      }

      // the next set of behaviors all require fuel, skip if no tanks
      if (structure.hasTanks()) {
        // every second, interact with entities, will consume fuel if needed
        boolean entityMelted = false;
        if (tick == 12) {
          entityMelted = entityModule.interactWithEntities();
        }

        // run in four phases alternating each tick, so each thing runs once every 4 ticks
        switch (tick % 4) {
          // first tick, find fuel if needed
          case 0:
            if (!fuelModule.hasFuel()) {
              // if we melted something already, we need fuel
              if (entityMelted) {
                fuelModule.findFuel(true);
              } else {
                // both alloying and melting need to know the temperature
                int possibleTemp = fuelModule.findFuel(false);
                alloyTank.setTemperature(possibleTemp);
                if (meltingInventory.canHeat(possibleTemp) || alloyingModule.canAlloy()) {
                  fuelModule.findFuel(true);
                }
              }
            }
            break;
            // second tick: melt items
          case 1:
            if (fuelModule.hasFuel()) {
              meltingInventory.heatItems(fuelModule.getTemperature());
            } else {
              meltingInventory.coolItems();
            }
            break;
            // third tick: alloy alloys
          case 2:
            if (fuelModule.hasFuel()) {
              alloyTank.setTemperature(fuelModule.getTemperature());
              alloyingModule.doAlloy();
            }
            break;
            // fourth tick: consume fuel, update fluids
          case 3:
            fuelModule.decreaseFuel(fuelRate);
            break;
        }
      }
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
    if (!world.isClient && !stack.isEmpty()) {
      double x = (double)(world.random.nextFloat() * 0.5F) + 0.25D;
      double y = (double)(world.random.nextFloat() * 0.5F) + 0.25D;
      double z = (double)(world.random.nextFloat() * 0.5F) + 0.25D;
      BlockPos pos = this.pos.offset(getCachedState().get(ControllerBlock.FACING));
      ItemEntity itementity = new ItemEntity(world, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, stack);
      itementity.setToDefaultPickupDelay();
      world.spawnEntity(itementity);
    }
  }


/*  *//* Capability *//*

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    this.itemCapability.invalidate();
  }

  @Override
  public <T> Optional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return itemCapability.cast();
    }
    return super.getCapability(capability, facing);
  }*/


  /* Structure */

  /**
   * Marks the smeltery for a structure check
   */
  public void updateStructure() {
    structureUpdateQueued = true;
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
      // fuel rate: every 10 blocks in the wall makes the fuel cost 1 more
      fuelRate = 1 + structure.getPerimeterCount() / BLOCKS_PER_FUEL;
    }
  }

  /**
   * Attempts to locate a valid smeltery structure
   */
  protected void checkStructure() {
    if (world == null || world.isClient) {
      return;
    }

    // TODO: validate the block is correct?
    boolean wasActive = getCachedState().get(ControllerBlock.ACTIVE);
    StructureData oldStructure = structure;
    StructureData newStructure = multiblock.detectMultiblock(world, pos, getCachedState().get(Properties.HORIZONTAL_FACING));

    // update block state
    boolean active = newStructure != null;
    if (active != wasActive) {
      world.setBlockState(pos, getCachedState().with(ControllerBlock.ACTIVE, active));
    }

    // structure info updates
    if (active) {
      // sync size to the client
      TinkerNetwork.getInstance().sendToClientsAround(
        new SmelteryStructureUpdatedPacket(pos, newStructure.getMinPos(), newStructure.getMaxPos(), newStructure.getTanks()), world, pos);

      // set master positions
      newStructure.assignMaster(this, oldStructure);
      setStructure(newStructure);

      // update tank capability
      if (!fluidCapability.isPresent()) {
        fluidCapability = Optional.of(tank);
      }
    } else {
      if (oldStructure != null) {
        oldStructure.clearMaster(this);
      }
      setStructure(null);

      // update tank capability
      if (fluidCapability.isPresent()) {
//        fluidCapability.invalidate();
        fluidCapability = Optional.empty();
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
  public BlockEntity getTileEntity() {
    return IMasterLogic.super.getTileEntity();
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


  /* Tank */

  @Override
  public void updateFluidsFromPacket(List<FluidVolume> fluids) {
    tank.setFluids(fluids);
  }

  /**
   * Updates the fluid displayed in the block, only used client side
   * @param fluid  Fluid
   */
  private void updateDisplayFluid(Fluid fluid) {
    if (world != null && world.isClient) {
      // update ourself
//      modelData.setData(IDisplayFluidListener.PROPERTY, fluid);
//      this.requestModelDataUpdate();
      BlockState state = getCachedState();
      world.updateListeners(pos, state, state, 48);

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
    listener.notifyDisplayFluidUpdated(tank.getFluidInTank(0).getRawFluid());
  }

  @Override
  public void notifyFluidsChanged(FluidChange type, Fluid fluid) {
    if (type == FluidChange.ORDER_CHANGED) {
      updateDisplayFluid(fluid);
    } else {
      // adding a new fluid means recipes that previously did not match might match now
      // can ignore removing a fluid as that is handled internally by the module
      if (type == FluidChange.ADDED) {
        alloyingModule.clearCachedRecipes();
      }

      // mark that fluids need an update on the client
      fluidUpdateQueued = true;
      this.markDirtyFast();
    }
  }

//  @Override
//  public Box getRenderBoundingBox() {
//    if (structure != null) {
//      return structure.getBounds();
//    } else if (defaultBounds == null) {
//      defaultBounds = new Box(pos, pos.add(1, 1, 1));
//    }
//    return defaultBounds;
//  }


  /* Heating helpers */

  /**
   * Checks if we can melt entities
   * @return  True if we can melt entities
   */
  private boolean canMeltEntities() {
    if (tank.getContained() > 0) {
      if (fuelModule.hasFuel()) {
        return true;
      }
      return fuelModule.findFuel(false) > 0;
    }
    return false;
  }

  /**
   * Inserts an item into the inventory
   * @param stack  Stack to insert
   */
  private ItemStack insertIntoInventory(ItemStack stack) {
    return insertItem(meltingInventory, stack, false);
  }

  public static ItemStack insertItem(IItemHandler dest, ItemStack stack, boolean simulate) {
    if (dest == null || stack.isEmpty())
      return stack;

    for (int i = 0; i < dest.getSlots(); i++)
    {
      stack = dest.insertItem(i, stack, simulate);
      if (stack.isEmpty())
      {
        return ItemStack.EMPTY;
      }
    }

    return stack;
  }


  /* UI and sync */

  @Nullable
  @Override
  public ScreenHandler createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    return new SmelteryContainer(id, inv, this);
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


  /* NBT */

  @Override
  public void fromTag(BlockState state, CompoundTag nbt) {
    super.fromTag(state, nbt);
    if (nbt.contains(TAG_TANK, NbtType.COMPOUND)) {
      tank.read(nbt.getCompound(TAG_TANK));
      Fluid first = tank.getFluidInTank(0).getRawFluid();
      if (first != Fluids.EMPTY) {
        updateDisplayFluid(first);
      }
    }
    if (nbt.contains(TAG_INVENTORY, NbtType.COMPOUND)) {
      meltingInventory.readFromNBT(nbt.getCompound(TAG_INVENTORY));
    }
    if (nbt.contains(TAG_STRUCTURE, NbtType.COMPOUND)) {
      setStructure(multiblock.readFromNBT(nbt.getCompound(TAG_STRUCTURE)));
      if (structure != null) {
        fluidCapability = Optional.of(tank);
      }
    }
    fuelModule.readFromNBT(nbt);
  }

  @Override
  public CompoundTag toTag(CompoundTag compound) {
    // NBT that just writes to disk
    compound = super.toTag(compound);
    if (structure != null) {
      compound.put(TAG_STRUCTURE, structure.writeToNBT());
    }
    fuelModule.writeToNBT(compound);
    return compound;
  }

  @Override
  public void writeSynced(CompoundTag compound) {
    // NBT that writes to disk and syncs to client
    super.writeSynced(compound);
    compound.put(TAG_TANK, tank.write(new CompoundTag()));
    compound.put(TAG_INVENTORY, meltingInventory.writeToNBT());
  }

  @Override
  public CompoundTag toInitialChunkDataTag() {
    // NBT that just syncs to client
    CompoundTag nbt = super.toInitialChunkDataTag();
    if (structure != null) {
      nbt.put(TAG_STRUCTURE, structure.writeClientNBT());
    }
    return nbt;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    throw new RuntimeException("CRAB!");
    //TODO: PORT
  }

  @Override
  public Text getName() {
    return super.getName();
  }

  @Override
  public boolean hasCustomName() {
    return super.hasCustomName();
  }

  @Override
  public Text getDisplayName() {
    return super.getDisplayName();
  }

  public @Nullable StructureData getStructure() {
    return this.structure;
  }

  public SmelteryTank getTank() {
    return this.tank;
  }

  public IFluidHandler getFluidCapability() {
    return this.fluidCapability.get();
  }

  public MeltingModuleInventory getMeltingInventory() {
    return this.meltingInventory;
  }

  public FuelModule getFuelModule() {
    return this.fuelModule;
  }

  public AlloyingModule getAlloyingModule() {
    return this.alloyingModule;
  }

  @Override
  public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
    buf.writeBlockPos(this.getPos());
  }
}
