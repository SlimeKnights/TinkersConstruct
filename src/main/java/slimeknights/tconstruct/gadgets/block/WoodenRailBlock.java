package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.RailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class WoodenRailBlock extends RailBlock {

  public WoodenRailBlock() {
    super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.2F).sound(SoundType.WOOD));
  }
}
