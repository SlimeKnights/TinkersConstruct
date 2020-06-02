package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class PurpleSlimeTree extends SlimeTree {

  private final boolean isIslandTree;

  public PurpleSlimeTree() {
    this(false);
  }

  public PurpleSlimeTree(boolean isIslandTree) {
    this.isIslandTree = isIslandTree;
  }

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Override
  @Nullable
  public ConfiguredFeature<SlimeTreeFeatureConfig, ?> getSlimeTreeFeature(Random random, boolean bool) {
    if (this.isIslandTree) {
      return TinkerWorld.TREE.get().withConfiguration(TinkerWorld.PURPLE_SLIME_ISLAND_TREE_CONFIG);
    } else {
      return TinkerWorld.TREE.get().withConfiguration(TinkerWorld.PURPLE_SLIME_TREE_CONFIG);
    }
  }
}
