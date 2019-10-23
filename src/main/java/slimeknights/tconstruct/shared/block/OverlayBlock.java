package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;

/**
 * Represents all blocks that consist of a texture overlaid onto another texture, like ores
 * Basically it's just a block with a different render layer.
 */
public class OverlayBlock extends Block {

  public OverlayBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }

}
