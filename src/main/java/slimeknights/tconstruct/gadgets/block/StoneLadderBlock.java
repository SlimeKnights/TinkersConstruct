package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class StoneLadderBlock extends LadderBlock {

  public StoneLadderBlock() {
    super(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.1F).sound(SoundType.STONE));
  }

}
