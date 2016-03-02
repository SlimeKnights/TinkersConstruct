package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.BlockRail;

import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockWoodRail extends BlockRail {

  public BlockWoodRail() {
    this.setHardness(0.2F); // much less than vanilla
    this.setStepSound(soundTypeWood);

    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }
}
