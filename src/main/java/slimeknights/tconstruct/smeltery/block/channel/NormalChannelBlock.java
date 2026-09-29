package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.NoWay;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.OneWay;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class NormalChannelBlock extends AbstractChannelBlock<NoWay, ChannelConnection.OneWay> {

  /** Voxel bounds for each of the four cardinal directions */
  private static final Map<Direction,VoxelShape> SIDE_BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, Shapes.join(box( 4, 4,  0, 12, 9,  4), box( 6, 6,  0, 10, 9,  4), BooleanOp.ONLY_FIRST));
    map.put(Direction.SOUTH, Shapes.join(box( 4, 4, 12, 12, 9, 16), box( 6, 6, 12, 10, 9, 16), BooleanOp.ONLY_FIRST));
    map.put(Direction.WEST,  Shapes.join(box( 0, 4,  4,  4, 9, 12), box( 0, 6,  6,  4, 9, 10), BooleanOp.ONLY_FIRST));
    map.put(Direction.EAST,  Shapes.join(box(12, 4,  4, 16, 9, 12), box(12, 6,  6, 16, 9, 10), BooleanOp.ONLY_FIRST));
  });

  @Override
  protected int makeShapeKey(@Nullable NoWay _up, OneWay down, boolean north, boolean south, boolean west, boolean east) {
    return (down.canFlow() ? 0b00001 : 0) | (north ? 0b00010 : 0) | (south ? 0b00100 : 0) | (west ? 0b01000 : 0) | (east ? 0b10000 : 0);
  }

  public NormalChannelBlock(Properties props) {
    super(props, null, DOWN_1WAY);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return bounds[makeShapeKey(null, state.getValue(DOWN_1WAY), state.getValue(NORTH).canFlow(), state.getValue(SOUTH).canFlow(), state.getValue(WEST).canFlow(), state.getValue(EAST).canFlow())];
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(DOWN_1WAY, POWERED);
    DIRECTION_MAP.values().forEach(builder::add);
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
    for (OneWay down : new OneWay[] { OneWay.TRUE, OneWay.FALSE }) {
      VoxelShape center = down == OneWay.TRUE ? centerConnected : centerUnconnected;
      for (boolean north : bools) {
        VoxelShape northBounds = north ? SIDE_BOUNDS.get(Direction.NORTH) : northWall;
        for (boolean south : bools) {
          VoxelShape southBounds = south ? SIDE_BOUNDS.get(Direction.SOUTH) : southWall;
          for (boolean west : bools) {
            VoxelShape westBounds = west ? SIDE_BOUNDS.get(Direction.WEST) : westWall;
            for (boolean east : bools) {
              VoxelShape eastBounds = east ? SIDE_BOUNDS.get(Direction.EAST) : eastWall;
              shapes[makeShapeKey(null, down, north, south, west, east)] = Shapes.or(center, northBounds, southBounds, westBounds, eastBounds);
            }
          }
        }
      }
    }
    return shapes;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    if (!worldIn.isClientSide) {
      boolean isPowered = worldIn.hasNeighborSignal(pos);
      if (isPowered != state.getValue(POWERED)) {
        var down = Objects.requireNonNull(this.down);
        state = state.setValue(POWERED, isPowered)
          .setValue(down, getDefaultValue(down, isPowered && canConnect(worldIn, pos, Direction.DOWN)));
        worldIn.setBlock(pos, state, Block.UPDATE_CLIENTS);
      }
    }
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = this.defaultBlockState().setValue(POWERED, world.hasNeighborSignal(pos));
    Direction side = context.getClickedFace();

    // we cannot connect upwards, so done here
    if (side == Direction.DOWN) {
      return state;
    }

    // if placed on the top face, try to connect down
    if (side == Direction.UP && down != null) {
      return state.setValue(down, getDefaultValue(down, canConnect(world, pos, Direction.DOWN)));
    }

    return super.getStateForPlacement(context);
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
    // down only cares about connected or not
    if (facing == Direction.DOWN) {
      if (state.getValue(DOWN_1WAY).canFlow() && facingState.isAir()) {
        state = state.setValue(DOWN_1WAY, OneWay.FALSE);
      }
      return state;
    }

    // ignore changes from above, we can't connect there.
    if (facing == Direction.UP) {
      return state;
    }

    return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
  }
}
