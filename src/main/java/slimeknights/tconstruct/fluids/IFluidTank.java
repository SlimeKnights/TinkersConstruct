package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import org.jetbrains.annotations.NotNull;

public interface IFluidTank {

  @NotNull
  FluidVolume getFluid();

  FluidAmount getFluidAmount();

  FluidAmount getCapacity();

  boolean isFluidValid(FluidVolume stack);

  int fill(FluidVolume resource, Simulation action);

  @NotNull
  FluidVolume drain(int maxDrain, Simulation action);

  @NotNull
  FluidVolume drain(FluidVolume resource, Simulation action);
}
