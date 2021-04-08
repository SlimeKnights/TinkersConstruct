package slimeknights.tconstruct.smeltery.block.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SearedGlassBlock extends SearedBlock {

  public SearedGlassBlock(Settings properties) {
    super(properties);
  }

  @Deprecated
  @Override
  @Environment(EnvType.CLIENT)
  public float getAmbientOcclusionLightLevel(BlockState state, BlockView worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView reader, BlockPos pos) {
    return true;
  }

  @Deprecated
  @Override
  @Environment(EnvType.CLIENT)
  public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
    return adjacentBlockState.getBlock() == this || super.isSideInvisible(state, adjacentBlockState, side);
  }
}
