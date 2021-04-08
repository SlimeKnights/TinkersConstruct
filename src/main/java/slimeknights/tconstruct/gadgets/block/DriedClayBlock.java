package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class DriedClayBlock extends Block {

  public DriedClayBlock() {
    super(Block.Properties.of(Material.STONE).strength(1.5F, 20.0F).sounds(BlockSoundGroup.STONE));
  }

}
