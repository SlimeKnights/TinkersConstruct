package slimeknights.tconstruct.world.worldgen.trees;
/*
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
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class SlimeTreeAbstract extends Tree {

  @Nullable
  @Override
  protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean bool) {
    return null;
  }

  @Nullable
  protected abstract ConfiguredFeature<SlimeTreeFeatureConfig, ?> getSlimeTreeFeature(Random randomIn, boolean bool);

  @Override
  public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand) {
    ConfiguredFeature<SlimeTreeFeatureConfig, ?> configuredFeature = this.getSlimeTreeFeature(rand, this.func_230140_a_(world, pos));
    if (configuredFeature == null) {
      return false;
    } else {
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
      if (configuredFeature.func_236265_a_(world, world.func_241112_a_(), chunkGenerator, rand, pos)) {
        return true;
      } else {
        world.setBlockState(pos, state, 4);
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
*/
