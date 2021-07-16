package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;

import javax.annotation.Nullable;
import java.util.Random;

public class SlimeTree extends Tree {

  private final SlimeType foliageType;

  public SlimeTree(SlimeType foliageType) {
    this.foliageType = foliageType;
  }

  @Deprecated
  @Nullable
  @Override
  protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean largeHive) {
    return null;
  }

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Nullable
  private ConfiguredFeature<?, ?> getSlimeTreeFeature() {
    switch (this.foliageType) {
      case EARTH:
        return TinkerStructures.EARTH_SLIME_TREE;
      case SKY:
        return TinkerStructures.SKY_SLIME_TREE;
      case ENDER:
        return TinkerStructures.ENDER_SLIME_TREE;
      case BLOOD:
        return TinkerStructures.BLOOD_SLIME_FUNGUS;
      case ICHOR:
        return TinkerStructures.ICHOR_SLIME_FUNGUS;
    }

    return null;
  }

  @Override
  public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand) {
    ConfiguredFeature<?, ?> configuredFeature = this.getSlimeTreeFeature();
    if (configuredFeature == null) {
      return false;
    }
    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
    if (configuredFeature.generate(world, chunkGenerator, rand, pos)) {
      return true;
    }
    else {
      world.setBlockState(pos, state, 4);
      return false;
    }
  }
}
