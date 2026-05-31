package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Optional;

public final class SlimeTree {
  private SlimeTree() {}

  public static TreeGrower create(FoliageType foliageType) {
    return switch (foliageType) {
      case EARTH -> single("tconstruct_earth_slime", TinkerStructures.earthSlimeTree);
      case SKY -> single("tconstruct_sky_slime", TinkerStructures.skySlimeTree);
      case ENDER -> new TreeGrower("tconstruct_ender_slime", 0.85f, Optional.empty(), Optional.empty(), Optional.of(TinkerStructures.enderSlimeTree), Optional.of(TinkerStructures.enderSlimeTreeTall), Optional.empty(), Optional.empty());
      case BLOOD -> single("tconstruct_blood_slime", TinkerStructures.bloodSlimeFungus);
      case ICHOR -> single("tconstruct_ichor_slime", TinkerStructures.ichorSlimeFungus);
    };
  }

  private static TreeGrower single(String name, ResourceKey<ConfiguredFeature<?, ?>> feature) {
    return new TreeGrower(name, Optional.empty(), Optional.of(feature), Optional.empty());
  }
}
