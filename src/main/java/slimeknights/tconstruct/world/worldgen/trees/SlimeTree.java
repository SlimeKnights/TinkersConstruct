package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;

import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class SlimeTree extends SaplingGenerator {

  private final SlimeGrassBlock.FoliageType foliageType;

  public SlimeTree(SlimeGrassBlock.FoliageType foliageType) {
    this.foliageType = foliageType;
  }

  @Deprecated
  @Nullable
  @Override
  protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random randomIn, boolean largeHive) {
    return null;
  }

  /**
   * Get a {@link ConfiguredFeature} of tree
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
  public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand) {
    ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> configuredFeature = this.getSlimeTreeFeature(rand, this.method_24282(world, pos));
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

  private boolean method_24282(WorldAccess world, BlockPos pos) {
    for (BlockPos blockpos : BlockPos.Mutable.iterate(pos.down().north(2).west(2), pos.up().south(2).east(2))) {
      if (world.getBlockState(blockpos).isIn(BlockTags.FLOWERS)) {
        return true;
      }
    }

    return false;
  }

}
