package slimeknights.tconstruct.shared.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import slimeknights.mantle.block.InventoryBlock;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Inventory block with directions and waterlogging
 */
public abstract class TableBlock extends InventoryBlock implements SimpleWaterloggedBlock {

  protected static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private static final VoxelShape TABLE_SHAPE = Shapes.or(
    Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D),  // top
    Block.box(0.0D, 0.0D, 0.0D, 4.0D, 15.0D, 4.0D),     // leg
    Block.box(12.0D, 0.0D, 0.0D, 16.0D, 15.0D, 4.0D),   // leg
    Block.box(12.0D, 0.0D, 12.0D, 16.0D, 15.0D, 16.0D), // leg
    Block.box(0.0D, 0.0D, 12.0D, 4.0D, 15.0D, 16.0D)).optimize();  // leg

  protected TableBlock(Properties builder) {
    super(builder);

    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
  }

  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return TABLE_SHAPE;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
  }

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED);
  }

  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
    return false;
  }
}
