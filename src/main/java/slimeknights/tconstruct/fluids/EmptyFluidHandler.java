package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import org.jetbrains.annotations.NotNull;

public class EmptyFluidHandler implements IFluidHandler {
  public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

  protected EmptyFluidHandler() {}

  @Override
  public int getTanks() { return 1; }

  @NotNull
  @Override
  public FluidVolume getFluidInTank(int tank) { return TinkerFluids.EMPTY; }

  @Override
  public FluidAmount getTankCapacity(int tank) {
    return FluidAmount.ZERO;
  }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidVolume stack) { return true; }

  @Override
  public FluidVolume fill(FluidVolume resource, Simulation action)
  {
    return TinkerFluids.EMPTY;
  }

  @NotNull
  @Override
  public FluidVolume drain(FluidVolume resource, Simulation action)
  {
    return TinkerFluids.EMPTY;
  }

  @NotNull
  @Override
  public FluidVolume drain(FluidAmount maxDrain, Simulation action)
  {
    return TinkerFluids.EMPTY;
  }
}
