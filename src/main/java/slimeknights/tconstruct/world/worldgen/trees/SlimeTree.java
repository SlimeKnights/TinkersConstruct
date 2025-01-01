package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.FoliageType;

public class SlimeTree extends AbstractTreeGrower {

  private final FoliageType foliageType;

  public SlimeTree(FoliageType foliageType) {
    this.foliageType = foliageType;
  }

  @Override
  protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean largeHive) {
    return switch (this.foliageType) {
      case EARTH -> TinkerStructures.earthSlimeTree;
      case SKY -> TinkerStructures.skySlimeTree;
      case ENDER -> random.nextFloat() < 0.85f ? TinkerStructures.enderSlimeTreeTall : TinkerStructures.enderSlimeTree;
      case BLOOD -> TinkerStructures.bloodSlimeFungus;
      case ICHOR -> TinkerStructures.ichorSlimeFungus;
    };
  }
}
