package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public interface IFluidHandler {

  int getTanks();

  FluidVolume getFluidInTank(int tank);

  int getTankCapacity(int tank);

  boolean isFluidValid(int tank, FluidVolume stack);

  int fill(FluidVolume resource, Simulation action);

  FluidVolume drain(FluidVolume resource, Simulation action);

  FluidVolume drain(int maxDrain, Simulation action);
}
