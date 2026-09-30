package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.OneWay;

import java.util.EnumMap;
import java.util.Map;

public class NormalChannelBlock extends AbstractChannelBlock {

  /** Voxel bounds for each of the four cardinal directions */
  private static final Map<Direction,VoxelShape> SIDE_BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, Shapes.join(box( 4, 4,  0, 12, 9,  4), box( 6, 6,  0, 10, 9,  4), BooleanOp.ONLY_FIRST));
    map.put(Direction.SOUTH, Shapes.join(box( 4, 4, 12, 12, 9, 16), box( 6, 6, 12, 10, 9, 16), BooleanOp.ONLY_FIRST));
    map.put(Direction.WEST,  Shapes.join(box( 0, 4,  4,  4, 9, 12), box( 0, 6,  6,  4, 9, 10), BooleanOp.ONLY_FIRST));
    map.put(Direction.EAST,  Shapes.join(box(12, 4,  4, 16, 9, 12), box(12, 6,  6, 16, 9, 10), BooleanOp.ONLY_FIRST));
  });

  @Override
  protected int makeShapeKey(boolean _up, boolean down, boolean north, boolean south, boolean west, boolean east) {
    return (down ? 0b00001 : 0) | (north ? 0b00010 : 0) | (south ? 0b00100 : 0) | (west ? 0b01000 : 0) | (east ? 0b10000 : 0);
  }

  public NormalChannelBlock(Properties props) {
    super(props, ConnectionType.NONE, ConnectionType.ONE_WAY);
  }

  @Override
  protected VoxelShape[] createShapes() {
    // center without down connection
    VoxelShape centerUnconnected = Shapes.joinUnoptimized(
      box(4, 4, 4, 12, 9, 12),
      Shapes.or(box(6, 6, 4, 10, 9, 12), box(4, 6, 6, 12, 9, 10)),
      BooleanOp.ONLY_FIRST);
    // center with down connection
    VoxelShape centerConnected = Shapes.joinUnoptimized(
      box(4, 2, 4, 12, 9, 12),
      Shapes.or(box(6, 6, 4, 10, 9, 12), box(4, 6, 6, 12, 9, 10), box(6, 2, 6, 10, 9, 10)),
      BooleanOp.ONLY_FIRST);
    // bounds for unconnected walls
    VoxelShape northWall = box( 6, 6,  4, 10, 9,  6);
    VoxelShape southWall = box( 6, 6, 10, 10, 9, 12);
    VoxelShape westWall  = box( 4, 6,  6,  6, 9, 10);
    VoxelShape eastWall  = box(10, 6,  6, 12, 9, 10);

    // iterate through each direction
    var shapes = new VoxelShape[32];
    boolean[] bools = {false, true};
    for (boolean down : bools) {
      VoxelShape center = down ? centerConnected : centerUnconnected;
      for (boolean north : bools) {
        VoxelShape northBounds = north ? SIDE_BOUNDS.get(Direction.NORTH) : northWall;
        for (boolean south : bools) {
          VoxelShape southBounds = south ? SIDE_BOUNDS.get(Direction.SOUTH) : southWall;
          for (boolean west : bools) {
            VoxelShape westBounds = west ? SIDE_BOUNDS.get(Direction.WEST) : westWall;
            for (boolean east : bools) {
              VoxelShape eastBounds = east ? SIDE_BOUNDS.get(Direction.EAST) : eastWall;
              shapes[makeShapeKey(false, down, north, south, west, east)] = Shapes.or(center, northBounds, southBounds, westBounds, eastBounds);
            }
          }
        }
      }
    }
    return shapes;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
    if (!world.isClientSide) {
      boolean isPowered = world.hasNeighborSignal(pos);
      if (isPowered != state.getValue(POWERED)) {
        state = state.setValue(POWERED, isPowered)
          .setValue(DOWN_1WAY, isPowered && canConnect(world, pos, Direction.DOWN) ? OneWay.TRUE : OneWay.FALSE);
        world.setBlock(pos, state, Block.UPDATE_CLIENTS);
      }
    }
  }
}
