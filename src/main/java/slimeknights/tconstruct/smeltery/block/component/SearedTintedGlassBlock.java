package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class SearedTintedGlassBlock extends SearedGlassBlock {
  public SearedTintedGlassBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
    return false;
  }

  @Override
  public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
    return world.getMaxLightLevel();
  }
}
