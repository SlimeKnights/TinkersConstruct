package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FullChannelBlock extends PipeChannelBlock {
  public FullChannelBlock(Properties props) {
    super(props);
  }

  @Override
  protected int makeShapeKey(boolean _up, boolean _down, boolean _north, boolean _south, boolean _west, boolean _east) {
    return 0;
  }

  @Override
  protected VoxelShape[] createShapes() {
    return new VoxelShape[] { Shapes.block() };
  }
}
