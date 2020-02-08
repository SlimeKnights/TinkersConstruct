package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class BlueSlimeTree extends SlimeTree {

  private final boolean isIslandTree;

  public BlueSlimeTree() {
    this(false);
  }

  public BlueSlimeTree(boolean isIslandTree) {
    this.isIslandTree = isIslandTree;
  }

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Override
  @Nullable
  public ConfiguredFeature<SlimeTreeFeatureConfig, ?> getSlimeTreeFeature(Random random, boolean bool) {
    if (this.isIslandTree) {
      return TinkerWorld.TREE.withConfiguration(TinkerWorld.BLUE_SLIME_ISLAND_TREE_CONFIG);
    } else {
      return TinkerWorld.TREE.withConfiguration(TinkerWorld.BLUE_SLIME_TREE_CONFIG);
    }
  }
}
