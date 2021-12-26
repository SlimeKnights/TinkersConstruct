package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import slimeknights.mantle.block.InventoryBlock;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * Inventory block with directions and waterlogging
 */
public abstract class TableBlock extends InventoryBlock implements IWaterLoggable {

  protected static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private static final VoxelShape TABLE_SHAPE = VoxelShapes.or(
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
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return TABLE_SHAPE;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
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
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED);
  }

  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
    return false;
  }
}
