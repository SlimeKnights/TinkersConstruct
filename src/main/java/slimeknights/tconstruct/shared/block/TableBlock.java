package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import slimeknights.mantle.block.InventoryBlock;

/**
 * Inventory block with directions and waterlogging
 */
public abstract class TableBlock extends InventoryBlock implements Waterloggable {

  protected static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

  private static final VoxelShape TABLE_SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D),  // top
    Block.createCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 15.0D, 4.0D),     // leg
    Block.createCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 15.0D, 4.0D),   // leg
    Block.createCuboidShape(12.0D, 0.0D, 12.0D, 16.0D, 15.0D, 16.0D), // leg
    Block.createCuboidShape(0.0D, 0.0D, 12.0D, 4.0D, 15.0D, 16.0D)).simplify();  // leg

  protected TableBlock(Settings builder) {
    super(builder);

    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
  }

  @Override
  @Deprecated
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return TABLE_SHAPE;
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext context) {
    boolean flag = context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER;
    return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()).with(WATERLOGGED, flag);
  }

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, BlockRotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
    return state.rotate(mirrorIn.getRotation(state.get(FACING)));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED);
  }

  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
  }
}
