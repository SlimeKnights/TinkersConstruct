package slimeknights.tconstruct.smeltery.block.entity.module;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/** Fuel module that supports multiple tanks, selecting just one for the fuel result */
public class MultitankFuelModule extends FuelModule implements IFluidHandler {
  /** Block position that will never be valid in world, used for sync */
  private static final BlockPos NULL_POS = new BlockPos(0, Short.MIN_VALUE, 0);

  /** Supplier for the list of valid tank positions */
  private final Supplier<List<BlockPos>> tankSupplier;
  /** Position of the last fluid handler */
  private BlockPos lastPos = NULL_POS;

  /** Map of all tank handlers at each relevant position. Used for fast switching between handlers, notably in the UI */
  private Map<BlockPos,LazyOptional<IFluidHandler>> tankHandlers;
  /** Listener to attach to display capabilities */
  private final NonNullConsumer<LazyOptional<IFluidHandler>> tankHandlerListener = new WeakConsumerWrapper<>(this, (self, cap) -> {
    if (self.tankHandlers != null) {
      self.tankHandlers.values().remove(cap);
    }
  });

  public MultitankFuelModule(MantleBlockEntity parent, Supplier<List<BlockPos>> tankSupplier) {
    super(parent);
    this.tankSupplier = tankSupplier;
  }

  /** Resets just the last fluid listener */
  private void clearLastListener() {
    super.resetHandler(null);
  }

  @Override
  protected void resetHandler(@Nullable LazyOptional<?> source) {
    if (source == null || source == fluidHandler) {
      this.lastPos = NULL_POS;
    }
    super.resetHandler(source);
  }

  /** Called on structure rebuild to clear the gui handler list */
  public void clearFluidListeners() {
    if (tankHandlers != null) {
      if (Util.isForge()) {
        for (LazyOptional<IFluidHandler> handler : tankHandlers.values()) {
          handler.removeListener(tankHandlerListener);
        }
      }
      tankHandlers = null;
    }
  }

  /** Called on servant load to ensure the listener is present in the cache */
  public void ensureTankPresent(BlockEntity be) {
    BlockPos pos = be.getBlockPos();
    if (tankHandlers != null && !tankHandlers.containsKey(pos)) {
      LazyOptional<IFluidHandler> handler = be.getCapability(ForgeCapabilities.FLUID_HANDLER);
      if (handler.isPresent()) {
        handler.addListener(tankHandlerListener);
        tankHandlers.put(pos, handler);
      }
    }
  }

  /** Gets the map from position to fluid handler */
  private Map<BlockPos,LazyOptional<IFluidHandler>> getTankHandlers() {
    if (tankHandlers == null) {
      tankHandlers = new LinkedHashMap<>();
      Level world = getLevel();
      for (BlockPos pos : tankSupplier.get()) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null) {
          LazyOptional<IFluidHandler> handler = te.getCapability(ForgeCapabilities.FLUID_HANDLER);
          if (handler.isPresent()) {
            handler.addListener(tankHandlerListener);
            tankHandlers.put(pos, handler);
          }
        }
      }
    }
    return tankHandlers;
  }


  /* Fuel finding */

  /**
   * Tries to consume fuel from the given position
   * @param pos  Position
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int tryFuelPosition(BlockPos pos, boolean consume) {
    LazyOptional<IFluidHandler> tankCap = getTankHandlers().get(pos);
    if (tankCap != null && tankCap.isPresent()) {
      // if we find a valid cap, try to consume fuel from it
      int temperature = tryLiquidFuel(tankCap.orElse(EmptyFluidHandler.INSTANCE), consume);
      if (temperature > 0) {
        clearLastListener();
        fluidHandler = tankCap;
        tankCap.addListener(fluidListener);
        lastPos = pos;
        return temperature;
      }
    }
    return 0;
  }

  /**
   * Attempts to consume fuel from one of the tanks
   * @return  temperature of the found fluid, 0 if none
   */
  @Override
  public int findFuel(boolean consume) {
    // only fetch a handler if we haven't done so
    if (fluidHandler != null) {
      // if we have a handler, try to use that if possible
      if (fluidHandler.isPresent()) {
        int temperature = tryLiquidFuel(fluidHandler.orElse(EmptyFluidHandler.INSTANCE), consume);
        if (temperature > 0) {
          return temperature;
        }
      }
    } else if (lastPos != NULL_POS) {
      // if no handler, try to find one at the last position
      int posTemp = tryFuelPosition(lastPos, consume);
      if (posTemp > 0) {
        return posTemp;
      }
    }

    // find a new handler among our tanks
    for (BlockPos pos : tankSupplier.get()) {
      // already checked the last position above, no reason to try again
      if (!pos.equals(lastPos)) {
        int posTemp = tryFuelPosition(pos, consume);
        if (posTemp > 0) {
          return posTemp;
        }
      }
    }

    // no handler found, tell client of the lack of fuel
    if (consume) {
      temperature = 0;
      rate = 0;
    }
    return 0;
  }


  /* NBT */
  private static final String TAG_LAST_FUEL = "last_fuel";

  @Override
  public void readFromTag(CompoundTag nbt) {
    super.readFromTag(nbt);
    if (nbt.contains(TAG_LAST_FUEL, Tag.TAG_COMPOUND)) {
      lastPos = NbtUtils.readBlockPos(nbt.getCompound(TAG_LAST_FUEL)).offset(parent.getBlockPos());
    }
  }

  @Override
  public CompoundTag writeToTag(CompoundTag nbt) {
    nbt = super.writeToTag(nbt);
    if (lastPos != NULL_POS) {
      nbt.put(TAG_LAST_FUEL, NbtUtils.writeBlockPos(lastPos.subtract(parent.getBlockPos())));
    }
    return nbt;
  }


  /* UI syncing */
  private static final int LAST_X = 4;
  private static final int LAST_Y = 5;
  private static final int LAST_Z = 6;

  @Override
  public int getCount() {
    return 7;
  }

  @Override
  public int get(int index) {
    return switch (index) {
      case LAST_X -> lastPos.getX();
      case LAST_Y -> lastPos.getY();
      case LAST_Z -> lastPos.getZ();
      default -> super.get(index);
    };
  }

  @Override
  public void set(int index, int value) {
    if (LAST_X <= index && index <= LAST_Z) {
      switch (index) {
        case LAST_X -> lastPos = new BlockPos(value, lastPos.getY(), lastPos.getZ());
        case LAST_Y -> lastPos = new BlockPos(lastPos.getX(), value, lastPos.getZ());
        case LAST_Z -> lastPos = new BlockPos(lastPos.getX(), lastPos.getY(), value);
      }
      clearLastListener();
    } else {
      super.set(index, value);
    }
  }

  @Override
  public FuelInfo getFuelInfo() {
    // if there is no position, means we have not yet consumed fuel. Just fetch the first tank
    // TODO: should we try to find a valid fuel tank? might be a bit confusing if they have multiple tanks in the structure before melting
    // however, a valid tank is a lot more effort to find

    // Y of big negative is how the UI syncs null
    BlockPos mainTank = lastPos;
    if (mainTank.getY() == NULL_POS.getY()) {
      // if no first, return no fuel info
      List<BlockPos> positions = tankSupplier.get();
      if (positions.isEmpty()) {
        return FuelInfo.EMPTY;
      }
      mainTank = positions.get(0);
      assert mainTank != null;
    }

    // fetch primary fuel handler
    if (fluidHandler == null) {
      LazyOptional<IFluidHandler> fluidCap = getTankHandlers().getOrDefault(mainTank, LazyOptional.empty());
      if (fluidCap.isPresent()) {
        fluidHandler = fluidCap;
        fluidHandler.addListener(fluidListener);
      } else {
        // ensure handlers is set
        fluidHandler = LazyOptional.empty();
      }
    }

    // determine what fluid we have and hpw many other fluids we have
    FuelInfo info = super.getFuelInfo();
    // add extra fluid display
    if (!info.isEmpty()) {
      // add display info from each handler
      FluidStack currentFuel = info.getFluid();
      for (Entry<BlockPos,LazyOptional<IFluidHandler>> entry : getTankHandlers().entrySet()) {
        if (!mainTank.equals(entry.getKey())) {
          entry.getValue().ifPresent(handler -> {
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
    }

    return info;
  }


  /* Fluid handler */

  /** Gets the most recently used fluid */
  public FluidStack getLastFluid() {
    if (fluidHandler != null && fluidHandler.isPresent()) {
      return fluidHandler.orElse(EmptyFluidHandler.INSTANCE).getFluidInTank(0);
    }
    BlockPos pos;
    if (lastPos.getY() != NULL_POS.getY()) {
      pos = lastPos;
    } else {
      List<BlockPos> positions = tankSupplier.get();
      if (!positions.isEmpty()) {
        pos = positions.get(0);
      } else {
        return FluidStack.EMPTY;
      }
    }
    return getTankHandlers().getOrDefault(pos, LazyOptional.empty()).orElse(EmptyFluidHandler.INSTANCE).getFluidInTank(0);
  }

  @Override
  public int getTanks() {
    return tankSupplier.get().size();
  }

  /** Gets the tank at the given index */
  private IFluidHandler getTank(int tank) {
    if (tank >= 0) {
      List<BlockPos> positions = tankSupplier.get();
      if (tank < positions.size()) {
        return getTankHandlers().getOrDefault(positions.get(tank), LazyOptional.empty()).orElse(EmptyFluidHandler.INSTANCE);
      }
    }
    return EmptyFluidHandler.INSTANCE;
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return getTank(tank).getFluidInTank(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return getTank(tank).getTankCapacity(tank);
  }

  @Override
  public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return getTank(tank).isFluidValid(0, stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    int totalFilled = 0;
    resource = resource.copy();
    // try each handler, updating the amount we filled as we go
    // note the map internally is a linked hash map so order is consistent
    for (LazyOptional<IFluidHandler> handler : getTankHandlers().values()) {
      int filled = handler.orElse(EmptyFluidHandler.INSTANCE).fill(resource, action);
      if (filled > 0) {
        // if we finished filling, we are done, return that value
        // this is a quick exit that might save us a copy
        totalFilled += filled;
        if (filled >= resource.getAmount()) {
          break;
        }
        // if this was our first fill, copy the resource
        if (totalFilled == filled) {
          resource = new FluidStack(resource, resource.getAmount() - filled);
        } else {
          resource.shrink(filled);
        }
        // resource will never be empty, as if it was the above break would be hit
      }
    }
    return totalFilled;
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    // try each handler, updating the amount we filled as we go
    // note the map internally is a linked hash map so order is consistent
    for (LazyOptional<IFluidHandler> handler : getTankHandlers().values()) {
      FluidStack drained = handler.orElse(EmptyFluidHandler.INSTANCE).drain(resource, action);
      if (!drained.isEmpty()) {
        // if we managed to drain something, add it into our current drained stack, and decrease the amount we still want to drain
        if (drainedSoFar.isEmpty()) {
          drainedSoFar = drained;
          // if the first success, make a copy of the resource before shrinking it, need to shrink to prevent passing in too much to future hooks
          // though we can skip copying if the first one is all we need
          // note the >= part is just for redundancy, practically its always either = or less than
          if (drained.getAmount() >= resource.getAmount()) {
            break;
          }
          resource = new FluidStack(resource, resource.getAmount() - drained.getAmount());
        } else {
          // resource is guaranteed a copy, and drainedSoFar is a newly created stack, both safe to mutate
          drainedSoFar.grow(drained.getAmount());
          resource.shrink(drained.getAmount());
          // if we drained everything desired, we are done
          if (resource.isEmpty()) {
            break;
          }
        }
      }
    }
    return drainedSoFar;
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    FluidStack toDrain = FluidStack.EMPTY;
    // try each handler, updating the amount we filled as we go
    // note the map internally is a linked hash map so order is consistent
    for (LazyOptional<IFluidHandler> handler : getTankHandlers().values()) {
      // if we have not drained anything yet, can use typeless hook
      if (toDrain.isEmpty()) {
        FluidStack drained = handler.orElse(EmptyFluidHandler.INSTANCE).drain(maxDrain, action);
        if (!drained.isEmpty()) {
          drainedSoFar = drained;
          // if we finished draining, we are done, otherwise we need to create a filter for future drain attempts
          // note the >= part is just for redundancy, practically its always either = or less than
          if (drained.getAmount() >= maxDrain) {
            break;
          }
          toDrain = new FluidStack(drained, maxDrain - drained.getAmount());
        }
      } else {
        // if we already drained some fluid, type sensitive and increase our results
        FluidStack drained = handler.orElse(EmptyFluidHandler.INSTANCE).drain(toDrain, action);
        if (!drained.isEmpty()) {
          drainedSoFar.grow(drained.getAmount());
          toDrain.shrink(drained.getAmount());
          // if we drained everything desired, we are done
          if (toDrain.isEmpty()) {
            break;
          }
        }
      }
    }
    return drainedSoFar;
  }
}
