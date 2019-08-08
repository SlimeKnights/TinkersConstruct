package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

import javax.annotation.Nullable;
import java.util.Random;

public class SlimeTree extends Tree {

  @Override
  @Nullable
  protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
    return new TreeFeature(NoFeatureConfig::deserialize, true);
  }
}
