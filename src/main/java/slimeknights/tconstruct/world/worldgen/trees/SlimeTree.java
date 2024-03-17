package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.annotation.Nullable;

public class SlimeTree extends AbstractTreeGrower {

  private final FoliageType foliageType;

  public SlimeTree(FoliageType foliageType) {
    this.foliageType = foliageType;
  }

  @Deprecated
  @Nullable
  @Override
  protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomIn, boolean largeHive) {
    return null;
  }

  /**
   * Get a {@link ConfiguredFeature} of tree
   */
  private ConfiguredFeature<?, ?> getSlimeTreeFeature() {
    return switch (this.foliageType) {
      case EARTH -> TinkerStructures.earthSlimeTree.get();
      case SKY -> TinkerStructures.skySlimeTree.get();
      case ENDER -> TinkerStructures.enderSlimeTree.get();
      case BLOOD -> TinkerStructures.bloodSlimeFungus.get();
      case ICHOR -> TinkerStructures.ichorSlimeFungus.get();
    };
  }

  @Override
  public boolean growTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource rand) {
    ConfiguredFeature<?, ?> configuredFeature = this.getSlimeTreeFeature();
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
