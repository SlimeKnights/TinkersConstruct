package slimeknights.tconstruct.gadgets.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class DriedClayBlock extends Block {

  public DriedClayBlock() {
    super(Block.Properties.of(Material.STONE).strength(1.5F, 20.0F).sound(SoundType.STONE));
  }

}
