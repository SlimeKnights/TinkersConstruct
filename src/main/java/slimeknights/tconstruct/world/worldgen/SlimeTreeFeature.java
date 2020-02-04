package slimeknights.tconstruct.world.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractSmallTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class SlimeTreeFeature extends AbstractSmallTreeFeature<TreeFeatureConfig> {

  public SlimeTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> config) {
    super(config);
  }

  @Override
  protected boolean func_225557_a_(IWorldGenerationReader worldIn, Random random, BlockPos blockPos, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBox, TreeFeatureConfig treeFeatureConfig) {
    int i = treeFeatureConfig.baseHeight + random.nextInt(treeFeatureConfig.heightRandA + 1) + random.nextInt(treeFeatureConfig.heightRandB + 1);
    int j = treeFeatureConfig.trunkHeight >= 0 ? treeFeatureConfig.trunkHeight + random.nextInt(treeFeatureConfig.trunkHeightRandom + 1) : i - (treeFeatureConfig.foliageHeight + random.nextInt(treeFeatureConfig.foliageHeightRandom + 1));
    int k = treeFeatureConfig.foliagePlacer.func_225573_a_(random, j, i, treeFeatureConfig);
    Optional<BlockPos> optional = this.func_227212_a_(worldIn, i, j, k, blockPos, treeFeatureConfig);
    if (!optional.isPresent()) {
      return false;
    } else {
      BlockPos blockpos = optional.get();
      this.setDirtAt(worldIn, blockpos.down(), blockpos);
      treeFeatureConfig.foliagePlacer.func_225571_a_(worldIn, random, treeFeatureConfig, i, j, k, blockpos, p_225557_5_);
      this.func_227213_a_(worldIn, random, i, blockpos, treeFeatureConfig.trunkTopOffset + random.nextInt(treeFeatureConfig.trunkTopOffsetRandom + 1), p_225557_4_, boundingBox, treeFeatureConfig);
      return true;
    }
  }
}
