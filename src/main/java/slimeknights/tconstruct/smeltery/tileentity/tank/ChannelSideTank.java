package slimeknights.tconstruct.smeltery.tileentity.tank;

import alexiil.mc.lib.attributes.Simulation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
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
	public int fill(FluidVolume resource, Simulation action) {
		int filled = super.fill(resource, action);
		if (action.isAction() && filled > 0) {
			channel.setFlow(side, true);
		}
		return filled;
	}
}
