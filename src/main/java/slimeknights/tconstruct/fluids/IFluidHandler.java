package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public interface IFluidHandler {

  int getTanks();

  FluidVolume getFluidInTank(int tank);

  FluidAmount getTankCapacity(int tank);

  boolean isFluidValid(int tank, FluidVolume stack);

  FluidVolume fill(FluidVolume resource, Simulation action);

  FluidVolume drain(FluidVolume resource, Simulation action);

  FluidVolume drain(FluidAmount resource, Simulation action);

  default FluidVolume drain(int maxDrain, Simulation action) {
    return drain(FluidAmount.of(maxDrain, 1000), action);
  }
}
