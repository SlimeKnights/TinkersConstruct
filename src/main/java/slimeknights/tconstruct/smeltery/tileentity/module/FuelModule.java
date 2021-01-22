package slimeknights.tconstruct.smeltery.tileentity.module;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Module handling fuel consumption for the melter and smeltery
 */
@RequiredArgsConstructor
public class FuelModule implements IIntArray {
  /** Block position that will never be valid in world, used for sync */
  private static final BlockPos NULL_POS = new BlockPos(0, -1, 0);
  /** Temperature used for solid fuels, hot enough to melt iron */
  private static final int SOLID_TEMPERATURE = 800;

  /** Listener to attach to stored capability */
  private final NonNullConsumer<LazyOptional<IFluidHandler>> fluidListener = new WeakConsumerWrapper<>(this, (self, cap) -> self.reset());
  private final NonNullConsumer<LazyOptional<IItemHandler>> itemListener = new WeakConsumerWrapper<>(this, (self, cap) -> self.reset());

  /** Parent TE */
  private final MantleTileEntity parent;
  /** Supplier for the list of valid tank positions */
  private final Supplier<List<BlockPos>> tankSupplier;

  /** Last fuel recipe used */
  @Nullable
  private MeltingFuel lastRecipe;
  /** Last fluid handler where fluid was extracted */
  @Nullable
  private LazyOptional<IFluidHandler> fluidHandler;
  /** Last item handler where items were extracted */
  @Nullable
  private LazyOptional<IItemHandler> itemHandler;
  /** Position of the last fluid handler */
  @Nullable
  private BlockPos lastPos = null;


  /** Client fuel display */
  private List<LazyOptional<IFluidHandler>> tankDisplayHandlers;
  /** Listener to attach to display capabilities */
  private final NonNullConsumer<LazyOptional<IFluidHandler>> displayListener = new WeakConsumerWrapper<>(this, (self, cap) -> {
    if (self.tankDisplayHandlers != null) {
      self.tankDisplayHandlers.remove(cap);
    }
  });

  /** Current amount of fluid in the TE */
  @Getter
  private int fuel = 0;
  /** Amount of fuel produced by the last source */
  @Getter
  private int fuelQuality = 0;
  /** Temperature of the current fuel */
  @Getter
  private int temperature = 0;


  /*
   * Helpers
   */

  private void reset() {
    this.fluidHandler = null;
    this.itemHandler = null;
    this.lastPos = null;
  }

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
    parent.markDirtyFast();
  }


  /* Fuel updating */

  /** Cached lambda of following function as its used a lot */
  private final NonNullPredicate<IFluidHandler> tryConsumeFuel = this::tryConsumeFuel;

  /** Cached lambda of following function as its used a lot */
  private final NonNullPredicate<IItemHandler> trySolidFuel = this::trySolidFuel;

  /**
   * Tries to consume fuel from the given fluid handler
   * @param handler  Handler to consume fuel from
   * @return   True if fuel was consumed
   */
  private boolean trySolidFuel(IItemHandler handler) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      int time = ForgeHooks.getBurnTime(stack) / 4;
      if (time > 0) {
        ItemStack extracted = handler.extractItem(i, 1, false);
        if (extracted.isItemEqual(stack)) {
          fuel += time;
          fuelQuality = time;
          temperature = SOLID_TEMPERATURE;
          parent.markDirtyFast();
        } else {
          TConstruct.log.error("Invalid item removed from solid fuel handler");
        }
        return true;
      }
    }
    return false;
  }

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
        fuelQuality = recipe.getDuration();
        temperature = recipe.getTemperature();
        parent.markDirtyFast();
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
        itemHandler = null;
        fluidHandler = capability;
        capability.addListener(fluidListener);
        lastPos = pos;
        return true;
      } else {
        // if we find a valid item cap, consume fuel from that
        LazyOptional<IItemHandler> itemCap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (itemCap.filter(trySolidFuel).isPresent()) {
          fluidHandler = null;
          itemHandler = itemCap;
          itemCap.addListener(itemListener);
          lastPos = pos;
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Attempts to consume fuel from one of the tanks
   */
  public void findFuel() {
    // if we have a handler, try to use that if possible
    if (fluidHandler != null) {
      if (fluidHandler.filter(tryConsumeFuel).isPresent()) {
        return;
      }
    } else if (itemHandler != null) {
      if (itemHandler.filter(trySolidFuel).isPresent()) {
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
    // technically unneeded for melters, but does not hurt to add
    if (lastPos != null) {
      nbt.put(TAG_LAST_FUEL, TagUtil.writePos(lastPos));
    }
    return nbt;
  }


  /* UI syncing */
  private static final int FUEL = 0;
  private static final int FUEL_QUALITY = 1;
  private static final int TEMPERATURE = 2;
  private static final int LAST_X = 3;
  private static final int LAST_Y = 4;
  private static final int LAST_Z = 5;

  @Override
  public int size() {
    return 6;
  }

  @Override
  public int get(int index) {
    switch (index) {
      case FUEL:
        return fuel;
      case FUEL_QUALITY:
        return fuelQuality;
      case TEMPERATURE:
        return temperature;
      case LAST_X:
        return lastPos == null ? 0 : lastPos.getX();
      case LAST_Y:
        return lastPos == null ? -1 : lastPos.getY();
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
      case FUEL_QUALITY:
        fuelQuality = value;
        break;
      case TEMPERATURE:
        temperature = value;
        break;
        // position sync takes three parts
      case LAST_X:
      case LAST_Y:
      case LAST_Z:
        // position sync
        if (lastPos == null) lastPos = NULL_POS;
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
        fluidHandler = null;
        itemHandler = null;
    }
  }

  /**
   * Called on client structure update to clear the cached display listeners
   */
  public void clearCachedDisplayListeners() {
    this.tankDisplayHandlers = null;
  }

  /**
   * Called client side to get the fuel info for the current tank
   * Note this relies on the client side fuel handlers containing fuel, which is common for our blocks as show fluid in world.
   * If a tank does not do that this won't work.
   * @return  Fuel info
   */
  public FuelInfo getFuelInfo() {
    List<BlockPos> positions = null;
    // if there is no position, means we have not yet consumed fuel. Just fetch the first tank
    // TODO: should we try to find a valid fuel tank? might be a bit confusing if they have multiple tanks in the structure before melting
    // however, a valid tank is a lot more effort to find

    // Y of -1 is how the UI syncs null
    BlockPos mainTank = lastPos;
    if (mainTank == null || mainTank.getY() == -1) {
      // if no first, return no fuel info
      positions = tankSupplier.get();
      if (positions.isEmpty()) {
        return FuelInfo.EMPTY;
      }
      mainTank = positions.get(0);
      assert mainTank != null;
    }

    // fetch primary fuel handler
    if (fluidHandler == null && itemHandler == null) {
      TileEntity te = getWorld().getTileEntity(mainTank);
      if (te != null) {
        LazyOptional<IFluidHandler> fluidCap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        if (fluidCap.isPresent()) {
          fluidHandler = fluidCap;
          fluidHandler.addListener(fluidListener);
        } else {
          LazyOptional<IItemHandler> itemCap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
          if (itemCap.isPresent()) {
            itemHandler = itemCap;
            itemHandler.addListener(itemListener);
          }
        }
      }
    }
    // ensure all handlers are set
    if (fluidHandler == null) fluidHandler = LazyOptional.empty();
    if (itemHandler == null) itemHandler = LazyOptional.empty();

    // if its an item, stop here
    if (itemHandler.isPresent()) {
      return FuelInfo.ITEM;
    }

    // determine what fluid we have and hpw many other fluids we have
    FuelInfo info = fluidHandler.map(handler -> FuelInfo.of(handler.getFluidInTank(0), handler.getTankCapacity(0)))
                                .orElse(FuelInfo.EMPTY);

    // add extra fluid display
    if (!info.isEmpty()) {
      // fetch fluid handler list if missing
      World world = getWorld();
      if (tankDisplayHandlers == null) {
        tankDisplayHandlers = new ArrayList<>();
        // only need to fetch this if either case requests
        if (positions == null) positions = tankSupplier.get();
        for (BlockPos pos : positions) {
          if (!pos.equals(mainTank)) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null) {
              LazyOptional<IFluidHandler> handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
              if (handler.isPresent()) {
                handler.addListener(displayListener);
                tankDisplayHandlers.add(handler);
              }
            }
          }
        }
      }

      // add display info from each handler
      FluidStack currentFuel = info.getFluid();
      for (LazyOptional<IFluidHandler> capability : tankDisplayHandlers) {
        capability.ifPresent(handler -> {
          // sum if empty (more capacity) or the same fluid (more amount and capacity)
          FluidStack fluid = handler.getFluidInTank(0);
          if (fluid.isEmpty()) {
            info.add(0, handler.getTankCapacity(0));
          } else if (currentFuel.isFluidEqual(fluid)) {
            info.add(fluid.getAmount(), handler.getTankCapacity(0));
          }
        });
      }
    }

    return info;
  }

  /** Data class to hold information about the current fuel */
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class FuelInfo {
    /** Empty fuel instance */
    public static final FuelInfo EMPTY = new FuelInfo(FluidStack.EMPTY, 0, 0);
    /** Item fuel instance */
    public static final FuelInfo ITEM = new FuelInfo(FluidStack.EMPTY, 0, 0);

    private final FluidStack fluid;
    private int totalAmount;
    private int capacity;

    /**
     * Gets fuel info from the given stack and capacity
     * @param fluid     Fluid
     * @param capacity  Capacity
     * @return  Fuel info
     */
    public static FuelInfo of(FluidStack fluid, int capacity) {
      if (fluid.isEmpty()) {
        return EMPTY;
      }
      return new FuelInfo(fluid, fluid.getAmount(), Math.max(capacity, fluid.getAmount()));
    }

    /**
     * Adds an additional amount and capacity to this info
     * @param amount    Amount to add
     * @param capacity  Capacity to add
     */
    protected void add(int amount, int capacity) {
      this.totalAmount += amount;
      this.capacity += capacity;
    }

    /**
     * Checks if this fuel info is an item
     * @return  True if an item
     */
    public boolean isItem() {
      return this == ITEM;
    }

    /** Checks if this fuel info has no fluid */
    public boolean isEmpty() {
      return fluid.isEmpty() || totalAmount == 0 || capacity == 0;
    }
  }
}
