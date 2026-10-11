package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

public class FullChannelBlock extends AbstractChannelBlock {
  public FullChannelBlock(Properties props) {
    super(props, ConnectionType.NONE, ConnectionType.NONE);
  }

  @Override
  protected Direction getHitSide(Vec3 hitVec, Direction side) {
    // map X and Z coords to a direction
    boolean northish = hitVec.z < 0.5;
    boolean westish = hitVec.x < 0.5;

    var x = Math.abs(hitVec.x - 0.5);
    var z = Math.abs(hitVec.z - 0.5);

    if (x > z)
      return westish ? Direction.WEST : Direction.EAST;
    else
      return northish ? Direction.NORTH : Direction.SOUTH;
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
