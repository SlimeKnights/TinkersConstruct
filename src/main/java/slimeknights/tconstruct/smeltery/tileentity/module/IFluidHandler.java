package slimeknights.tconstruct.smeltery.tileentity.module;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public interface IFluidHandler {
  enum FluidAction {
    EXECUTE, SIMULATE;

    public boolean execute() {
      return this == EXECUTE;
    }

    public boolean simulate() {
      return this == SIMULATE;
    }
  }

  int getTanks();

  FluidVolume getFluidInTank(int tank);

  int getTankCapacity(int tank);

  boolean isFluidValid(int tank, FluidVolume stack);

  int fill(FluidVolume resource, FluidAction action);

  FluidVolume drain(FluidVolume resource, FluidAction action);

  FluidVolume drain(int maxDrain, FluidAction action);
}
