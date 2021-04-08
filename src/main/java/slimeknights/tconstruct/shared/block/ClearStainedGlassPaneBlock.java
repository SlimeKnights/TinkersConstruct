package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;

import org.jetbrains.annotations.Nullable;

public class ClearStainedGlassPaneBlock extends ClearGlassPaneBlock {

  private final GlassColor glassColor;
  public ClearStainedGlassPaneBlock(Settings builder, GlassColor glassColor) {
    super(builder);
    this.glassColor = glassColor;
  }

  @Nullable
  @Override
  public float[] getBeaconColorMultiplier(BlockState state, WorldView world, BlockPos pos, BlockPos beaconPos) {
    return this.glassColor.getRgb();
  }
}
