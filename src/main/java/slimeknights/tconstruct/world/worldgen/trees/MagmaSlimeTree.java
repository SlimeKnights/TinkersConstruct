package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class MagmaSlimeTree extends Tree {

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Override
  @Nullable
  public ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean bool) {
    return TinkerWorld.TREE.withConfiguration(TinkerWorld.MAGMA_SLIME_TREE_CONFIG);
  }
}
