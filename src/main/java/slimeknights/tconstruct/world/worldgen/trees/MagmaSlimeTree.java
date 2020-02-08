package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class MagmaSlimeTree extends SlimeTree {

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Override
  @Nullable
  public ConfiguredFeature<SlimeTreeFeatureConfig, ?> getSlimeTreeFeature(Random random, boolean bool) {
    return TinkerWorld.TREE.withConfiguration(TinkerWorld.MAGMA_SLIME_TREE_CONFIG);
  }
}
