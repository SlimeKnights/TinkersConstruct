package slimeknights.tconstruct.library.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

/**
 * Simple implementation of {@link IFluidTank} and {@link IFluidHandler} for a single tank.
 *
 * Similar to {@link net.minecraftforge.fluids.capability.templates.FluidTank} except with more control over the fluid storage.
 */
public interface SimpleFluidTank extends IFluidTank, IFluidHandler {
  @Override
  default int getTanks() {
    return 1;
  }

  /** Called to set the fluid after it has changed */
  void setFluid(FluidStack fluid);

  /**
   * Used by {@link #fill(FluidStack, FluidAction)}, {@link #drain(int, FluidAction)}, and {@link #drain(FluidStack, FluidAction)} to update the fluid result.
   * Allows updating the fluid without needing to call {@link #getFluid()} again, in case it has a cost.
   * @param updated  New fluid stack
   * @param change   Amount the fluid grew or shrunk by.
   */
  default void updateFluid(FluidStack updated, int change) {
    if (change != 0) {
      setFluid(updated);
    }
  }


  /* Redirect duplicate methods */

  @Override
  default int getFluidAmount() {
    return getFluid().getAmount();
  }

  @Nonnull
  @Override
  default FluidStack getFluidInTank(int tank) {
    return getFluid();
  }

  @Override
  default int getTankCapacity(int tank) {
    return getCapacity();
  }

  @Override
  default boolean isFluidValid(FluidStack stack) {
    return true;
  }

  @Override
  default boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return isFluidValid(stack);
  }


  /* Filling and draining */

  @Override
  default int fill(FluidStack resource, FluidAction action) {
    // if nothing to fill, do nothing
    if (resource.isEmpty() || !isFluidValid(resource)) {
      return 0;
    }
    FluidStack fluid = getFluid();

    // if we have nothing, fill as much as possible
    if (fluid.isEmpty()) {
      int amount = Math.min(getCapacity(), resource.getAmount());
      if (action.execute()) {
        updateFluid(new FluidStack(resource, amount), amount);
      }
      return amount;
    }

    // if unable to fill, nothing more to do
    if (!fluid.isFluidEqual(resource)) {
      return 0;
    }

    int capacity = getCapacity();
    int filled = Math.min(capacity - fluid.getAmount(), resource.getAmount());
    if (action.execute()) {
      fluid.grow(filled);
      updateFluid(fluid, filled);
    }
    return filled;
  }

  /** Common logic between both drain methods */
  private FluidStack drain(FluidStack fluid, int maxDrain, FluidAction action) {
    // preconditions: fluid is not empty, maxDrain > 0
    // limit max drain to current fluid
    int drained = maxDrain;
    if (fluid.getAmount() < drained) {
      drained = fluid.getAmount();
    }
    // build the result
    FluidStack result = new FluidStack(fluid, drained);
    if (action.execute()) {
      fluid.shrink(drained);
      updateFluid(fluid, -drained);
    }
    return result;
  }

  @Nonnull
  @Override
  default FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return FluidStack.EMPTY;
    }
    FluidStack fluid = getFluid();
    if (fluid.isEmpty() || !fluid.isFluidEqual(resource)) {
      return FluidStack.EMPTY;
    }
    return drain(fluid, resource.getAmount(), action);
  }

  @Nonnull
  @Override
  default FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0) {
      return FluidStack.EMPTY;
    }
    FluidStack fluid = getFluid();
    if (fluid.isEmpty()) {
      return FluidStack.EMPTY;
    }
    return drain(fluid, maxDrain, action);
  }
}
