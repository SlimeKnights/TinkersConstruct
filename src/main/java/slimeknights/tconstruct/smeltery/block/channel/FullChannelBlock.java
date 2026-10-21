package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
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
  protected Direction getHitSide(Vec3 hitVec, Direction side) {
    // side values in pixels
    var x = hitVec.x * 16;
    var y = hitVec.y * 16;
    var z = hitVec.z * 16;

    switch (side) {
      case DOWN, UP -> {
        // check the centre
        if (x > 5 && x < 11 && z > 5 && z < 11)
          return side;

        // cut one diagonal, n or e
        if (x > z)
          // cool trick to cut the opposite diagonal
          return x + z > 16 ? Direction.EAST : Direction.NORTH;
        // otherwise s or w
        return x + z > 16 ? Direction.SOUTH : Direction.WEST;
      }
      case NORTH, SOUTH -> {
        if (x > 5 && x < 11 && y > 5 && y < 11)
          return side;

        // e or d
        if (x > y)
          return x + y > 16 ? Direction.EAST : Direction.DOWN;
        // otherwise u or w
        return x + y > 16 ? Direction.UP : Direction.WEST;
      }
      case WEST, EAST -> {
        // check the centre
        if (z > 5 && z < 11 && y > 5 && y < 11)
          return side;

        // n or u
        if (z > y)
          return z + y > 16 ? Direction.SOUTH : Direction.DOWN;
        // otherwise s or d
        return z + y > 16 ? Direction.UP : Direction.NORTH;
      }
    }
    // should be impossible
    return side;
  }

  @Override
  protected VoxelShape[] createShapes() {
    return new VoxelShape[] { Shapes.block() };
  }

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
    return true;
  }
}
