package slimeknights.tconstruct.world.worldgen.islands;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;

@Getter
public enum SlimeIslandVariant implements StringIdentifiable {
  SKY(0,
    TinkerWorld.skySlimeGrass.get(FoliageType.SKY).getDefaultState(),
    Objects.requireNonNull(TinkerFluids.skySlime.getBlock()),
    createArray(TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
    TinkerWorld.skySlimeVine.get().getDefaultState(),
    createArray(TinkerWorld.slimeFern.get(FoliageType.SKY).getDefaultState(), TinkerWorld.slimeTallGrass.get(FoliageType.SKY).getDefaultState()),
    TinkerStructures.SKY_SLIME_ISLAND_TREE,
    BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS),

  EARTH(1,
    TinkerWorld.earthSlimeGrass.get(FoliageType.SKY).getDefaultState(),
    TinkerFluids.skySlime.getBlock(),
    createArray(TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
    TinkerWorld.skySlimeVine.get().getDefaultState(),
    createArray(TinkerWorld.slimeFern.get(FoliageType.SKY).getDefaultState(), TinkerWorld.slimeTallGrass.get(FoliageType.SKY).getDefaultState()),
    TinkerStructures.SKY_SLIME_ISLAND_TREE,
    BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS),

  ENDER(2,
    TinkerWorld.enderSlimeGrass.get(FoliageType.ENDER).getDefaultState(),
    Objects.requireNonNull(TinkerFluids.enderSlime.getBlock()),
    createArray(TinkerWorld.congealedSlime.get(SlimeType.ENDER).getDefaultState()), TinkerWorld.enderSlimeVine.get().getDefaultState(),
    createArray(TinkerWorld.slimeFern.get(FoliageType.ENDER).getDefaultState(), TinkerWorld.slimeTallGrass.get(FoliageType.ENDER).getDefaultState()),
    TinkerStructures.ENDER_SLIME_ISLAND_TREE,
    BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS),

  BLOOD(3,
    TinkerWorld.ichorSlimeGrass.get(FoliageType.BLOOD).getDefaultState(),
    Objects.requireNonNull(TinkerFluids.magmaCream.getBlock()),
    createArray(TinkerWorld.congealedSlime.get(SlimeType.ICHOR).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeType.BLOOD).getDefaultState()),
    null,
    createArray(TinkerWorld.slimeFern.get(FoliageType.BLOOD).getDefaultState(), TinkerWorld.slimeTallGrass.get(FoliageType.BLOOD).getDefaultState()),
    TinkerStructures.BLOOD_SLIME_TREE,
    BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);

  @Getter
  private final int index;
  @Getter
  private final BlockState lakeBottom;
  @Getter
  private final BlockState lakeFluid;
  @Getter
  private final BlockState[] congealedSlime;
  @Nullable
  @Getter
  private final BlockState vine;
  @Getter
  private final BlockState[] tallGrass;
  @Getter
  private final ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> configuredTreeFeature;
  @Getter
  private final StructureProcessor structureProcessor;

  SlimeIslandVariant(int index, BlockState lakeBottom, Block lakeFluid, BlockState[] congealedSlime, @Nullable BlockState vine, BlockState[] tallGrass, ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> configuredTreeFeature, StructureProcessor structureProcessor) {
    this.index = index;
    this.lakeBottom = lakeBottom;
    this.lakeFluid = lakeFluid.getDefaultState();
    this.congealedSlime = congealedSlime;
    this.vine = vine;
    this.tallGrass = tallGrass;
    this.configuredTreeFeature = configuredTreeFeature;
    this.structureProcessor = structureProcessor;
  }

  private static BlockState[] createArray(BlockState... states) {
    return states;
  }

  @Override
  public String asString() {
    return this.toString().toLowerCase(Locale.US);
  }

  public static SlimeIslandVariant getVariantFromIndex(int index) {
    switch (index) {
      case 0:
        return SKY;
      case 1:
        return EARTH;
      case 2:
        return ENDER;
      case 3:
        return BLOOD;
      default:
        throw new IllegalStateException("Unexpected variant: " + index);
    }
  }
}
