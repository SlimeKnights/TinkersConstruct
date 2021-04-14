package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
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
  public int getTankCapacity(int tank) { return 0; }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidVolume stack) { return true; }

  @Override
  public int fill(FluidVolume resource, Simulation action)
  {
    return 0;
  }

  @NotNull
  @Override
  public FluidVolume drain(FluidVolume resource, Simulation action)
  {
    return TinkerFluids.EMPTY;
  }

  @NotNull
  @Override
  public FluidVolume drain(int maxDrain, Simulation action)
  {
    return TinkerFluids.EMPTY;
  }
}
