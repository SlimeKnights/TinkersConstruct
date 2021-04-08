package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class TinkersAnvilBlock extends TinkerStationBlock {
  private static final VoxelShape PART_BASE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
  private static final VoxelShape X_AXIS_AABB = VoxelShapes.union(
    PART_BASE,
    Block.createCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D),
    Block.createCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D),
    Block.createCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D));
  private static final VoxelShape Z_AXIS_AABB = VoxelShapes.union(
    PART_BASE,
    Block.createCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D),
    Block.createCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D),
    Block.createCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D));

  public TinkersAnvilBlock(Settings builder, int slotCount) {
    super(builder, slotCount);
  }
  @Override
  @Deprecated
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    Direction direction = state.get(FACING);
    return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
  }
}
