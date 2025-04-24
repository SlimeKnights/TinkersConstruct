package slimeknights.tconstruct.smeltery.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

/**
 * Fluid tank representing a stack of multiple fluid tanks. All operations must affect every tack in the stack at the same time, so must in increments of the scale.
 * Internally works the same as a fluid tank with {@code capacity * scale}, except operations are truncated to the nearest scale (e.g. if scale is 4, we must fill in 4mb increments).
 */
public class ScaledFluidTank extends FluidTank {
  private final int scale;
  private ScaledFluidTank(int capacity, int scale) {
    super(capacity * scale);
    this.scale = scale;
  }

  /** Creates a new instance */
  public static FluidTank create(int capacity, int scale) {
    if (scale == 1) {
      return new FluidTank(capacity);
    }
    return new ScaledFluidTank(capacity, scale);
  }

  /* Helpers */

  /** enforces the amount matches the scale */
  private int enforceScale(int amount) {
    // no working with fluids of partial amounts
    int remainder = amount % scale;
    if (remainder != 0) {
      amount -= remainder;
    }
    return amount;
  }

  /** enforces the fluid matches the scale */
  private FluidStack enforceScale(FluidStack stack, boolean copy) {
    // no working with fluids of partial amounts
    int remainder = stack.getAmount() % scale;
    if (remainder != 0) {
      if (copy) {
        stack = stack.copy();
      }
      stack.shrink(remainder);
    }
    return stack;
  }


  /* Fluid tank methods */

  @Override
  public FluidTank setCapacity(int capacity) {
    return super.setCapacity(enforceScale(capacity));
  }

  @Override
  public void setFluid(FluidStack stack) {
    super.setFluid(enforceScale(stack, false));
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return super.fill(enforceScale(resource, true), action);
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return super.drain(enforceScale(maxDrain), action);
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return super.drain(enforceScale(resource, true), action);
  }


  /* NBT */

  @Override
  public FluidTank readFromNBT(CompoundTag nbt) {
    // scale the fluid on reading from NBT; as each instance should store the fluid relative to stack size 1
    FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
    fluid.setAmount(fluid.getAmount() * scale);
    setFluid(fluid);
    return this;
  }

  @Override
  public CompoundTag writeToNBT(CompoundTag nbt) {
    // scale the fluid on reading from NBT; as each instance should store the fluid relative to stack size 1
    FluidStack fluid = this.fluid.copy();
    fluid.setAmount(fluid.getAmount() / scale);
    fluid.writeToNBT(nbt);
    return nbt;
  }
}
