package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TinkersAnvilBlock extends TinkerStationBlock {
  private static final VoxelShape PART_BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
  private static final VoxelShape X_AXIS_AABB = Shapes.or(
    PART_BASE,
    Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D),
    Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D),
    Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D));
  private static final VoxelShape Z_AXIS_AABB = Shapes.or(
    PART_BASE,
    Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D),
    Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D),
    Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D));

  public TinkersAnvilBlock(Properties builder, int slotCount) {
    super(builder, slotCount);
  }

  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(FACING);
    return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
  }
}
