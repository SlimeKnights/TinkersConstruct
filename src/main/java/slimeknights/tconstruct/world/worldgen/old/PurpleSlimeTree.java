package slimeknights.tconstruct.world.worldgen.old;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import slimeknights.tconstruct.blocks.WorldBlocks;

import javax.annotation.Nullable;
import java.util.Random;

public class PurpleSlimeTree {

  private final boolean isIslandTree;

  public PurpleSlimeTree(boolean isIslandTree) {
    this.isIslandTree = isIslandTree;
  }

  @Nullable
  protected SlimeTreeFeature getTreeFeature(Random random) {
    if (this.isIslandTree) {
      return new SlimeTreeFeature(NoFeatureConfig::deserialize, true, 5, 4, WorldBlocks.congealed_green_slime.getDefaultState(), WorldBlocks.purple_slime_leaves.getDefaultState(), WorldBlocks.purple_slime_vine_middle.getDefaultState(), WorldBlocks.purple_slime_sapling, true);
    } else {
      return new SlimeTreeFeature(NoFeatureConfig::deserialize, true, 5, 4, WorldBlocks.congealed_green_slime.getDefaultState(), WorldBlocks.purple_slime_leaves.getDefaultState(), null, WorldBlocks.purple_slime_sapling, true);
    }
  }
}

