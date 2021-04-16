package slimeknights.tconstruct.smeltery.tileentity.module;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInvView;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.mantle.util.NotNullConsumer;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.FluidUtil;
import slimeknights.tconstruct.fluids.IFluidHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelCache;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.misc.IItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Module handling fuel consumption for the melter and smeltery
 */
@RequiredArgsConstructor
public class FuelModule implements PropertyDelegate {
  /** Block position that will never be valid in world, used for sync */
  private static final BlockPos NULL_POS = new BlockPos(0, -1, 0);
  /** Temperature used for solid fuels, hot enough to melt iron */
  public static final int SOLID_TEMPERATURE = 800;

  /** Listener to attach to stored capability */
  private final NotNullConsumer<Optional<IFluidHandler>> fluidListener = new WeakConsumerWrapper<>(this, (self, cap) -> self.reset());
  private final NotNullConsumer<Optional<IItemHandler>> itemListener = new WeakConsumerWrapper<>(this, (self, cap) -> self.reset());

  /** Parent TE */
  private final MantleTileEntity parent;
  /** Supplier for the list of valid tank positions */
  private final Supplier<List<BlockPos>> tankSupplier;

  /** Last fuel recipe used */
  @Nullable
  private MeltingFuel lastRecipe;
  /** Last fluid handler where fluid was extracted */
  @Nullable
  private Optional<IFluidHandler> fluidHandler;
  /** Last item handler where items were extracted */
  @Nullable
  private Optional<IItemHandler> itemHandler;
  /** Position of the last fluid handler */
  @Nullable
  private BlockPos lastPos = null;


  /** Client fuel display */
  private List<Optional<IFluidHandler>> tankDisplayHandlers;
  /** Listener to attach to display capabilities */
  private final NotNullConsumer<Optional<IFluidHandler>> displayListener = new WeakConsumerWrapper<>(this, (self, cap) -> {
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
    return MeltingFuelCache.findRecipe(getWorld().getRecipeManager(), fluid);
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

  /* Cache of objects, since they are otherwise created possibly several times */
  private final Function<IItemHandler,Integer> trySolidFuelConsume = handler -> trySolidFuel(handler, true);
  private final Function<IItemHandler,Integer> trySolidFuelNoConsume = handler -> trySolidFuel(handler, false);
  private final Function<IFluidHandler,Integer> tryLiquidFuelConsume = handler -> tryLiquidFuel(handler, true);
  private final Function<IFluidHandler,Integer> tryLiquidFuelNoConsume = handler -> tryLiquidFuel(handler, false);

  /**
   * Tries to consume fuel from the given fluid handler
   * @param handler  Handler to consume fuel from
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int trySolidFuel(IItemHandler handler, boolean consume) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      int time = 0; //ForgeHooks.getBurnTime(stack) / 4;
      if (time > 0) {
        if (consume) {
          ItemStack extracted = handler.extractItem(i, 1, false);
          if (extracted.isItemEqualIgnoreDamage(stack)) {
            fuel += time;
            fuelQuality = time;
            temperature = SOLID_TEMPERATURE;
            parent.markDirtyFast();
          } else {
            TConstruct.log.error("Invalid item removed from solid fuel handler");
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
  private Function<IItemHandler,Integer> trySolidFuel(boolean consume) {
    return consume ? trySolidFuelConsume : trySolidFuelNoConsume;
  }

  /**
   * Trys to consume fuel from the given fluid handler
   * @param handler  Handler to consume fuel from
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int tryLiquidFuel(IFluidHandler handler, boolean consume) {
    FluidVolume fluid = handler.getFluidInTank(0);
    MeltingFuel recipe = findRecipe(fluid.getRawFluid());
    if (recipe != null) {
      int amount = recipe.getAmount(fluid.getRawFluid());
      if (fluid.getAmount() >= amount) {
        if (consume) {
          FluidVolume drained = handler.drain(FluidVolume.create(fluid.getRawFluid(), amount), Simulation.ACTION);
          if (drained.getAmount() != amount) {
            TConstruct.log.error("Invalid amount of fuel drained from tank");
          }
          fuel += recipe.getDuration();
          fuelQuality = recipe.getDuration();
          temperature = recipe.getTemperature();
          parent.markDirtyFast();
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
  private Function<IFluidHandler,Integer> tryLiquidFuel(boolean consume) {
    return consume ? tryLiquidFuelConsume : tryLiquidFuelNoConsume;
  }

  /**
   * Tries to consume fuel from the given position
   * @param pos  Position
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int tryFindFuel(BlockPos pos, boolean consume) {
    final FixedFluidInvView invView = FluidAttributes.FIXED_INV_VIEW.get(getWorld(), pos);
//    for (FluidVolume fluidVolume : invView.fluidIterable()) {
//    }
    return 690000000;
//    if (te != null) {
//      // if we find a valid cap, try to consume fuel from it
//      Optional<IFluidHandler> capability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
//      Optional<Integer> temperature = capability.map(tryLiquidFuel(consume));
//      if (temperature.isPresent()) {
//        itemHandler = null;
//        fluidHandler = capability;
//        capability.addListener(fluidListener);
//        lastPos = pos;
//        return temperature.get();
//      } else {
//        // if we find a valid item cap, consume fuel from that
//        Optional<IItemHandler> itemCap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
//        temperature = itemCap.map(trySolidFuel(consume));
//        if (temperature.isPresent()) {
//          fluidHandler = null;
//          itemHandler = itemCap;
//          itemCap.addListener(itemListener);
//          lastPos = pos;
//          return temperature.get();
//        }
//      }
//    }
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
    } else if (lastPos != null) {
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

  /* NBT */
  private static final String TAG_FUEL = "fuel";
  private static final String TAG_TEMPERATURE = "temperature";
  private static final String TAG_LAST_FUEL = "last_fuel_tank";

  /**
   * Reads the fuel from NBT
   * @param nbt  NBT to read from
   */
  public void readFromNBT(CompoundTag nbt) {
    fuel = nbt.getInt(TAG_FUEL);
    temperature = nbt.getInt(TAG_TEMPERATURE);
    lastPos = TagUtil.readPos(nbt, TAG_LAST_FUEL);
  }

  /**
   * Writes the fuel to NBT
   * @param nbt  NBT to write to
   * @return  NBT written to
   */
  public CompoundTag writeToNBT(CompoundTag nbt) {
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
      BlockEntity te = getWorld().getBlockEntity(mainTank);
      if (te != null) {
        if(te instanceof SmelteryTileEntity) {
          SmelteryTileEntity smeltery = (SmelteryTileEntity) te;
          fluidHandler = Optional.of(smeltery.getTank());
        } else if(te instanceof TankTileEntity){
          TankTileEntity tank = (TankTileEntity) te;
          fluidHandler = Optional.of(tank.getTank());
        } else {
          throw new RuntimeException("CRAB! " + te.getClass().getSimpleName());
        }
      }
    }
    // ensure all handlers are set
    if (fluidHandler == null) fluidHandler = Optional.empty();
    if (itemHandler == null) itemHandler = Optional.empty();

    // if its an item, stop here
    if (itemHandler.isPresent()) {
      return FuelInfo.ITEM;
    }

    // determine what fluid we have and hpw many other fluids we have
    FuelInfo info = fluidHandler.map(handler -> {
      FluidVolume fluid = handler.getFluidInTank(0);
      int temperature = 0;
      if (!fluid.isEmpty()) {
        MeltingFuel fuel = findRecipe(fluid.getRawFluid());
        if (fuel != null) {
          temperature = fuel.getTemperature();
        }
      }
      return FuelInfo.of(fluid, handler.getTankCapacity(0), temperature);
    }).orElse(FuelInfo.EMPTY);

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
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null) {
              throw new RuntimeException("CRAB!"); // FIXME: PORT
//              Optional<IFluidHandler> handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
//              if (handler.isPresent()) {
//                handler.addListener(displayListener);
//                tankDisplayHandlers.add(handler);
//              }
            }
          }
        }
      }

      // add display info from each handler
      FluidVolume currentFuel = info.getFluid();
      for (Optional<IFluidHandler> capability : tankDisplayHandlers) {
        capability.ifPresent(handler -> {
          // sum if empty (more capacity) or the same fluid (more amount and capacity)
          FluidVolume fluid = handler.getFluidInTank(0);
          if (fluid.isEmpty()) {
            info.add(0, handler.getTankCapacity(0).asInt(1000));
          } else if (currentFuel.equals(fluid)) {
            info.add(fluid.getAmount(), handler.getTankCapacity(0).asInt(1000));
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
    public static final FuelInfo EMPTY = new FuelInfo(TinkerFluids.EMPTY, 0, 0, 0);
    /** Item fuel instance */
    public static final FuelInfo ITEM = new FuelInfo(TinkerFluids.EMPTY, 0, 0, SOLID_TEMPERATURE);

    private final FluidVolume fluid;
    private int totalAmount;
    private int capacity;
    private final int temperature;

    /**
     * Gets fuel info from the given stack and capacity
     * @param fluid     Fluid
     * @param capacity  Capacity
     * @return  Fuel info
     */
    public static FuelInfo of(FluidVolume fluid, FluidAmount capacity, int temperature) {
      if (fluid.isEmpty()) {
        return EMPTY;
      }
      return new FuelInfo(fluid, fluid.getAmount_F().asInt(1000), capacity.max(fluid.getAmount_F()).asInt(1000), temperature);
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
