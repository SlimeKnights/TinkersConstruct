package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class PurpleSlimeTree extends Tree {

  private final boolean isIslandTree;

  public PurpleSlimeTree(boolean isIslandTree) {
    this.isIslandTree = isIslandTree;
  }

  @Override
  @Nullable
  protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
    if (this.isIslandTree) {
      return new SlimeTreeFeature(NoFeatureConfig::deserialize, true, 5, 4, TinkerCommons.congealed_green_slime.getDefaultState(), TinkerWorld.purple_slime_leaves.getDefaultState(), TinkerWorld.purple_slime_vine_middle.getDefaultState(), TinkerWorld.purple_slime_sapling, true);
    }
    else {
      return new SlimeTreeFeature(NoFeatureConfig::deserialize, true, 5, 4, TinkerCommons.congealed_green_slime.getDefaultState(), TinkerWorld.purple_slime_leaves.getDefaultState(), null, TinkerWorld.purple_slime_sapling, true);
    }
  }
}

