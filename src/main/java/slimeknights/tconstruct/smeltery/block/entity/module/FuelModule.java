package slimeknights.tconstruct.smeltery.block.entity.module;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Module handling fuel consumption for the melter and smeltery
 */
@RequiredArgsConstructor
public class FuelModule implements ContainerData {
  /** Block position that will never be valid in world, used for sync */
  private static final BlockPos NULL_POS = new BlockPos(0, Short.MIN_VALUE, 0);
  /** Temperature used for solid fuels, hot enough to melt iron */
  public static final int SOLID_TEMPERATURE = 800;

  /** Listener to attach to stored capability */
  private final NonNullConsumer<LazyOptional<IFluidHandler>> fluidListener = new WeakConsumerWrapper<>(this, (self, cap) -> self.reset());
  private final NonNullConsumer<LazyOptional<IItemHandler>> itemListener = new WeakConsumerWrapper<>(this, (self, cap) -> self.reset());

  /** Parent TE */
  private final MantleBlockEntity parent;
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
  private BlockPos lastPos = NULL_POS;


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
    this.tankDisplayHandlers = null;
    this.lastPos = NULL_POS;
  }

  /** Gets a nonnull world instance from the parent */
  private Level getLevel() {
    return Objects.requireNonNull(parent.getLevel(), "Parent tile entity has null world");
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
    MeltingFuel recipe = MeltingFuelLookup.findFuel(fluid);
    if (recipe != null) {
      lastRecipe = recipe;
    }
    return recipe;
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
    parent.setChangedFast();
  }


  /* Fuel updating */

  /* Cache of objects, since they are otherwise created possibly several times */
  private final NonNullFunction<IItemHandler,Integer> trySolidFuelConsume = handler -> trySolidFuel(handler, true);
  private final NonNullFunction<IItemHandler,Integer> trySolidFuelNoConsume = handler -> trySolidFuel(handler, false);
  private final NonNullFunction<IFluidHandler,Integer> tryLiquidFuelConsume = handler -> tryLiquidFuel(handler, true);
  private final NonNullFunction<IFluidHandler,Integer> tryLiquidFuelNoConsume = handler -> tryLiquidFuel(handler, false);

  /**
   * Tries to consume fuel from the given fluid handler
   * @param handler  Handler to consume fuel from
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int trySolidFuel(IItemHandler handler, boolean consume) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      int time = ForgeHooks.getBurnTime(stack, TinkerRecipeTypes.FUEL.get()) / 4;
      if (time > 0) {
        if (consume) {
          ItemStack extracted = handler.extractItem(i, 1, false);
          if (ItemStack.isSameItem(extracted, stack)) {
            fuel += time;
            fuelQuality = time;
            temperature = SOLID_TEMPERATURE;
            parent.setChangedFast();
            // return the container
            ItemStack container = extracted.getCraftingRemainingItem();
            if (!container.isEmpty()) {
              // if we cannot insert the container back, spit it on the ground
              ItemStack notInserted = ItemHandlerHelper.insertItem(handler, container, false);
              if (!notInserted.isEmpty()) {
                Level world = getLevel();
                double x = (world.random.nextFloat() * 0.5F) + 0.25D;
                double y = (world.random.nextFloat() * 0.5F) + 0.25D;
                double z = (world.random.nextFloat() * 0.5F) + 0.25D;
                BlockPos pos = lastPos == NULL_POS ? parent.getBlockPos() : lastPos;
                ItemEntity itementity = new ItemEntity(world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, container);
                itementity.setDefaultPickUpDelay();
                world.addFreshEntity(itementity);
              }
            }
          } else {
            TConstruct.LOG.error("Invalid item removed from solid fuel handler");
          }
        }
        return SOLID_TEMPERATURE;
      }
    }
    return 0;
  }

  /**
   * Gets the mapper function for solid fuel
   * @param consume  If true, fuel is consumed
   * @return Mapper function for solid fuel
   */
  private NonNullFunction<IItemHandler,Integer> trySolidFuel(boolean consume) {
    return consume ? trySolidFuelConsume : trySolidFuelNoConsume;
  }

  /**
   * Trys to consume fuel from the given fluid handler
   * @param handler  Handler to consume fuel from
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int tryLiquidFuel(IFluidHandler handler, boolean consume) {
    FluidStack fluid = handler.getFluidInTank(0);
    MeltingFuel recipe = findRecipe(fluid.getFluid());
    if (recipe != null) {
      int amount = recipe.getAmount(fluid.getFluid());
      if (fluid.getAmount() >= amount) {
        if (consume) {
          FluidStack drained = handler.drain(new FluidStack(fluid, amount), FluidAction.EXECUTE);
          if (drained.getAmount() != amount) {
            TConstruct.LOG.error("Invalid amount of fuel drained from tank");
          }
          fuel += recipe.getDuration();
          fuelQuality = recipe.getDuration();
          temperature = recipe.getTemperature();
          parent.setChangedFast();
          return temperature;
        } else {
          return recipe.getTemperature();
        }
      }
    }
    return 0;
  }

  /**
   * Gets the mapper function for liquid fuel
   * @param consume  If true, fuel is consumed
   * @return Mapper function for liquid fuel
   */
  private NonNullFunction<IFluidHandler,Integer> tryLiquidFuel(boolean consume) {
    return consume ? tryLiquidFuelConsume : tryLiquidFuelNoConsume;
  }

  /**
   * Tries to consume fuel from the given position
   * @param pos  Position
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int tryFindFuel(BlockPos pos, boolean consume) {
    BlockEntity te = getLevel().getBlockEntity(pos);
    if (te != null) {
      // if we find a valid cap, try to consume fuel from it
      LazyOptional<IFluidHandler> capability = te.getCapability(ForgeCapabilities.FLUID_HANDLER);
      Optional<Integer> temperature = capability.map(tryLiquidFuel(consume));
      if (temperature.isPresent()) {
        itemHandler = null;
        fluidHandler = capability;
        tankDisplayHandlers = null;
        capability.addListener(fluidListener);
        lastPos = pos;
        return temperature.get();
      } else {
        // if we find a valid item cap, consume fuel from that
        LazyOptional<IItemHandler> itemCap = te.getCapability(ForgeCapabilities.ITEM_HANDLER);
        temperature = itemCap.map(trySolidFuel(consume));
        if (temperature.isPresent()) {
          fluidHandler = null;
          tankDisplayHandlers = null;
          itemHandler = itemCap;
          itemCap.addListener(itemListener);
          lastPos = pos;
          return temperature.get();
        }
      }
    }

    return 0;
  }

  /**
   * Attempts to consume fuel from one of the tanks
   * @return  temperature of the found fluid, 0 if none
   */
  public int findFuel(boolean consume) {
    // if we have a handler, try to use that if possible
    Optional<Integer> handlerTemp = Optional.empty();
    if (fluidHandler != null) {
      handlerTemp = fluidHandler.map(tryLiquidFuel(consume));
    } else if (itemHandler != null) {
      handlerTemp = itemHandler.map(trySolidFuel(consume));
    // if no handler, try to find one at the last position
    } else if (lastPos != NULL_POS) {
      int posTemp = tryFindFuel(lastPos, consume);
      if (posTemp > 0) {
        return posTemp;
      }
    }

    // if either handler was present, return the temperature
    if (handlerTemp.orElse(0) > 0) {
      return handlerTemp.get();
    }

    // find a new handler among our tanks
    for (BlockPos pos : tankSupplier.get()) {
      // already checked the last position above, no reason to try again
      if (!pos.equals(lastPos)) {
        int posTemp = tryFindFuel(pos, consume);
        if (posTemp > 0) {
          return posTemp;
        }
      }
    }

    // no handler found, tell client of the lack of fuel
    if (consume) {
      temperature = 0;
    }
    return 0;
  }

  /* Tag */
  private static final String TAG_FUEL = "fuel";
  private static final String TAG_TEMPERATURE = "temperature";
  private static final String TAG_LAST_FUEL = "last_fuel";

  /**
   * Reads the fuel from NBT
   * @param nbt  Tag to read from
   */
  public void readFromTag(CompoundTag nbt) {
    if (nbt.contains(TAG_FUEL, Tag.TAG_ANY_NUMERIC)) {
      fuel = nbt.getInt(TAG_FUEL);
    }
    if (nbt.contains(TAG_TEMPERATURE, Tag.TAG_ANY_NUMERIC)) {
      temperature = nbt.getInt(TAG_TEMPERATURE);
    }
    if (nbt.contains(TAG_LAST_FUEL, Tag.TAG_COMPOUND)) {
      lastPos = NbtUtils.readBlockPos(nbt.getCompound(TAG_LAST_FUEL)).offset(parent.getBlockPos());
    }
  }

  /**
   * Writes the fuel to NBT
   * @param nbt  Tag to write to
   * @return  Tag written to
   */
  public CompoundTag writeToTag(CompoundTag nbt) {
    nbt.putInt(TAG_FUEL, fuel);
    nbt.putInt(TAG_TEMPERATURE, temperature);
    // technically unneeded for melters, but does not hurt to add
    if (lastPos != NULL_POS) {
      nbt.put(TAG_LAST_FUEL, NbtUtils.writeBlockPos(lastPos.subtract(parent.getBlockPos())));
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
  public int getCount() {
    return 6;
  }

  @Override
  public int get(int index) {
    return switch (index) {
      case FUEL         -> fuel;
      case FUEL_QUALITY -> fuelQuality;
      case TEMPERATURE  -> temperature;
      case LAST_X -> lastPos.getX();
      case LAST_Y -> lastPos.getY();
      case LAST_Z -> lastPos.getZ();
      default -> 0;
    };
  }

  @Override
  public void set(int index, int value) {
    switch (index) {
      case FUEL         -> fuel = value;
      case FUEL_QUALITY -> fuelQuality = value;
      case TEMPERATURE  -> temperature = value;

      // position sync takes three parts
      case LAST_X, LAST_Y, LAST_Z -> {
        // position sync
        switch (index) {
          case LAST_X -> lastPos = new BlockPos(value, lastPos.getY(), lastPos.getZ());
          case LAST_Y -> lastPos = new BlockPos(lastPos.getX(), value, lastPos.getZ());
          case LAST_Z -> lastPos = new BlockPos(lastPos.getX(), lastPos.getY(), value);
        }
        fluidHandler = null;
        itemHandler = null;
        tankDisplayHandlers = null;
      }
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
    if (mainTank.getY() == NULL_POS.getY()) {
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
      BlockEntity te = getLevel().getBlockEntity(mainTank);
      if (te != null) {
        LazyOptional<IFluidHandler> fluidCap = te.getCapability(ForgeCapabilities.FLUID_HANDLER);
        if (fluidCap.isPresent()) {
          fluidHandler = fluidCap;
          fluidHandler.addListener(fluidListener);
        } else {
          LazyOptional<IItemHandler> itemCap = te.getCapability(ForgeCapabilities.ITEM_HANDLER);
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
    FuelInfo info = fluidHandler.map(handler -> {
      FluidStack fluid = handler.getFluidInTank(0);
      int temperature = 0;
      if (!fluid.isEmpty()) {
        MeltingFuel fuel = findRecipe(fluid.getFluid());
        if (fuel != null) {
          temperature = fuel.getTemperature();
        }
      }
      return FuelInfo.of(fluid, handler.getTankCapacity(0), temperature);
    }).orElse(FuelInfo.EMPTY);

    // add extra fluid display
    if (!info.isEmpty()) {
      // fetch fluid handler list if missing
      Level world = getLevel();
      if (tankDisplayHandlers == null) {
        tankDisplayHandlers = new ArrayList<>();
        // only need to fetch this if either case requests
        if (positions == null) positions = tankSupplier.get();
        for (BlockPos pos : positions) {
          if (!pos.equals(mainTank)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null) {
              LazyOptional<IFluidHandler> handler = te.getCapability(ForgeCapabilities.FLUID_HANDLER);
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
    public static final FuelInfo EMPTY = new FuelInfo(FluidStack.EMPTY, 0, 0, 0);
    /** Item fuel instance */
    public static final FuelInfo ITEM = new FuelInfo(FluidStack.EMPTY, 0, 0, SOLID_TEMPERATURE);

    private final FluidStack fluid;
    private int totalAmount;
    private int capacity;
    private final int temperature;

    /**
     * Gets fuel info from the given stack and capacity
     * @param fluid     Fluid
     * @param capacity  Capacity
     * @return  Fuel info
     */
    public static FuelInfo of(FluidStack fluid, int capacity, int temperature) {
      if (fluid.isEmpty()) {
        return EMPTY;
      }
      return new FuelInfo(fluid, fluid.getAmount(), Math.max(capacity, fluid.getAmount()), temperature);
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
