package slimeknights.tconstruct.smeltery.tileentity.module;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Module handling fuel consumption for the melter and smeltery
 */
@RequiredArgsConstructor
public class FuelModule implements IIntArray {
  /** Listener to attach to stored capability */
  private final NonNullConsumer<LazyOptional<IFluidHandler>> listener = new WeakConsumerWrapper<>(this, (self, cap) -> {
    self.fluidHandler = null;
    self.lastPos = null;
  });

  /** Parent TE */
  private final MantleTileEntity parent;
  /** Supplier for the list of valid tank positions */
  private final Supplier<Collection<BlockPos>> tankSupplier;

  /** Last fuel recipe used */
  @Nullable
  private MeltingFuel lastRecipe;
  /** Last fluid handler where fluid was extracted */
  @Nullable
  private LazyOptional<IFluidHandler> fluidHandler;
  /** Position of the last fluid handler */
  @Nullable
  private BlockPos lastPos = null;

  /** Current amount of fluid in the TE */
  private int fuel = 0;
  /** Temperature of the current fuel */
  @Getter
  private int temperature = 0;


  /*
   * Helpers
   */

  /** Gets a nonnull world instance from the parent */
  private World getWorld() {
    return Objects.requireNonNull(parent.getWorld(), "Parent tile entity has null world");
  }

  /**
   * Finds a recipe for the given fluid
   * @param fluid  Fluid
   * @return  Recipe
   */
  @Nullable
  private MeltingFuel findRecipe(Fluid fluid) {
    if (lastRecipe != null && lastRecipe.matches(fluid)) {
      return lastRecipe;
    }
    for (MeltingFuel recipe : RecipeHelper.getRecipes(getWorld().getRecipeManager(), RecipeTypes.FUEL, MeltingFuel.class)) {
      if (recipe.matches(fluid)) {
        lastRecipe = recipe;
        return recipe;
      }
    }
    return null;
  }


  /* Fuel attributes */

  /**
   * Checks if we have fuel
   * @return  True if we have fuel
   */
  public boolean hasFuel() {
    return fuel > 0;
  }

  /**
   * Consumes fuel from the module
   * @param amount  Amount of fuel to consume
   */
  public void decreaseFuel(int amount) {
    fuel = Math.max(0, fuel - amount);
  }


  /* Fuel updating */

  /** Cached lambda of following function as its used a lot */
  private final NonNullPredicate<IFluidHandler> tryConsumeFuel = this::tryConsumeFuel;

  /**
   * Trys to consume fuel from the given fluid handler
   * @param handler  Handler to consume fuel from
   * @return   True if fuel was consumed
   */
  private boolean tryConsumeFuel(IFluidHandler handler) {
    FluidStack fluid = handler.getFluidInTank(0);
    MeltingFuel recipe = findRecipe(fluid.getFluid());
    if (recipe != null) {
      int amount = recipe.getAmount(fluid.getFluid());
      if (fluid.getAmount() >= amount) {
        FluidStack drained = handler.drain(new FluidStack(fluid, amount), FluidAction.EXECUTE);
        if (drained.getAmount() != amount) {
          TConstruct.log.error("Invalid amount of fuel drained from tank");
        }
        fuel += recipe.getDuration();
        temperature = recipe.getTemperature();
        return true;
      }
    }
    return false;
  }

  /**
   * Tries to consume fuel from the given position
   * @param pos  Position
   * @return  True if fuel was consumed
   */
  private boolean tryConsumeFuel(BlockPos pos) {
    TileEntity te = getWorld().getTileEntity(pos);
    if (te != null) {
      // if we find a valid cap, try to consume fuel from it
      LazyOptional<IFluidHandler> capability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
      if (capability.filter(tryConsumeFuel).isPresent()) {
        fluidHandler = capability;
        capability.addListener(listener);
        lastPos = pos;
        return true;
      }
    }
    return false;
  }

  /**
   * Attempts to consume fuel from one of the tanks
   */
  public void findFuel() {
    // TODO: smeltery liked combining all tanks into one fuel info, is there an efficient way to do that?

    // if we have a handler, try to use that if possible
    if (fluidHandler != null) {
      if (fluidHandler.filter(tryConsumeFuel).isPresent()) {
        return;
      }
      // if no handler, try to find one at the last position
    } else if (lastPos != null && tryConsumeFuel(lastPos)) {
      return;
    }

    // find a new handler among our tanks
    for (BlockPos pos : tankSupplier.get()) {
      // already checked the last position above, no reason to try again
      if (!pos.equals(lastPos) && tryConsumeFuel(pos)) {
        return;
      }
    }

    // no handler found, tell client of the lack of fuel
    temperature = 0;
  }

  /* NBT */
  private static final String TAG_FUEL = "fuel";
  private static final String TAG_TEMPERATURE = "temperature";
  private static final String TAG_LAST_FUEL = "last_fuel_tank";

  /**
   * Reads the fuel from NBT
   * @param nbt  NBT to read from
   */
  public void readFromNBT(CompoundNBT nbt) {
    fuel = nbt.getInt(TAG_FUEL);
    temperature = nbt.getInt(TAG_TEMPERATURE);
    lastPos = TagUtil.readPos(nbt, TAG_LAST_FUEL);
  }

  /**
   * Writes the fuel to NBT
   * @param nbt  NBT to write to
   * @return  NBT written to
   */
  public CompoundNBT writeToNBT(CompoundNBT nbt) {
    nbt.putInt(TAG_FUEL, fuel);
    nbt.putInt(TAG_TEMPERATURE, temperature);
    return nbt;
  }

  /**
   * Writes the last position to NBT. Separate method as its synced in the smeltery and unused in the melter
   * @param nbt  NBT to write to
   * @return  NBT written to
   */
  public CompoundNBT writeLastPos(CompoundNBT nbt) {
    if (lastPos != null) {
      nbt.put(TAG_LAST_FUEL, TagUtil.writePos(lastPos));
    }
    return nbt;
  }


  /* UI syncing */
  private static final int FUEL = 0;
  private static final int TEMPERATURE = 1;
  private static final int LAST_X = 2;
  private static final int LAST_Y = 3;
  private static final int LAST_Z = 4;

  @Override
  public int size() {
    return 5;
  }

  @Override
  public int get(int index) {
    switch (index) {
      case FUEL:
        return fuel;
      case TEMPERATURE:
        return temperature;
      case LAST_X:
        return lastPos == null ? 0 : lastPos.getX();
      case LAST_Y:
        return lastPos == null ? 0 : lastPos.getY();
      case LAST_Z:
        return lastPos == null ? 0 : lastPos.getZ();
    }
    return 0;
  }

  @Override
  public void set(int index, int value) {
    switch (index) {
      case FUEL:
        fuel = value;
        break;
      case TEMPERATURE:
        temperature = value;
        break;
      case LAST_X:
      case LAST_Y:
      case LAST_Z:
        // position sync
        if (lastPos == null) lastPos = BlockPos.ZERO;
        switch (index) {
          case LAST_X:
            lastPos = new BlockPos(value, lastPos.getY(), lastPos.getZ());
            break;
          case LAST_Y:
            lastPos = new BlockPos(lastPos.getX(), value, lastPos.getZ());
            break;
          case LAST_Z:
            lastPos = new BlockPos(lastPos.getX(), lastPos.getY(), value);
            break;
        }
    }
  }

  /**
   * Updates the last position from the packet
   * @param lastPos  New last position
   */
  public void setFuelInfo(@Nullable BlockPos lastPos) {
    this.lastPos = lastPos;
    fluidHandler = null;
  }

  /**
   * Called client side to get the fuel info for the current tank
   * @return  Fuel info
   */
  public FuelInfo getFuelInfo() {
    // no last pos? no fuel
    if (lastPos == null) {
      return FuelInfo.EMPTY;
    }

    // fetch fluid handler
    if (fluidHandler == null) {
      TileEntity te = getWorld().getTileEntity(lastPos);
      if (te != null) {
        fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        fluidHandler.addListener(listener);
      } else {
        // TODO: how do we clear this cache?
        fluidHandler = LazyOptional.empty();
      }
    }

    return fluidHandler.map(handler -> new FuelInfo(handler.getFluidInTank(0), handler.getTankCapacity(0)))
                       .orElse(FuelInfo.EMPTY);
  }

  /** Data class to hold information about the current fuel */
  @Data
  public static class FuelInfo {
    /** Empty fuel instance */
    public static final FuelInfo EMPTY = new FuelInfo(FluidStack.EMPTY, 0);

    private final FluidStack fuel;
    private final int capacity;

    public boolean isEmpty() {
      return fuel.isEmpty() || capacity == 0;
    }
  }
}
