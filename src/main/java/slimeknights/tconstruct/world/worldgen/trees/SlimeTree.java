package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
  protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random randomIn, boolean largeHive) {
    return null;
  }

  /**
   * Get a {@link ConfiguredFeature} of tree
   */
  @Nullable
  private ConfiguredFeature<?, ?> getSlimeTreeFeature() {
    switch (this.foliageType) {
      case EARTH:
        return TinkerStructures.earthSlimeTree.get();
      case SKY:
        return TinkerStructures.skySlimeTree.get();
      case ENDER:
        return TinkerStructures.enderSlimeTree.get();
      case BLOOD:
        return TinkerStructures.bloodSlimeFungus.get();
      case ICHOR:
        return TinkerStructures.ichorSlimeFungus.get();
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
