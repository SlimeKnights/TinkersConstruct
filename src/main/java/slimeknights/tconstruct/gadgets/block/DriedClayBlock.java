package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class DriedClayBlock extends Block {

  public DriedClayBlock() {
    super(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 20.0F).sound(SoundType.STONE));
  }

}
