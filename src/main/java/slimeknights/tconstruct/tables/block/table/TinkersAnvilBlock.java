package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class TinkersAnvilBlock extends TinkerStationBlock {
  private static final VoxelShape PART_BASE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
  private static final VoxelShape X_AXIS_AABB = VoxelShapes.or(
    PART_BASE,
    Block.makeCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D),
    Block.makeCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D),
    Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D));
  private static final VoxelShape Z_AXIS_AABB = VoxelShapes.or(
    PART_BASE,
    Block.makeCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D),
    Block.makeCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D),
    Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D));

  public TinkersAnvilBlock(Properties builder, int slotCount) {
    super(builder, slotCount);
  }
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    Direction direction = state.get(FACING);
    return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
  }
}
