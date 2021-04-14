package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class FluidTank implements IFluidHandler, IFluidTank {

  protected Predicate<FluidVolume> validator;
  @NotNull
  protected FluidVolume fluid = TinkerFluids.EMPTY;
  protected int capacity;

  public FluidTank(int capacity) {
    this(capacity, e -> true);
  }

  public FluidTank(int capacity, Predicate<FluidVolume> validator) {
    this.capacity = capacity;
    this.validator = validator;
  }

  public FluidTank setCapacity(int capacity) {
    this.capacity = capacity;
    return this;
  }

  public FluidTank setValidator(Predicate<FluidVolume> validator) {
    if (validator != null) {
      this.validator = validator;
    }
    return this;
  }

  public boolean isFluidValid(FluidVolume stack) {
    return validator.test(stack);
  }

  public FluidAmount getCapacity() {
    return FluidAmount.of1620(capacity);
  }

  @NotNull
  public FluidVolume getFluid() {
    return fluid;
  }

  public FluidAmount getFluidAmount() {
    return fluid.getAmount_F();
  }

  public FluidTank readFromNBT(CompoundTag nbt) {

    FluidVolume fluid = FluidVolume.fromTag(nbt);
    setFluid(fluid);
    return this;
  }

  public CompoundTag writeToNBT(CompoundTag nbt) {

    fluid.toTag(nbt);

    return nbt;
  }

  @Override
  public int getTanks() {

    return 1;
  }

  @NotNull
  @Override
  public FluidVolume getFluidInTank(int tank) {

    return getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {

    return getCapacity().as1620();
  }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidVolume stack) {

    return isFluidValid(stack);
  }

  @Override
  public int fill(FluidVolume resource, Simulation action) {
    if (resource.isEmpty() || !isFluidValid(resource)) {
      return 0;
    }
    if (action.isAction()) {
      if (fluid.isEmpty()) {
        return Math.min(capacity, resource.getAmount());
      }
      if (!fluid.equals(resource)) {
        return 0;
      }
      return Math.min(capacity - fluid.getAmount(), resource.getAmount());
    }
    if (fluid.isEmpty()) {
      fluid = FluidVolume.create(resource.getRawFluid(), Math.min(capacity, resource.getAmount()));
      onContentsChanged();
      return fluid.getAmount();
    }
    if (!fluid.equals(resource)) {
      return 0;
    }
    int filled = capacity - fluid.getAmount();

    if (resource.getAmount() < filled) {
      fluid.withAmount(fluid.amount().add(resource.getAmount_F()));
      filled = resource.getAmount();
    } else {
      FluidVolume.create(fluid.getFluidKey(), capacity);
    }
    if (filled > 0)
      onContentsChanged();
    return filled;
  }

  @NotNull
  @Override
  public FluidVolume drain(FluidVolume resource, Simulation action) {
    if (resource.isEmpty() || !resource.equals(fluid)) {
      return TinkerFluids.EMPTY;
    }
    return drain(resource.getAmount(), action);
  }

  @NotNull
  @Override
  public FluidVolume drain(int maxDrain, Simulation action) {
    int drained = maxDrain;
    if (fluid.getAmount() < drained) {
      drained = fluid.getAmount();
    }
    FluidVolume stack = FluidVolume.create(fluid.getRawFluid(), drained);
    if (action.isSimulate() && drained > 0) {
      fluid.withAmount(fluid.amount().min(FluidAmount.of1620(drained)));
      onContentsChanged();
    }
    return stack;
  }

  protected void onContentsChanged() {

  }

  public void setFluid(FluidVolume stack) {
    this.fluid = stack;
  }

  public boolean isEmpty() {
    return fluid.isEmpty();
  }

  public int getSpace() {
    return Math.max(0, capacity - fluid.getAmount());
  }

}
