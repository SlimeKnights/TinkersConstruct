package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;

import javax.annotation.Nullable;
import java.util.Random;

public class SlimeTree extends AbstractTreeGrower {

  private final SlimeType foliageType;

  public SlimeTree(SlimeType foliageType) {
    this.foliageType = foliageType;
  }

  @Deprecated
  @Nullable
  @Override
  protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random randomIn, boolean largeHive) {
    return null;
  }

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Nullable
  private ConfiguredFeature<?, ?> getSlimeTreeFeature() {
    switch (this.foliageType) {
      case EARTH:
        return TinkerStructures.earthSlimeTree;
      case SKY:
        return TinkerStructures.skySlimeTree;
      case ENDER:
        return TinkerStructures.enderSlimeTree;
      case BLOOD:
        return TinkerStructures.bloodSlimeFungus;
      case ICHOR:
        return TinkerStructures.ichorSlimeFungus;
    }

    return null;
  }

  @Override
  public boolean growTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand) {
    ConfiguredFeature<?, ?> configuredFeature = this.getSlimeTreeFeature();
    if (configuredFeature == null) {
      return false;
    }
    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
    if (configuredFeature.place(world, chunkGenerator, rand, pos)) {
      return true;
    }
    else {
      world.setBlock(pos, state, 4);
      return false;
    }
  }
}
