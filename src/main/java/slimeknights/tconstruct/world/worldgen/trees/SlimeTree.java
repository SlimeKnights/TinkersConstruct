package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class SlimeTree extends Tree {

  private final SlimeGrassBlock.FoliageType foliageType;

  public SlimeTree(SlimeGrassBlock.FoliageType foliageType) {
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
  public ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> getSlimeTreeFeature(Random randomIn, boolean largeHive) {
    switch (this.foliageType) {
      case SKY:
        return TinkerStructures.SKY_SLIME_TREE;
      case ENDER:
        return TinkerStructures.ENDER_SLIME_TREE;
      case BLOOD:
        return TinkerStructures.BLOOD_SLIME_TREE;
      case ICHOR:
        return TinkerStructures.ICHOR_SLIME_TREE;
    }

    return null;
  }

  @Override
  public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand) {
    ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> configuredFeature = this.getSlimeTreeFeature(rand, this.hasNearbyFlora(world, pos));
    if (configuredFeature == null) {
      return false;
    }
    else {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);

      configuredFeature.config.forcePlacement();

      if (configuredFeature.generate(world, chunkGenerator, rand, pos)) {
        return true;
      }
      else {
        world.setBlockState(pos, state, 4);
        return false;
      }
    }
  }

  private boolean hasNearbyFlora(IWorld world, BlockPos pos) {
    for (BlockPos blockpos : BlockPos.Mutable.getAllInBoxMutable(pos.down().north(2).west(2), pos.up().south(2).east(2))) {
      if (world.getBlockState(blockpos).isIn(BlockTags.FLOWERS)) {
        return true;
      }
    }

    return false;
  }

}
