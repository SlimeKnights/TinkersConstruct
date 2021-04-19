package slimeknights.tconstruct.smeltery.tileentity;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.model.IModelData;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.TConfig;
import slimeknights.tconstruct.fluids.IFluidHandler;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.client.model.SinglePropertyData;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.MelterBlock;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;

import java.util.Collections;

public class MelterTileEntity extends NamableTileEntity implements ITankTileEntity, Tickable {
  /** Max capacity for the tank */
  private static final FluidAmount TANK_CAPACITY = MaterialValues.METAL_BLOCK;
  /* tags */
  private static final String TAG_FUEL = "fuel";
  private static final String TAG_TEMPERATURE = "temperature";
  private static final String TAG_INVENTORY = "inventory";

  /* Tank */
  /** Internal fluid tank output */
  protected final FluidTankAnimated tank = new FluidTankAnimated(TANK_CAPACITY, this);
  /** Last comparator strength to reduce block updates */
  private int lastStrength = -1;

  /** Internal tick counter */
  private int tick;

  /* Heating */
  /** Handles all the melting needs */
  private final MeltingModuleInventory meltingInventory = new MeltingModuleInventory(this, (IFluidHandler) tank, () -> TConfig.common.melterNuggetsPerOre, 3);

  /** Fuel handling logic */
  private final FuelModule fuelModule = new FuelModule(this, () -> Collections.singletonList(this.pos.down()));

  /** Main constructor */
  public MelterTileEntity() {
    this(TinkerSmeltery.melter);
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected MelterTileEntity(BlockEntityType<? extends MelterTileEntity> type) {
    super(type, new TranslatableText(Util.makeTranslationKey("gui", "melter")));
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int id, PlayerInventory inv, PlayerEntity playerEntity) {
    return new MelterContainer(id, inv, this);
  }

  /*
   * Tank methods
   */

/*  @Override
  public <T> Optional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return tankHolder.cast();
    }
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return inventoryHolder.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    this.tankHolder.invalidate();
    this.inventoryHolder.invalidate();
  }*/

  @Override
  public int getLastStrength() {
    return lastStrength;
  }

  @Override
  public void setLastStrength(int strength) {
    lastStrength = strength;
  }

  /*
   * Melting
   */

  /** Checks if the tile entity is active */
  private boolean isFormed() {
    BlockState state = this.getCachedState();
    return state.contains(MelterBlock.IN_STRUCTURE) && state.get(MelterBlock.IN_STRUCTURE);
  }

  @Override
  public void tick() {
    if(!isServerWorld()) {
      return;
    }

    // are we fully formed?
    if (isFormed()) {

      switch (tick) {
        // tick 0: find fuel
        case 0:
          if (!fuelModule.hasFuel() && meltingInventory.canHeat(fuelModule.findFuel(false))) {
            fuelModule.findFuel(true);
          }
        // tick 2: heat items and consume fuel
        case 2: {
          assert world != null;
          BlockState state = getBlockState();
          boolean hasFuel = fuelModule.hasFuel();
          // update the active state
          if (state.get(ControllerBlock.ACTIVE) != hasFuel) {
            world.setBlockState(pos, state.with(ControllerBlock.ACTIVE, hasFuel));
            // update the heater below
            BlockPos down = pos.down();
            BlockState downState = world.getBlockState(down);
            if (TinkerTags.Blocks.MELTER_TANKS.contains(downState.getBlock()) && downState.hasProperty(ControllerBlock.ACTIVE) && downState.get(ControllerBlock.ACTIVE) != hasFuel) {
              world.setBlockState(down, downState.with(ControllerBlock.ACTIVE, hasFuel));
            }
          }
          // heat items
          if (hasFuel) {
            meltingInventory.heatItems(fuelModule.getTemperature());
            fuelModule.decreaseFuel(1);
          } else {
            meltingInventory.coolItems();
          }
        }
      }
      tick = (tick + 1) % 4;
    }
  }


  /*
   * NBT
   */

  @Override
  public void fromTag(BlockState state, CompoundTag tag) {
    super.fromTag(state, tag);
    tank.readFromNBT(tag.getCompound(Tags.TANK));
    fuelModule.readFromNBT(tag);
    if (tag.contains(TAG_INVENTORY, NbtType.COMPOUND)) {
      meltingInventory.readFromNBT(tag.getCompound(TAG_INVENTORY));
    }
  }

  @Override
  public void writeSynced(CompoundTag tag) {
    tag.put(Tags.TANK, tank.writeToNBT(new CompoundTag()));
    tag.put(TAG_INVENTORY, meltingInventory.writeToNBT());
  }

  @Override
  public CompoundTag toTag(CompoundTag tag) {
    tag = super.toTag(tag);
    fuelModule.writeToNBT(tag);
    return tag;
  }

  /*
   * Helpers
   */
  /** Checks if we are on a server world */
  private boolean isServerWorld() {
    return this.getWorld() != null && !this.getWorld().isClient;
  }

  public FluidTankAnimated getTank() {
    return this.tank;
  }

  public MeltingModuleInventory getMeltingInventory() {
    return this.meltingInventory;
  }

  public FuelModule getFuelModule() {
    return this.fuelModule;
  }
}
