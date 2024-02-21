package slimeknights.tconstruct.shared.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulGlassPaneBlock extends ClearGlassPaneBlock {
  public SoulGlassPaneBlock(Properties properties) {
    super(properties);
  }

  @Override
  public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return Shapes.empty();
  }

  @Override
  public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
    return super.getCollisionShape(pState, pReader, pPos, CollisionContext.empty());
  }
}
