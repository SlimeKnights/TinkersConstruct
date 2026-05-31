package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SearedSoulGlassBlock extends SearedGlassBlock {
  public SearedSoulGlassBlock(Properties properties) {
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
