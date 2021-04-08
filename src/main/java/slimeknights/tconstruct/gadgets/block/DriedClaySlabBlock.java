package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Material;
import net.minecraft.block.SlabBlock;
import net.minecraft.sound.BlockSoundGroup;

public class DriedClaySlabBlock extends SlabBlock {

  public DriedClaySlabBlock() {
    // TODO: constructor properties
    super(Settings.of(Material.STONE).strength(3.0F, 20.0F).sounds(BlockSoundGroup.STONE).luminance(s -> 7));
  }

}
