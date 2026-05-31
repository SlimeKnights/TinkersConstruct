package slimeknights.tconstruct.shared.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.compat.minecraft.world.level.block.GlassBlock;

public class SoulGlassBlock extends GlassBlock {
  public SoulGlassBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
    return Shapes.block();
  }

  @Override
  protected boolean isPathfindable(BlockState pState, PathComputationType pType) {
    return false;
  }
}
