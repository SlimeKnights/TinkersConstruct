package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class SearedLadderBlock extends OrientableSmelteryBlock {
  /** Collision bounds, determines where this block stops the player */
  private static final Map<Direction,VoxelShape> COLLISION = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), createCuboidShape( 2, 0,  0, 14, 16,  5), BooleanBiFunction.ONLY_FIRST));
    map.put(Direction.SOUTH, VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), createCuboidShape( 2, 0, 11, 14, 16, 16), BooleanBiFunction.ONLY_FIRST));
    map.put(Direction.WEST,  VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), createCuboidShape( 0, 0,  2,  5, 16, 14), BooleanBiFunction.ONLY_FIRST));
    map.put(Direction.EAST,  VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), createCuboidShape(11, 0,  2, 16, 16, 14), BooleanBiFunction.ONLY_FIRST));
  });
  private static final Map<Direction,VoxelShape> COLLISION_BOTTOM = new EnumMap<>(Direction.class);

  /** Selection bounds */

  private static final Map<Direction,VoxelShape> BOUNDS = Util.make(new EnumMap<>(Direction.class), map -> {
    map.put(Direction.NORTH, VoxelShapes.combineAndSimplify(
      VoxelShapes.fullCube(),
      VoxelShapes.union(
        createCuboidShape(2,  0, 0, 14, 16, 2),
        createCuboidShape(2,  0, 0, 14,  2, 3),
        createCuboidShape(2,  4, 0, 14,  6, 3),
        createCuboidShape(2,  8, 0, 14, 10, 3),
        createCuboidShape(2, 12, 0, 14, 14, 3)),
      BooleanBiFunction.ONLY_FIRST));
    map.put(Direction.SOUTH, VoxelShapes.combineAndSimplify(
      VoxelShapes.fullCube(),
      VoxelShapes.union(
        createCuboidShape(2,  0, 14, 14, 16, 16),
        createCuboidShape(2,  0, 13, 14,  2, 16),
        createCuboidShape(2,  4, 13, 14,  6, 16),
        createCuboidShape(2,  8, 13, 14, 10, 16),
        createCuboidShape(2, 12, 13, 14, 14, 16)),
      BooleanBiFunction.ONLY_FIRST));
    map.put(Direction.WEST, VoxelShapes.combineAndSimplify(
      VoxelShapes.fullCube(),
      VoxelShapes.union(
        createCuboidShape(0,  0, 2, 2, 16, 14),
        createCuboidShape(0,  0, 2, 3,  2, 14),
        createCuboidShape(0,  4, 2, 3,  6, 14),
        createCuboidShape(0,  8, 2, 3, 10, 14),
        createCuboidShape(0, 12, 2, 3, 14, 14)),
      BooleanBiFunction.ONLY_FIRST));
    map.put(Direction.EAST, VoxelShapes.combineAndSimplify(
      VoxelShapes.fullCube(),
      VoxelShapes.union(
        createCuboidShape(14,  0, 2, 16, 16, 14),
        createCuboidShape(13,  0, 2, 16,  2, 14),
        createCuboidShape(13,  4, 2, 16,  6, 14),
        createCuboidShape(13,  8, 2, 16, 10, 14),
        createCuboidShape(13, 12, 2, 16, 14, 14)),
      BooleanBiFunction.ONLY_FIRST));
  });
  private static final Map<Direction,VoxelShape> BOUNDS_BOTTOM = new EnumMap<>(Direction.class);
  static {
    VoxelShape base = createCuboidShape(0, 0, 0, 16, 2, 16);
    for (Direction side : Type.HORIZONTAL) {
      BOUNDS_BOTTOM.put(side, VoxelShapes.union(BOUNDS.get(side), base));
      COLLISION_BOTTOM.put(side, VoxelShapes.union(COLLISION.get(side), base));
    }
  }

  public static final BooleanProperty BOTTOM = Properties.BOTTOM;

  public SearedLadderBlock(Settings properties) {
    super(properties, SmelteryComponentTileEntity::new);
  }

  /** Bottom connections */

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(BOTTOM);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    BlockState state = context.getWorld().getBlockState(context.getBlockPos().down());
    Direction direction = context.getPlayerFacing().getOpposite();
    return this.getDefaultState()
               .with(BOTTOM, !state.isOf(this) || state.get(FACING) != direction)
               .with(FACING, direction);
  }

  @Deprecated
  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (facing == Direction.DOWN) {
      return state.with(BOTTOM, !facingState.isOf(this) || state.get(FACING) != facingState.get(FACING));
    }
    return state;
  }


  /** Bounds */

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return (state.get(BOTTOM) ? BOUNDS_BOTTOM : BOUNDS).get(state.get(FACING));
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return (state.get(BOTTOM) ? COLLISION_BOTTOM : COLLISION).get(state.get(FACING));
  }
}
