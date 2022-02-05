package slimeknights.tconstruct.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.SkullBlock.Type;

public class PiglinHeadBlock extends SkullBlock {
  protected static final VoxelShape PIGLIN_SHAPE = Block.box(3, 0, 3, 13, 8, 13);
  public PiglinHeadBlock(Type type, Properties properties) {
    super(type, properties);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return PIGLIN_SHAPE;
  }
}
