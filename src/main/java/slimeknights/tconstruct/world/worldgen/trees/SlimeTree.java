package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerStructures;
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
    switch (this.foliageType) {
      case BLUE:
        if (this.isIslandTree) {
          return TinkerStructures.tree.get().withConfiguration(TinkerStructures.blueSlimeIslandTreeConfig);
        } else {
          return TinkerStructures.tree.get().withConfiguration(TinkerStructures.blueSlimeTreeConfig);
        }
      case PURPLE:
        if (this.isIslandTree) {
          return TinkerStructures.tree.get().withConfiguration(TinkerStructures.purpleSlimeIslandTreeConfig);
        } else {
          return TinkerStructures.tree.get().withConfiguration(TinkerStructures.purpleSlimeTreeConfig);
        }
      case ORANGE:
        return TinkerStructures.tree.get().withConfiguration(TinkerStructures.magmaSlimeTreeConfig);
    }

    return null;
  }
}
