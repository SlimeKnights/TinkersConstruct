package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import slimeknights.tconstruct.smeltery.tileentity.component.SmelteryComponentTileEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SearedLadderBlock extends OrientableSmelteryBlock {
  /** Collision bounds, determines where this block stops the player */
  private static final Map<Direction,VoxelShape> COLLISION = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, Shapes.join(Shapes.block(), box( 2, 0,  0, 14, 16,  5), BooleanOp.ONLY_FIRST));
    map.put(Direction.SOUTH, Shapes.join(Shapes.block(), box( 2, 0, 11, 14, 16, 16), BooleanOp.ONLY_FIRST));
    map.put(Direction.WEST,  Shapes.join(Shapes.block(), box( 0, 0,  2,  5, 16, 14), BooleanOp.ONLY_FIRST));
    map.put(Direction.EAST,  Shapes.join(Shapes.block(), box(11, 0,  2, 16, 16, 14), BooleanOp.ONLY_FIRST));
  });
  private static final Map<Direction,VoxelShape> COLLISION_BOTTOM = new EnumMap<>(Direction.class);

  /** Selection bounds */

  private static final Map<Direction,VoxelShape> BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, Shapes.join(
      Shapes.block(),
      Shapes.or(
        box(2,  0, 0, 14, 16, 2),
        box(2,  0, 0, 14,  2, 3),
        box(2,  4, 0, 14,  6, 3),
        box(2,  8, 0, 14, 10, 3),
        box(2, 12, 0, 14, 14, 3)),
      BooleanOp.ONLY_FIRST));
    map.put(Direction.SOUTH, Shapes.join(
      Shapes.block(),
      Shapes.or(
        box(2,  0, 14, 14, 16, 16),
        box(2,  0, 13, 14,  2, 16),
        box(2,  4, 13, 14,  6, 16),
        box(2,  8, 13, 14, 10, 16),
        box(2, 12, 13, 14, 14, 16)),
      BooleanOp.ONLY_FIRST));
    map.put(Direction.WEST, Shapes.join(
      Shapes.block(),
      Shapes.or(
        box(0,  0, 2, 2, 16, 14),
        box(0,  0, 2, 3,  2, 14),
        box(0,  4, 2, 3,  6, 14),
        box(0,  8, 2, 3, 10, 14),
        box(0, 12, 2, 3, 14, 14)),
      BooleanOp.ONLY_FIRST));
    map.put(Direction.EAST, Shapes.join(
      Shapes.block(),
      Shapes.or(
        box(14,  0, 2, 16, 16, 14),
        box(13,  0, 2, 16,  2, 14),
        box(13,  4, 2, 16,  6, 14),
        box(13,  8, 2, 16, 10, 14),
        box(13, 12, 2, 16, 14, 14)),
      BooleanOp.ONLY_FIRST));
  });
  private static final Map<Direction,VoxelShape> BOUNDS_BOTTOM = new EnumMap<>(Direction.class);
  static {
    VoxelShape base = box(0, 0, 0, 16, 2, 16);
    for (Direction side : Plane.HORIZONTAL) {
      BOUNDS_BOTTOM.put(side, Shapes.or(BOUNDS.get(side), base));
      COLLISION_BOTTOM.put(side, Shapes.or(COLLISION.get(side), base));
    }
  }

  public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

  public SearedLadderBlock(Properties properties) {
    super(properties, SmelteryComponentTileEntity::new);
  }

  /** Bottom connections */

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BOTTOM);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = context.getLevel().getBlockState(context.getClickedPos().below());
    Direction direction = context.getHorizontalDirection().getOpposite();
    return this.defaultBlockState()
               .setValue(BOTTOM, !state.is(this) || state.getValue(FACING) != direction)
               .setValue(FACING, direction);
  }

  @Deprecated
  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.DOWN) {
      return state.setValue(BOTTOM, !facingState.is(this) || state.getValue(FACING) != facingState.getValue(FACING));
    }
    return state;
  }


  /** Bounds */

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return (state.getValue(BOTTOM) ? BOUNDS_BOTTOM : BOUNDS).get(state.getValue(FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return (state.getValue(BOTTOM) ? COLLISION_BOTTOM : COLLISION).get(state.getValue(FACING));
  }
}
