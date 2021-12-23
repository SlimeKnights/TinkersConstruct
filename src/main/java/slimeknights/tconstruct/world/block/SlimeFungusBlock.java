package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FungusBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeFungusConfig;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.function.Supplier;

/** Update of fungus that grows on slime soil instead */
public class SlimeFungusBlock extends FungusBlock {
  public SlimeFungusBlock(Properties properties, Supplier<ConfiguredFeature<HugeFungusConfig,?>> fungusFeature) {
    super(properties, fungusFeature);
  }

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return state.isIn(TinkerTags.Blocks.SLIMY_SOIL);
  }

  @Override
  public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return worldIn.getBlockState(pos.down()).isIn(TinkerTags.Blocks.SLIMY_SOIL);
  }
}
