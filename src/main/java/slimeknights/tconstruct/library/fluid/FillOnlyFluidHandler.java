package slimeknights.tconstruct.library.fluid;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.smeltery.tileentity.module.IFluidHandler;

/**
 * Fluid handler wrapper that only allows filling
 */
public class FillOnlyFluidHandler implements IFluidHandler {
	private final IFluidHandler parent;
	public FillOnlyFluidHandler(IFluidHandler parent) {
		this.parent = parent;
	}

	@Override
	public int getTanks() {
		return parent.getTanks();
	}

	@NotNull
	@Override
	public FluidVolume getFluidInTank(int tank) {
		return parent.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return parent.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidVolume stack) {
		return false;
	}

	@Override
	public int fill(FluidVolume resource, Simulation action) {
		return parent.fill(resource, action);
	}

	@NotNull
	@Override
	public FluidVolume drain(FluidVolume resource, Simulation action) {
		return TinkerFluids.EMPTY;
	}

	@NotNull
	@Override
	public FluidVolume drain(int maxDrain, Simulation action) {
		return TinkerFluids.EMPTY;
	}
}
