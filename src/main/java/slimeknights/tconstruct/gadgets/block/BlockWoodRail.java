package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;

import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockWoodRail extends BlockRail {

  public BlockWoodRail() {
    this.setHardness(0.2F); // much less than vanilla
    this.setSoundType(SoundType.WOOD);

    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }
}
