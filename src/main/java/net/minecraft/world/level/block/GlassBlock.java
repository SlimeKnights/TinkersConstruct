package slimeknights.tconstruct.compat.minecraft.world.level.block;

import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Compatibility alias for the pre-1.21 glass block name. */
public class GlassBlock extends TransparentBlock {
  public GlassBlock(BlockBehaviour.Properties properties) {
    super(properties);
  }
}
