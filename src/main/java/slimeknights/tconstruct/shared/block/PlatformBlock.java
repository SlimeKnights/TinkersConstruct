package slimeknights.tconstruct.shared.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.common.TinkerTags;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOWN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.EAST;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.NORTH;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SOUTH;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.UP;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST;

public class PlatformBlock extends Block implements SimpleWaterloggedBlock {
  private static final VoxelShape[] SHAPES = new VoxelShape[64];
  private static final BooleanProperty[] DIRECTIONS = { DOWN, UP, NORTH, SOUTH, WEST, EAST };

  /** Makes an index in the shapes map for the given set of booleans */
  private static int makeShapeIndex(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
    return (down    ? 0b000001 : 0)
           | (up    ? 0b000010 : 0)
           | (north ? 0b000100 : 0)
           | (south ? 0b001000 : 0)
           | (west  ? 0b010000 : 0)
           | (east  ? 0b100000 : 0);
  }

  static {
    // base boxes
    VoxelShape neither = Shapes.or(Block.box( 0, 0,  0,  2, 16,  2), Block.box(14, 0,  0, 16, 16,  2),
                        Block.box( 0, 0, 14,  2, 16, 16), Block.box(14, 0, 14, 16, 16, 16));
    VoxelShape bottom = Shapes.or(neither, Block.box(0, 0, 0, 16, 2, 16));
    VoxelShape topPlate = Block.box(0, 14, 0, 16, 16, 16);
    VoxelShape top = Shapes.or(neither, topPlate);
    VoxelShape both = Shapes.or(bottom, topPlate);

    // start building map for all orientations
    boolean[] bools = {false, true};
    VoxelShape northPlate = Block.box( 0, 2,  0, 16, 14,  1);
    VoxelShape southPlate = Block.box( 0, 2, 15, 16, 14, 16);
    VoxelShape westPlate  = Block.box( 0, 2,  0,  1, 14, 16);
    VoxelShape eastPlate  = Block.box(15, 2,  0, 16, 14, 16);
    for (boolean north : bools) {
      for (boolean east : bools) {
        for (boolean south : bools) {
          for (boolean west : bools) {
            for (boolean up : bools) {
              for (boolean down : bools) {
                // base shape
                VoxelShape shape;
                if (up) {
                  shape = down ? neither : bottom;
                } else {
                  shape = down ? top : both;
                }
                if (north) shape = Shapes.joinUnoptimized(shape, northPlate, BooleanOp.OR);
                if (south) shape = Shapes.joinUnoptimized(shape, southPlate, BooleanOp.OR);
                if (west)  shape = Shapes.joinUnoptimized(shape, westPlate, BooleanOp.OR);
                if (east)  shape = Shapes.joinUnoptimized(shape, eastPlate, BooleanOp.OR);
                // add to map
                int index = makeShapeIndex(up, down, north, east, south, west);
                SHAPES[index] = shape.optimize();
              }
            }
          }
        }
      }
    }
  }
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public PlatformBlock(Properties props) {
    super(props);
  }

  @Override
  protected void createBlockStateDefinition(Builder<Block,BlockState> builder) {
    builder.add(DIRECTIONS);
    builder.add(WATERLOGGED);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPES[makeShapeIndex(state.getValue(UP), state.getValue(DOWN), state.getValue(NORTH), state.getValue(EAST), state.getValue(SOUTH), state.getValue(WEST))];
  }

  @Override
  public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
    return Shapes.block();
  }

  /** Checks if the block has the given facing property */
  private static boolean facingConnected(Direction facing, BlockState state, DirectionProperty property) {
    return !state.hasProperty(property) || state.getValue(property) == facing;
  }

  /** Checks if the block should connect to the given side */
  private static boolean connected(Direction direction, BlockState state) {
    if (!state.is(TinkerTags.Blocks.PLATFORM_CONNECTIONS)) {
      return false;
    }
    // if the block has a side property, use that
    BooleanProperty sideProp = DIRECTIONS[direction.getOpposite().get3DDataValue()];
    if (state.hasProperty(sideProp)) {
      return state.getValue(sideProp);
    }
    // if there is a face property and it is not wall, not connected
    if (state.hasProperty(BlockStateProperties.ATTACH_FACE) && state.getValue(BlockStateProperties.ATTACH_FACE) != AttachFace.WALL) {
      return false;
    }
    // try relevant facing properties, if any are present must be facing this
    return facingConnected(direction, state, BlockStateProperties.HORIZONTAL_FACING)
           && facingConnected(direction, state, BlockStateProperties.FACING)
           && facingConnected(direction, state, BlockStateProperties.FACING_HOPPER);
  }

  /** Returns true if this platform connects vertically to the block */
  protected boolean verticalConnect(BlockState state) {
    return state.is(this);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockPos pos = context.getClickedPos();
    Level level = context.getLevel();
    BlockPos below = pos.below();
    BlockState belowState = level.getBlockState(below);
    BlockState state = this.defaultBlockState()
                           .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER)
                           .setValue(UP, verticalConnect(level.getBlockState(pos.above())))
                           .setValue(DOWN, verticalConnect(belowState) || belowState.isFaceSturdy(level, below, Direction.UP));
    for (Direction direction : Plane.HORIZONTAL) {
      state = state.setValue(DIRECTIONS[direction.get3DDataValue()], connected(direction, level.getBlockState(pos.relative(direction))));
    }
    return state;
  }

  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos selfPos, BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      level.scheduleTick(selfPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }

    if (direction == Direction.UP) {
      return state.setValue(UP, verticalConnect(neighbor));
    } else if (direction == Direction.DOWN) {
      return state.setValue(DOWN, verticalConnect(neighbor) || neighbor.isFaceSturdy(level, neighborPos, Direction.UP));
    }
    return state.setValue(DIRECTIONS[direction.get3DDataValue()], connected(direction, neighbor));
  }

  @Override
  public FluidState getFluidState(BlockState pState) {
    return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
  }
}
