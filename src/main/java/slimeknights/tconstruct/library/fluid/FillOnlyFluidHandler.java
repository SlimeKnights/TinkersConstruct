package slimeknights.tconstruct.library.fluid;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;

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
	public int fill(FluidVolume resource, FluidAction action) {
		return parent.fill(resource, action);
	}

	@NotNull
	@Override
	public FluidVolume drain(FluidVolume resource, FluidAction action) {
		return FluidVolume.EMPTY;
	}

	@NotNull
	@Override
	public FluidVolume drain(int maxDrain, FluidAction action) {
		return FluidVolume.EMPTY;
	}
}
