package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class SlimeTreeAbstract extends Tree {

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Nullable
  @Override
  protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean bool) {
    return null;
  }

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  protected abstract ConfiguredFeature<SlimeTreeFeatureConfig, ?> getSlimeTreeFeature(Random randomIn, boolean bool);

  @Override
  public boolean place(IWorld worldIn, ChunkGenerator<?> chunkGenerator, BlockPos blockPos, BlockState state, Random random) {
    ConfiguredFeature<SlimeTreeFeatureConfig, ?> configuredfeature = this.getSlimeTreeFeature(random, this.func_230140_a_(worldIn, blockPos));
    if (configuredfeature == null) {
      return false;
    } else {
      worldIn.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
      if (configuredfeature.place(worldIn, chunkGenerator, random, blockPos)) {
        return true;
      } else {
        worldIn.setBlockState(blockPos, state, 4);
        return false;
      }
    }
  }

  private boolean func_230140_a_(IWorld world, BlockPos blockPos) {
    for (BlockPos blockpos : BlockPos.Mutable.getAllInBoxMutable(blockPos.down().north(2).west(2), blockPos.up().south(2).east(2))) {
      if (world.getBlockState(blockpos).isIn(BlockTags.FLOWERS)) {
        return true;
      }
    }

    return false;
  }
}
