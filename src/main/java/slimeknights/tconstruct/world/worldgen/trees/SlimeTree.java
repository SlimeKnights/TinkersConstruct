package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class SlimeTree extends SlimeTreeAbstract {

  private final SlimeGrassBlock.FoliageType foliageType;
  private final boolean isIslandTree;

  public SlimeTree(SlimeGrassBlock.FoliageType foliageType, boolean isIslandTree) {
    this.foliageType = foliageType;
    this.isIslandTree = isIslandTree;
  }

  /**
   * Get a {@link net.minecraft.world.gen.feature.ConfiguredFeature} of tree
   */
  @Override
  @Nullable
  public ConfiguredFeature<SlimeTreeFeatureConfig, ?> getSlimeTreeFeature(Random random, boolean bool) {
    switch (foliageType) {
      case BLUE:
        if (this.isIslandTree) {
          return TinkerWorld.TREE.get().withConfiguration(TinkerWorld.BLUE_SLIME_ISLAND_TREE_CONFIG);
        } else {
          return TinkerWorld.TREE.get().withConfiguration(TinkerWorld.BLUE_SLIME_TREE_CONFIG);
        }
      case PURPLE:
        if (this.isIslandTree) {
          return TinkerWorld.TREE.get().withConfiguration(TinkerWorld.PURPLE_SLIME_ISLAND_TREE_CONFIG);
        } else {
          return TinkerWorld.TREE.get().withConfiguration(TinkerWorld.PURPLE_SLIME_TREE_CONFIG);
        }
      case ORANGE:
        return TinkerWorld.TREE.get().withConfiguration(TinkerWorld.MAGMA_SLIME_TREE_CONFIG);
    }
    return null;
  }
}
