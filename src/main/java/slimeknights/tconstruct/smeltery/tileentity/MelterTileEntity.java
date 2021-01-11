package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
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
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.MelterBlock;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MelterFuelWrapper;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MeltingModuleInventory;

import javax.annotation.Nullable;
import java.util.Optional;

public class MelterTileEntity extends NamableTileEntity implements ITankTileEntity, ITickableTileEntity {
  /** Max capacity for the tank */
  private static final int TANK_CAPACITY = MaterialValues.VALUE_Block;
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

  /* Heating */
  /** Internal tick counter */
  private int tick;
  /** Handles all the melting needs */
  @Getter
  private final MeltingModuleInventory meltingInventory = new MeltingModuleInventory(this, tank, 3);
  /** Capability holder for the tank */
  private final LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> meltingInventory);

  /* Fuel */
  /** Fluid inventory to find new fuels */
  @Nullable
  private MelterFuelWrapper fuelInventory;
  /** Cache of the last fuel we tried to fetch */
  @Nullable
  private MeltingFuel lastFuel;
  /** Temperature of current fuel */
  @Getter @Setter
  private int temperature;
  /** Number of ticks of fuel left */
  @Getter @Setter
  private int fuel;

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

  @Override
  public void tick() {
    if(!isServerWorld()) {
      return;
    }

    // are we fully formed?
    if (isActive()) {
      if(tick % 4 == 0) {
        // try to consume fuel if needed
        if (!hasFuel() && meltingInventory.canHeat()) {
          consumeFuel();
        }

        // progress is reversed if no items
        if (hasFuel()) {
          meltingInventory.heatItems(temperature);
          fuel--;
        } else {
          meltingInventory.coolItems();
        }

      }
    }

    tick = (tick + 1) % 20;
  }

  /*
   * Fueling
   */
  /** Checks if we have fuel in the melter */
  private boolean hasFuel() {
    return fuel > 0;
  }

  /**
   * Gets the fuel inventory for the melter
   * @return  Fuel inventory, fetching it if needed
   */
  @Nullable
  public MelterFuelWrapper getFuelInventory() {
    if (world != null && (fuelInventory == null || !fuelInventory.isValid())) {
      TileEntity te = world.getTileEntity(this.getPos().down());
      if (te instanceof ITankTileEntity) {
        fuelInventory = new MelterFuelWrapper(((ITankTileEntity)te).getTank());
      } else {
        fuelInventory = null;
      }
    }
    return fuelInventory;
  }

  /** Consumes fuel to power the melter */
  private void consumeFuel() {
    // no need to consume fuel if we have fuel
    if (hasFuel()) {
      return;
    }

    // cannot consume if inactive
    if (!isActive()) {
      return;
    }

    // if no fluid inventory, fetch from tile below
    getFuelInventory();

    // find a new fuel
    if (fuelInventory != null) {
      MeltingFuel fuel = findMeltingFuel();
      if (fuel != null) {
        // store fuel stats
        temperature = fuel.getTemperature();
        this.fuel = fuelInventory.consumeFuel(fuel);
        // TODO: syncing, what does the client need? can the UI do it?
      }
    }
  }

  /**
   * Finds melting fuel for the current melter
   * @return Melting fuel recipe
   */
  @Nullable
  public MeltingFuel findMeltingFuel() {
    if (fuelInventory == null || world == null) {
      return null;
    }
    // try last fuel for a match
    if (lastFuel != null && lastFuel.matches(fuelInventory, world)) {
      return lastFuel;
    }
    // if no match, find a new fuel
    Optional<MeltingFuel> newFuel = world.getRecipeManager().getRecipe(RecipeTypes.FUEL, fuelInventory, world);
    if (newFuel.isPresent()) {
      // update last
      lastFuel = newFuel.get();
      return lastFuel;
    }
    // no fuel found
    return null;
  }

  /*
   * NBT
   */

  @Override
  public void read(BlockState state, CompoundNBT tag) {
    tank.readFromNBT(tag.getCompound(Tags.TANK));
    this.fuel = tag.getInt(TAG_FUEL);
    this.temperature = tag.getInt(TAG_TEMPERATURE);
    if (tag.contains(TAG_INVENTORY, NBT.TAG_COMPOUND)) {
      meltingInventory.readFromNBT(tag.getCompound(TAG_INVENTORY));
    }
    super.read(state, tag);
  }

  @Override
  public void writeSynced(CompoundNBT tag) {
    tag.put(Tags.TANK, tank.writeToNBT(new CompoundNBT()));
    tag.put(TAG_INVENTORY, meltingInventory.writeToNBT());
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.putInt(TAG_FUEL, fuel);
    tag.putInt(TAG_TEMPERATURE, temperature);
    return super.write(tag);
  }

  /*
   * Helpers
   */
  /** Checks if we are on a server world */
  private boolean isServerWorld() {
    return this.getWorld() != null && !this.getWorld().isRemote;
  }

  /** Checks if the tile entity is active */
  private boolean isActive() {
    BlockState state = this.getBlockState();
    return state.hasProperty(MelterBlock.ACTIVE) && state.get(MelterBlock.ACTIVE);
  }
}
