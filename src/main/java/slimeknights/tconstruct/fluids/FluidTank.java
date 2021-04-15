package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class FluidTank extends SimpleFixedFluidInv implements IFluidHandler {
  protected FluidFilter validator;

  public FluidTank(int capacity) {
    this(capacity, ConstantFluidFilter.ANYTHING);
  }

  public FluidTank(int capacity, FluidFilter validator) {
    super(1, FluidAmount.of(capacity, 1000));
    this.validator = validator;
  }

  public FluidTank(FluidAmount capacity) {
    this(capacity, ConstantFluidFilter.ANYTHING);
  }

  public FluidTank(FluidAmount capacity, FluidFilter validator) {
    super(1, capacity);
    this.validator = validator;
  }

  public FluidTank setValidator(FluidFilter validator) {
    if (validator != null) {
      this.validator = validator;
    }
    return this;
  }
  
  @Override
  public FluidFilter getInsertionFilter() {
    return super.getInsertionFilter().and(validator);
  }

  @NotNull
  public FluidVolume getFluid() {
    return getFluidInTank(0);
  }

  public FluidAmount getFluidAmount() {
    return getFluid().getAmount_F();
  }

  public FluidTank readFromNBT(CompoundTag nbt) {
    fromTag(nbt);
    return this;
  }

  public CompoundTag writeToNBT(CompoundTag nbt) {
    return toTag(nbt);
  }

  @Override
  public int getTanks() {
    return getTankCount();
  }

  @NotNull
  @Override
  public FluidVolume getFluidInTank(int tank) {
    return getInvFluid(tank);
  }

  @Override
  public FluidAmount getTankCapacity(int tank) {
    return getMaxAmount_F(tank);
  }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidVolume stack) {
    return getInsertionFilter().matches(stack.getFluidKey());
  }

  @Override
  public FluidVolume fill(FluidVolume resource, Simulation action) {
    return attemptInsertion(resource, action);
  }

  @Override
  public FluidVolume drain(FluidAmount resource, Simulation action) {
    return attemptAnyExtraction(resource, action);
  }

  @Override
  public FluidVolume drain(FluidVolume resource, Simulation action) {
    return attemptExtraction(resource.getFluidKey().exactFilter, resource.getAmount_F(), action);
  }

  protected void onContentsChanged() {

  }

  public void setFluid(FluidVolume stack) {
    setInvFluid(0, stack, Simulation.ACTION);
  }

  public boolean isEmpty() {
    return getFluid().isEmpty();
  }

  public FluidAmount getSpace() {
    return getTank(0).getSpace();
  }

}
