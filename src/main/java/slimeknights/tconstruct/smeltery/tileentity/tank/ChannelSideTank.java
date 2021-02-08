package slimeknights.tconstruct.smeltery.tileentity.tank;

import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.fluid.FillOnlyFluidHandler;
import slimeknights.tconstruct.smeltery.tileentity.ChannelTileEntity;

/** Tank for each side connection, for the sake of rendering */
public class ChannelSideTank extends FillOnlyFluidHandler {
	private final ChannelTileEntity channel;
	private final Direction side;

	public ChannelSideTank(ChannelTileEntity channel, ChannelTank tank, Direction side) {
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
