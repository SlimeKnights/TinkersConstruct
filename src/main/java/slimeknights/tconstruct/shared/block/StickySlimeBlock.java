package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockState;

public class StickySlimeBlock extends net.minecraft.block.SlimeBlock {

  public StickySlimeBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean isStickyBlock(BlockState state) {
    return true;
  }

}
