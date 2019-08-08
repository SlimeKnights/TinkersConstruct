package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;

public class SlimeVineBlock extends VineBlock {

  protected final SlimeGrassBlock.FoliageType foliage;
  protected final SlimeVineBlock nextStage;

  public SlimeVineBlock(SlimeGrassBlock.FoliageType foliage, SlimeVineBlock nextStage) {
    super(Block.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.2F).sound(SoundType.PLANT));
    this.foliage = foliage;
    this.nextStage = nextStage;
  }

}
