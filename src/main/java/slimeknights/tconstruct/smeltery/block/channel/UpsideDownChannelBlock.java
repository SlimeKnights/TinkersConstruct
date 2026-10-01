package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.library.utils.Util;

import java.util.EnumMap;
import java.util.Map;

public class UpsideDownChannelBlock extends AbstractChannelBlock {

  /** Voxel bounds for each of the four cardinal directions */
  private static final Map<Direction,VoxelShape> SIDE_BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, Shapes.join(box( 4, 7,  0, 12, 12,  4), box( 6, 7,  0, 10, 10,  4), BooleanOp.ONLY_FIRST));
    map.put(Direction.SOUTH, Shapes.join(box( 4, 7, 12, 12, 12, 16), box( 6, 7, 12, 10, 10, 16), BooleanOp.ONLY_FIRST));
    map.put(Direction.WEST,  Shapes.join(box( 0, 7,  4,  4, 12, 12), box( 0, 7,  6,  4, 10, 10), BooleanOp.ONLY_FIRST));
    map.put(Direction.EAST,  Shapes.join(box(12, 7,  4, 16, 12, 12), box(12, 7,  6, 16, 10, 10), BooleanOp.ONLY_FIRST));
  });

  @Override
  protected int makeShapeKey(boolean up, boolean _down, boolean north, boolean south, boolean west, boolean east) {
    return (up ? 0b00001 : 0) | (north ? 0b00010 : 0) | (south ? 0b00100 : 0) | (west ? 0b01000 : 0) | (east ? 0b10000 : 0);
  }

  public UpsideDownChannelBlock(Properties props) {
    super(props, ConnectionType.ONE_WAY, ConnectionType.NONE);
  }

  @Override
  protected VoxelShape[] createShapes() {
    // center without up connection
    VoxelShape centerUnconnected = Shapes.joinUnoptimized(
      box(4, 7, 4, 12, 12, 12),
      Shapes.or(box(6, 7, 4, 10, 10, 12), box(4, 7, 6, 12, 10, 10)),
      BooleanOp.ONLY_FIRST);
    // center with up connection
    VoxelShape centerConnected = Shapes.joinUnoptimized(
      box(4, 7, 4, 12, 14, 12),
      Shapes.or(box(6, 7, 4, 10, 10, 12), box(4, 7, 6, 12, 10, 10), box(6, 7, 6, 10, 14, 10)),
      BooleanOp.ONLY_FIRST);
    // bounds for unconnected walls
    VoxelShape northWall = box( 6, 7,  4, 10, 10,  6);
    VoxelShape southWall = box( 6, 7, 10, 10, 10, 12);
    VoxelShape westWall  = box( 4, 7,  6,  6, 10, 10);
    VoxelShape eastWall  = box(10, 7,  6, 12, 10, 10);

    // iterate through each direction
    var shapes = new VoxelShape[32];
    boolean[] bools = {false, true};
    for (boolean up : bools) {
      VoxelShape center = up ? centerConnected : centerUnconnected;
      for (boolean north : bools) {
        VoxelShape northBounds = north ? SIDE_BOUNDS.get(Direction.NORTH) : northWall;
        for (boolean south : bools) {
          VoxelShape southBounds = south ? SIDE_BOUNDS.get(Direction.SOUTH) : southWall;
          for (boolean west : bools) {
            VoxelShape westBounds = west ? SIDE_BOUNDS.get(Direction.WEST) : westWall;
            for (boolean east : bools) {
              VoxelShape eastBounds = east ? SIDE_BOUNDS.get(Direction.EAST) : eastWall;
              shapes[makeShapeKey(up, false, north, south, west, east)] = Shapes.or(center, northBounds, southBounds, westBounds, eastBounds);
            }
          }
        }
      }
    }
    return shapes;
  }

  @Override
  protected Direction getHitSide(Vec3 hitVec, Direction side) {
    // map X and Z coords to a direction
    if (hitVec.z() < 0.25f) {
      side = Direction.NORTH;
    } else if (hitVec.z() > 0.75f) {
      side = Direction.SOUTH;
    } else if (hitVec.x() < 0.25f) {
      side = Direction.WEST;
    } else if (hitVec.x() > 0.75f) {
      side = Direction.EAST;
    }
    return side;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
    if (!world.isClientSide) {
      boolean isPowered = world.hasNeighborSignal(pos);
      if (isPowered != state.getValue(POWERED)) {
        state = state.setValue(POWERED, isPowered)
          .setValue(UP_1WAY, isPowered && canConnect(world, pos, Direction.UP) ? ChannelConnection.OneWay.TRUE : ChannelConnection.OneWay.FALSE);
        world.setBlock(pos, state, 2);
      }
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    var level = context.getLevel();
    var pos = context.getClickedPos();
    return super.getStateForPlacement(context).setValue(POWERED, level.hasNeighborSignal(pos));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
    builder.add(UP_1WAY);
    super.createBlockStateDefinition(builder);
  }
}
