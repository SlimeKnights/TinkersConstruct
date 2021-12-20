package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class PiglinHeadBlock extends SkullBlock {
  protected static final VoxelShape PIGLIN_SHAPE = Block.makeCuboidShape(3, 0, 3, 13, 8, 13);
  public PiglinHeadBlock(ISkullType type, Properties properties) {
    super(type, properties);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return PIGLIN_SHAPE;
  }
}
