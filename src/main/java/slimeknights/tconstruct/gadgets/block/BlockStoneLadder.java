package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.BlockLadder;

import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockStoneLadder extends BlockLadder {

  public BlockStoneLadder() {
    this.setHardness(0.1F); // much less than stone ladder
    this.setStepSound(soundTypeStone);

    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }
}
