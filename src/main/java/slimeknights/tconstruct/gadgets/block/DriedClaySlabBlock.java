package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.minecraft.block.AbstractBlock.Properties;

public class DriedClaySlabBlock extends SlabBlock {

  public DriedClaySlabBlock() {
    // TODO: constructor properties
    super(Properties.of(Material.STONE).strength(3.0F, 20.0F).sound(SoundType.STONE).lightLevel(s -> 7));
  }

}
