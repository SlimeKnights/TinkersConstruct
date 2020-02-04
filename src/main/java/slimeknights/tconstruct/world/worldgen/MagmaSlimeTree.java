package slimeknights.tconstruct.world.worldgen;

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
  protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean bool) {
    return TinkerWorld.MAGMA_TREE.withConfiguration(TinkerWorld.MAGMA_TREE_CONFIG);
  }
}
