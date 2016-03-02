package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.BlockTorch;

import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockStoneTorch extends BlockTorch {

  public BlockStoneTorch() {
    this.setHardness(0.0F);
    this.setLightLevel(0.9375F);
    this.setStepSound(soundTypeStone);

    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }
}
