package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.client.util.SinglePropertyModelData;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloy.inventory.IAlloyInventory;
import slimeknights.tconstruct.library.recipe.alloy.inventory.TileAlloyingWrapper;
import slimeknights.tconstruct.library.recipe.alloy.recipe.IAlloyRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.smeltery.FluidHandlerDrainOnlyWrapper;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.AlloyTankBlock;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MelterFuelWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AlloyTankTileEntity extends MantleTileEntity implements ITankTileEntity, ITickableTileEntity {
  /** Max capacity for the tank */
  private static final int TANK_CAPACITY = FluidAttributes.BUCKET_VOLUME * 4;
  /* tags */
  private static final String TAG_FUEL = "fuel";
  private static final String TAG_TEMPERATURE = "temperature";

  /* Tank */
  /** Internal fluid tank output */
  @Getter
  protected final FluidTankAnimated tank = new FluidTankAnimated(TANK_CAPACITY, this);
  /** Capability holder for the tank */
  private final LazyOptional<FluidHandlerDrainOnlyWrapper> tankHolder = LazyOptional.of(() -> new FluidHandlerDrainOnlyWrapper(this, tank));
  private TileAlloyingWrapper alloyingWrapper;
  /** Tank data for the model */
  @Getter
  private final IModelData modelData;
  /** Last comparator strength to reduce block updates */
  @Getter
  private int lastStrength = -1;

  /* Heating */
  /** Internal tick counter */
  private int tick;
  /** Last recipe seen */
  private IAlloyRecipe lastRecipe;
  /** Potential input directions */
  private Set<Direction> tankDirections = EnumSet.complementOf(EnumSet.of(Direction.DOWN));
  private Map<FluidIngredient,Direction> recipeMap = Collections.emptyMap();
  /**
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
  public AlloyTankTileEntity() {
    this(TinkerSmeltery.alloy.get());
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected AlloyTankTileEntity(TileEntityType<? extends AlloyTankTileEntity> type) {
    super(type);
    this.lastRecipe = null;

    // alloying
    alloyingWrapper = new TileAlloyingWrapper(this, Fluids.EMPTY);
    // tank data
    modelData = new SinglePropertyModelData<>(tank, ModelProperties.FLUID_TANK);
  }

  /*
   * Tank methods
   */

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return tankHolder.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void setLastStrength(int strength) {
    lastStrength = strength;
  }


  /*
   * Alloying
   */

  @Override
  public void tick() {
    if (!isServerWorld()) {
      return;
    }

    // are we fully formed?
    if (isActive()) {
      if (tick % 4 == 0) {
        // returns true if we need fuel
        if (alloy()) {
          consumeFuel();
        }
      }
    }

    tick = (tick + 1) % 20;
  }

  /**
   * Alloys fluids from surrounding tanks
   * @return True if we need fuel
   */
  private boolean alloy() {
    boolean alloyed = false;

    alloyingWrapper = getAlloyInventory();
    if (alloyingWrapper != null && alloyingWrapper.isValid()) {
      lastRecipe = findRecipe(alloyingWrapper);
      if (lastRecipe != null) {
        System.out.println("lastRecipe " + lastRecipe.getId().toString());
      }
    }
    // if we alloyed, decrease fuel
    if (alloyed) {
      fuel--;
    }
    // no fuel needed
    return false;
  }

  /**
   * Finds an alloy recipe
   * @param inv Inventory instance
   * @return Alloy recipe found, or null if no match
   */
  @Nullable
  private IAlloyRecipe findRecipe(IAlloyInventory inv) {
    if (world == null) {
      return null;
    }
    // first, try last recipe
    if (lastRecipe != null && lastRecipe.matches(inv, world)) {
      return lastRecipe;
    }

    // if that fails, try to find a new recipe
    Optional<IAlloyRecipe> newRecipe = world.getRecipeManager().getRecipe(RecipeTypes.ALLOY, inv, world);
    if (newRecipe.isPresent()) {
      lastRecipe = newRecipe.get();
      return lastRecipe;
    }
    return null;
  }

  /**
   * Gets the alloying inventory for the alloy tank
   * @return Alloy inventory, fetching it if needed
   */
  @Nullable
  public TileAlloyingWrapper getAlloyInventory() {
    if (world != null && (alloyingWrapper == null || !alloyingWrapper.isValid())) {
      alloyingWrapper = new TileAlloyingWrapper(this, tank.getFluid().getFluid());
      for (Direction direction : tankDirections) {
        TileEntity te = world.getTileEntity(this.getPos().offset(direction));
        if (te instanceof IFluidTank) {
          alloyingWrapper.addTank(direction, (IFluidTank)te);
        }
      }
    }
    return alloyingWrapper;
  }
  /*
   * Fueling
   */
  /** Checks if we have fuel in the alloy tank */
  private boolean hasFuel() {
    return fuel > 0;
  }

  /**
   * Gets the fuel inventory for the alloy tank
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

  /** Consume fuel to power the alloy tank */
  private void consumeFuel() {
    // no need to consume fuel if we have fuel
    if (hasFuel()) {
      return;
    }

    // cannot consume if inactive
    if (!isActive()) {
      return;
    }

    // if no fuel inventory, fetch from tile below
    getFuelInventory();

    // find a new fuel
    if (fuelInventory != null) {
      MeltingFuel fuel = findMeltingFuel();
      if (fuel != null) {
        // store fuel stats
        temperature = fuel.getTemperature();
        this.fuel = fuelInventory.consumeFuel(fuel);
      }
    }
  }

  /**
   * Finds melting fuel for the current alloy tank
   * @return  Melting fuel recipe
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
    super.read(state, tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.put(Tags.TANK, tank.writeToNBT(new CompoundNBT()));
    tag.putInt(TAG_FUEL, fuel);
    tag.putInt(TAG_TEMPERATURE, temperature);
    return super.write(tag);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return this.write(new CompoundNBT());
  }

  /*
   * Helpers
   */
  /** Checks if we are on a server world */
  private boolean isServerWorld() {
    return  this.getWorld() != null && !this.getWorld().isRemote;
  }

  /** Checks if the tile entity is active */
  private boolean isActive() {
    BlockState state = this.getBlockState();
    return state.hasProperty(AlloyTankBlock.ACTIVE) && state.get(AlloyTankBlock.ACTIVE);
  }
}
