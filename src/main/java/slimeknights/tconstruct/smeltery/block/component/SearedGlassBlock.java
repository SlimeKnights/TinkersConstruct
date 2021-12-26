package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.block.AbstractBlock.Properties;

public class SearedGlassBlock extends SearedBlock {

  public SearedGlassBlock(Properties properties) {
    super(properties);
  }

  @Deprecated
  @Override
  @OnlyIn(Dist.CLIENT)
  public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  @Deprecated
  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
    return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);
  }
}
