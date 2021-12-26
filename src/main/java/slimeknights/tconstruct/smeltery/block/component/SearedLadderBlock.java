package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import slimeknights.tconstruct.smeltery.tileentity.component.SmelteryComponentTileEntity;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.AbstractBlock.Properties;

public class SearedLadderBlock extends OrientableSmelteryBlock {
  /** Collision bounds, determines where this block stops the player */
  private static final Map<Direction,VoxelShape> COLLISION = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, VoxelShapes.join(VoxelShapes.block(), box( 2, 0,  0, 14, 16,  5), IBooleanFunction.ONLY_FIRST));
    map.put(Direction.SOUTH, VoxelShapes.join(VoxelShapes.block(), box( 2, 0, 11, 14, 16, 16), IBooleanFunction.ONLY_FIRST));
    map.put(Direction.WEST,  VoxelShapes.join(VoxelShapes.block(), box( 0, 0,  2,  5, 16, 14), IBooleanFunction.ONLY_FIRST));
    map.put(Direction.EAST,  VoxelShapes.join(VoxelShapes.block(), box(11, 0,  2, 16, 16, 14), IBooleanFunction.ONLY_FIRST));
  });
  private static final Map<Direction,VoxelShape> COLLISION_BOTTOM = new EnumMap<>(Direction.class);

  /** Selection bounds */

  private static final Map<Direction,VoxelShape> BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, VoxelShapes.join(
      VoxelShapes.block(),
      VoxelShapes.or(
        box(2,  0, 0, 14, 16, 2),
        box(2,  0, 0, 14,  2, 3),
        box(2,  4, 0, 14,  6, 3),
        box(2,  8, 0, 14, 10, 3),
        box(2, 12, 0, 14, 14, 3)),
      IBooleanFunction.ONLY_FIRST));
    map.put(Direction.SOUTH, VoxelShapes.join(
      VoxelShapes.block(),
      VoxelShapes.or(
        box(2,  0, 14, 14, 16, 16),
        box(2,  0, 13, 14,  2, 16),
        box(2,  4, 13, 14,  6, 16),
        box(2,  8, 13, 14, 10, 16),
        box(2, 12, 13, 14, 14, 16)),
      IBooleanFunction.ONLY_FIRST));
    map.put(Direction.WEST, VoxelShapes.join(
      VoxelShapes.block(),
      VoxelShapes.or(
        box(0,  0, 2, 2, 16, 14),
        box(0,  0, 2, 3,  2, 14),
        box(0,  4, 2, 3,  6, 14),
        box(0,  8, 2, 3, 10, 14),
        box(0, 12, 2, 3, 14, 14)),
      IBooleanFunction.ONLY_FIRST));
    map.put(Direction.EAST, VoxelShapes.join(
      VoxelShapes.block(),
      VoxelShapes.or(
        box(14,  0, 2, 16, 16, 14),
        box(13,  0, 2, 16,  2, 14),
        box(13,  4, 2, 16,  6, 14),
        box(13,  8, 2, 16, 10, 14),
        box(13, 12, 2, 16, 14, 14)),
      IBooleanFunction.ONLY_FIRST));
  });
  private static final Map<Direction,VoxelShape> BOUNDS_BOTTOM = new EnumMap<>(Direction.class);
  static {
    VoxelShape base = box(0, 0, 0, 16, 2, 16);
    for (Direction side : Plane.HORIZONTAL) {
      BOUNDS_BOTTOM.put(side, VoxelShapes.or(BOUNDS.get(side), base));
      COLLISION_BOTTOM.put(side, VoxelShapes.or(COLLISION.get(side), base));
    }
  }

  public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

  public SearedLadderBlock(Properties properties) {
    super(properties, SmelteryComponentTileEntity::new);
  }

  /** Bottom connections */

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BOTTOM);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    BlockState state = context.getLevel().getBlockState(context.getClickedPos().below());
    Direction direction = context.getHorizontalDirection().getOpposite();
    return this.defaultBlockState()
               .setValue(BOTTOM, !state.is(this) || state.getValue(FACING) != direction)
               .setValue(FACING, direction);
  }

  @Deprecated
  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.DOWN) {
      return state.setValue(BOTTOM, !facingState.is(this) || state.getValue(FACING) != facingState.getValue(FACING));
    }
    return state;
  }


  /** Bounds */

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return (state.getValue(BOTTOM) ? BOUNDS_BOTTOM : BOUNDS).get(state.getValue(FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return (state.getValue(BOTTOM) ? COLLISION_BOTTOM : COLLISION).get(state.getValue(FACING));
  }
}
