package slimeknights.tconstruct.smeltery.block.entity.tank;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.fluid.FillOnlyFluidHandler;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/** Tank for each side connection, for the sake of rendering */
public class ChannelSideTank extends FillOnlyFluidHandler {
	private final ChannelBlockEntity channel;
	private final Direction side;

	public ChannelSideTank(ChannelBlockEntity channel, ChannelTank tank, Direction side) {
		super(tank);
		// only horizontals
		assert side.getAxis() != Axis.Y;
		this.channel = channel;
		this.side = side;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		int filled = super.fill(resource, action);
		if (action.execute() && filled > 0) {
			channel.setFlow(side, true);
		}
		return filled;
	}
}
