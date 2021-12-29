package slimeknights.tconstruct.gadgets.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class DriedClaySlabBlock extends SlabBlock {

  public DriedClaySlabBlock() {
    // TODO: constructor properties
    super(Properties.of(Material.STONE).strength(3.0F, 20.0F).sound(SoundType.STONE).lightLevel(s -> 7));
  }

}
