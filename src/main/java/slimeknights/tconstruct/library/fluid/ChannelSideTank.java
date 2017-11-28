package slimeknights.tconstruct.library.fluid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;

public class ChannelSideTank extends FluidHandlerConcatenate {

  private EnumFacing side;
  private TileChannel channel;
  public ChannelSideTank(TileChannel channel, ChannelTank tank, EnumFacing side) {
    super(tank);
    // only horizontals
    assert side.getAxis() != Axis.Y;
    this.channel = channel;
    this.side = side;
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    int filled = super.fill(resource, doFill);
    // tell the channel we got fluid from this side
    if(doFill && filled > 0) {
      channel.setFlow(side, true);
    }
    return filled;
  }

}
