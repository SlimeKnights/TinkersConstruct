package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.MelterBlock;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;

import javax.annotation.Nullable;
import java.util.Collections;

public class MelterTileEntity extends NamableTileEntity implements ITankTileEntity, ITickableTileEntity {
  /** Max capacity for the tank */
  private static final int TANK_CAPACITY = MaterialValues.METAL_BLOCK;
  /* tags */
  private static final String TAG_FUEL = "fuel";
  private static final String TAG_TEMPERATURE = "temperature";
  private static final String TAG_INVENTORY = "inventory";

  /* Tank */
  /** Internal fluid tank output */
  @Getter
  protected final FluidTankAnimated tank = new FluidTankAnimated(TANK_CAPACITY, this);
  /** Capability holder for the tank */
  private final LazyOptional<IFluidHandler> tankHolder = LazyOptional.of(() -> tank);
  /** Tank data for the model */
  private final IModelData modelData = new SinglePropertyData<>(ModelProperties.FLUID_TANK, tank);
  /** Last comparator strength to reduce block updates */
  private int lastStrength = -1;

  /** Internal tick counter */
  private int tick;

  /* Heating */
  /** Handles all the melting needs */
  @Getter
  private final MeltingModuleInventory meltingInventory = new MeltingModuleInventory(this, tank, Config.COMMON.melterNuggetsPerOre::get, 3);
  /** Capability holder for the tank */
  private final LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> meltingInventory);

  /** Fuel handling logic */
  @Getter
  private final FuelModule fuelModule = new FuelModule(this, () -> Collections.singletonList(this.pos.down()));

  /** Main constructor */
  public MelterTileEntity() {
    this(TinkerSmeltery.melter.get());
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected MelterTileEntity(TileEntityType<? extends MelterTileEntity> type) {
    super(type, new TranslationTextComponent(Util.makeTranslationKey("gui", "melter")));
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity playerEntity) {
    return new MelterContainer(id, inv, this);
  }

  /*
   * Tank methods
   */

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
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
  }

  @Override
  public int getLastStrength() {
    return lastStrength;
  }

  @Override
  public void setLastStrength(int strength) {
    lastStrength = strength;
  }

  @Override
  public IModelData getModelData() {
    return modelData;
  }

  /*
   * Melting
   */

  /** Checks if the tile entity is active */
  private boolean isFormed() {
    BlockState state = this.getBlockState();
    return state.hasProperty(MelterBlock.IN_STRUCTURE) && state.get(MelterBlock.IN_STRUCTURE);
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
  public void read(BlockState state, CompoundNBT tag) {
    super.read(state, tag);
    tank.readFromNBT(tag.getCompound(Tags.TANK));
    fuelModule.readFromNBT(tag);
    if (tag.contains(TAG_INVENTORY, NBT.TAG_COMPOUND)) {
      meltingInventory.readFromNBT(tag.getCompound(TAG_INVENTORY));
    }
  }

  @Override
  public void writeSynced(CompoundNBT tag) {
    tag.put(Tags.TANK, tank.writeToNBT(new CompoundNBT()));
    tag.put(TAG_INVENTORY, meltingInventory.writeToNBT());
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag = super.write(tag);
    fuelModule.writeToNBT(tag);
    return tag;
  }

  /*
   * Helpers
   */
  /** Checks if we are on a server world */
  private boolean isServerWorld() {
    return this.getWorld() != null && !this.getWorld().isRemote;
  }
}
