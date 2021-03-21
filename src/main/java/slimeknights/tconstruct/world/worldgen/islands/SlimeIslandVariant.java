package slimeknights.tconstruct.world.worldgen.islands;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;

import javax.annotation.Nullable;
import java.util.Locale;

public enum SlimeIslandVariant implements IStringSerializable {
  BLUE(0,
    TinkerWorld.skySlimeGrass.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState(),
    TinkerFluids.skySlime.getBlock(),
    createArray(TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
    TinkerWorld.skySlimeVine.get().getDefaultState(),
    createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
    TinkerStructures.BLUE_SLIME_TREE_ISLAND,
    BlockIgnoreStructureProcessor.STRUCTURE_BLOCK),

  GREEN(1,
    TinkerWorld.earthSlimeGrass.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState(),
    TinkerFluids.skySlime.getBlock(),
    createArray(TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState(), TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
    TinkerWorld.skySlimeVine.get().getDefaultState(),
    createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
    TinkerStructures.BLUE_SLIME_TREE_ISLAND,
    BlockIgnoreStructureProcessor.STRUCTURE_BLOCK),

  PURPLE(2,
    TinkerWorld.enderSlimeGrass.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState(),
    TinkerFluids.enderSlime.getBlock(),
    createArray(TinkerWorld.congealedSlime.get(SlimeType.ENDER).getDefaultState()),
    TinkerWorld.enderSlimeVine.get().getDefaultState(),
    createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
    TinkerStructures.PURPLE_SLIME_TREE_ISLAND,
    BlockIgnoreStructureProcessor.STRUCTURE_BLOCK),

  MAGMA(3,
    TinkerWorld.ichorSlimeGrass.get(SlimeGrassBlock.FoliageType.ICHOR).getDefaultState(),
    Blocks.LAVA,
    createArray(TinkerWorld.congealedSlime.get(SlimeType.ICHOR).getDefaultState(),
      TinkerWorld.congealedSlime.get(SlimeType.BLOOD).getDefaultState()),
    null,
    createArray(TinkerWorld.slimeFern.get(SlimeGrassBlock.FoliageType.ICHOR).getDefaultState(), TinkerWorld.slimeTallGrass.get(SlimeGrassBlock.FoliageType.ICHOR).getDefaultState()),
    TinkerStructures.MAGMA_SLIME_TREE,
    BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);

  private final int index;
  private final BlockState lakeBottom;
  private final BlockState lakeFluid;
  private final BlockState[] congealedSlime;
  @Nullable
  private final BlockState vine;
  private final BlockState[] tallGrass;
  private final ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> configuredTreeFeature;
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

  public BlockState getLakeBottom() {
    return this.lakeBottom;
  }

  public BlockState getLakeFluid() {
    return this.lakeFluid;
  }

  public BlockState[] getCongealedSlime() {
    return this.congealedSlime;
  }

  @Nullable
  public BlockState getVine() {
    return this.vine;
  }

  public BlockState[] getTallGrass() {
    return this.tallGrass;
  }

  @Override
  public String getString() {
    return this.toString().toLowerCase(Locale.US);
  }

  public int getIndex() {
    return this.index;
  }

  public ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> getConfiguredTreeFeature() {
    return this.configuredTreeFeature;
  }

  public StructureProcessor getStructureProcessor() {
    return this.structureProcessor;
  }

  public static SlimeIslandVariant getVariantFromIndex(int index) {
    switch (index) {
      case 0:
        return BLUE;
      case 1:
        return GREEN;
      case 2:
        return PURPLE;
      case 3:
        return MAGMA;
      default:
        throw new IllegalStateException("Unexpected variant: " + index);
    }
  }
}
