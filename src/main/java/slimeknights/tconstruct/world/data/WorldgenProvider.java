package slimeknights.tconstruct.world.data;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration.TreeConfigurationBuilder;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.FrequencyReductionMethod;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.GeodeItemObject;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock.VineStage;
import slimeknights.tconstruct.world.worldgen.islands.IslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.ExtraRootVariantPlacer;
import slimeknights.tconstruct.world.worldgen.trees.LeaveVineDecorator;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.minecraft.core.HolderSet.direct;
import static slimeknights.tconstruct.TConstruct.getResource;
import static slimeknights.tconstruct.world.TinkerStructures.bloodIsland;
import static slimeknights.tconstruct.world.TinkerStructures.bloodSlimeFungus;
import static slimeknights.tconstruct.world.TinkerStructures.bloodSlimeIslandFungus;
import static slimeknights.tconstruct.world.TinkerStructures.clayIsland;
import static slimeknights.tconstruct.world.TinkerStructures.earthSlimeIsland;
import static slimeknights.tconstruct.world.TinkerStructures.earthSlimeIslandTree;
import static slimeknights.tconstruct.world.TinkerStructures.earthSlimeTree;
import static slimeknights.tconstruct.world.TinkerStructures.endSkyIsland;
import static slimeknights.tconstruct.world.TinkerStructures.endSlimeIsland;
import static slimeknights.tconstruct.world.TinkerStructures.enderSlimeTree;
import static slimeknights.tconstruct.world.TinkerStructures.enderSlimeTreeTall;
import static slimeknights.tconstruct.world.TinkerStructures.ichorSlimeFungus;
import static slimeknights.tconstruct.world.TinkerStructures.netherOceanIsland;
import static slimeknights.tconstruct.world.TinkerStructures.overworldOceanIsland;
import static slimeknights.tconstruct.world.TinkerStructures.overworldSkyIsland;
import static slimeknights.tconstruct.world.TinkerStructures.skySlimeIsland;
import static slimeknights.tconstruct.world.TinkerStructures.skySlimeIslandTree;
import static slimeknights.tconstruct.world.TinkerStructures.skySlimeTree;
import static slimeknights.tconstruct.world.TinkerStructures.slimeFungus;
import static slimeknights.tconstruct.world.TinkerStructures.slimeTree;
import static slimeknights.tconstruct.world.TinkerWorld.configuredEarthGeode;
import static slimeknights.tconstruct.world.TinkerWorld.configuredEnderGeode;
import static slimeknights.tconstruct.world.TinkerWorld.configuredIchorGeode;
import static slimeknights.tconstruct.world.TinkerWorld.configuredLargeCobaltOre;
import static slimeknights.tconstruct.world.TinkerWorld.configuredSkyGeode;
import static slimeknights.tconstruct.world.TinkerWorld.configuredSmallCobaltOre;
import static slimeknights.tconstruct.world.TinkerWorld.earthGeode;
import static slimeknights.tconstruct.world.TinkerWorld.enderGeode;
import static slimeknights.tconstruct.world.TinkerWorld.ichorGeode;
import static slimeknights.tconstruct.world.TinkerWorld.placedEarthGeode;
import static slimeknights.tconstruct.world.TinkerWorld.placedEnderGeode;
import static slimeknights.tconstruct.world.TinkerWorld.placedIchorGeode;
import static slimeknights.tconstruct.world.TinkerWorld.placedLargeCobaltOre;
import static slimeknights.tconstruct.world.TinkerWorld.placedSkyGeode;
import static slimeknights.tconstruct.world.TinkerWorld.placedSmallCobaltOre;
import static slimeknights.tconstruct.world.TinkerWorld.skyGeode;
import static slimeknights.tconstruct.world.TinkerWorld.spawnCobaltOre;
import static slimeknights.tconstruct.world.TinkerWorld.spawnEarthGeode;
import static slimeknights.tconstruct.world.TinkerWorld.spawnEndSlime;
import static slimeknights.tconstruct.world.TinkerWorld.spawnEnderGeode;
import static slimeknights.tconstruct.world.TinkerWorld.spawnIchorGeode;
import static slimeknights.tconstruct.world.TinkerWorld.spawnOverworldSlime;
import static slimeknights.tconstruct.world.TinkerWorld.spawnSkyGeode;

/** Provider for all our worldgen datapack registry stuff */
public class WorldgenProvider {
  private WorldgenProvider() {}

  /** Registers this provider with the data generator */
  public static void register(RegistrySetBuilder builder) {
    builder.add(Registries.CONFIGURED_FEATURE, WorldgenProvider::registerConfiguredFeatures);
    builder.add(Registries.PLACED_FEATURE, WorldgenProvider::registerPlacedFeatures);
    builder.add(Registries.STRUCTURE, WorldgenProvider::registerStructures);
    builder.add(Registries.STRUCTURE_SET, WorldgenProvider::registerStructureSets);
    builder.add(ForgeRegistries.Keys.BIOME_MODIFIERS, WorldgenProvider::registerBiomeModifiers);
  }

  /** Registers all configured features */
  @SuppressWarnings("deprecation")
  private static void registerConfiguredFeatures(BootstapContext<ConfiguredFeature<?,?>> context) {
    // sapling trees
    register(context, earthSlimeTree, slimeTree,
             new SlimeTreeConfig.Builder()
               .planted()
               .trunk(TinkerWorld.greenheart.getLog())
               .leaves(TinkerWorld.slimeLeaves.get(FoliageType.EARTH))
               .baseHeight(4).randomHeight(3)
               .build());
    register(context, skySlimeTree, slimeTree,
             new SlimeTreeConfig.Builder()
               .planted().canDoubleHeight()
               .trunk(TinkerWorld.skyroot.getLog())
               .leaves(TinkerWorld.slimeLeaves.get(FoliageType.SKY))
               .build());
    // island trees
    register(context, earthSlimeIslandTree, slimeTree,
             new SlimeTreeConfig.Builder()
               .trunk(TinkerWorld.greenheart.getLog())
               .leaves(TinkerWorld.slimeLeaves.get(FoliageType.EARTH))
               .baseHeight(4).randomHeight(3)
               .build());
    register(context, skySlimeIslandTree, slimeTree,
             new SlimeTreeConfig.Builder()
               .canDoubleHeight()
               .trunk(TinkerWorld.skyroot.getLog())
               .leaves(TinkerWorld.slimeLeaves.get(FoliageType.SKY))
               .vines(TinkerWorld.skySlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
               .build());

    // mangrove style
    register(context, enderSlimeTree, Feature.TREE,
             new TreeConfigurationBuilder(BlockStateProvider.simple(TinkerWorld.enderbark.getLog()),
                                          new UpwardsBranchingTrunkPlacer(2, 1, 4, UniformInt.of(1, 4), 0.5F, UniformInt.of(0, 1), BuiltInRegistries.BLOCK.getOrCreateTag(TinkerTags.Blocks.ENDERBARK_LOGS_CAN_GROW_THROUGH)),
                                          BlockStateProvider.simple(TinkerWorld.slimeLeaves.get(FoliageType.ENDER)),
                                          new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
                                          ExtraRootVariantPlacer.builder()
                                                                .trunkOffset(UniformInt.of(1, 3))
                                                                .rootBlock(TinkerWorld.enderbarkRoots.get())
                                                                .canGrowThroughTag(TinkerTags.Blocks.ENDERBARK_ROOTS_CAN_GROW_THROUGH)
                                                                .slimyRoots(TinkerWorld.slimyEnderbarkRoots)
                                                                .buildOptional(),
                                          new TwoLayersFeatureSize(2, 0, 2))
               .decorators(List.of(new LeaveVineDecorator(TinkerWorld.enderSlimeVine.get(), 0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(BlockStateProvider.simple(TinkerWorld.slimeSapling.get(FoliageType.ENDER).defaultBlockState().setValue(BlockStateProperties.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(Direction.DOWN))))
               .ignoreVines()
               .build());
    register(context, enderSlimeTreeTall, Feature.TREE,
             new TreeConfigurationBuilder(BlockStateProvider.simple(TinkerWorld.enderbark.getLog()),
                                          new UpwardsBranchingTrunkPlacer(4, 1, 9, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1), BuiltInRegistries.BLOCK.getOrCreateTag(TinkerTags.Blocks.ENDERBARK_LOGS_CAN_GROW_THROUGH)),
                                          BlockStateProvider.simple(TinkerWorld.slimeLeaves.get(FoliageType.ENDER)),
                                          new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
                                          ExtraRootVariantPlacer.builder()
                                                                .trunkOffset(UniformInt.of(3, 7))
                                                                .rootBlock(TinkerWorld.enderbarkRoots.get())
                                                                .canGrowThroughTag(TinkerTags.Blocks.ENDERBARK_ROOTS_CAN_GROW_THROUGH)
                                                                .slimyRoots(TinkerWorld.slimyEnderbarkRoots)
                                                                .buildOptional(),
                                          new TwoLayersFeatureSize(3, 0, 2))
               .decorators(List.of(new LeaveVineDecorator(TinkerWorld.enderSlimeVine.get(), 0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(BlockStateProvider.simple(TinkerWorld.slimeSapling.get(FoliageType.ENDER).defaultBlockState().setValue(BlockStateProperties.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(Direction.DOWN))))
               .ignoreVines()
               .build());

    // fungus style
    BlockPredicate slimyFungusGrowThrough = BlockPredicate.matchesTag(TinkerTags.Blocks.SLIMY_FUNGUS_CAN_GROW_THROUGH);
    register(context, bloodSlimeFungus, slimeFungus,
             new SlimeFungusConfig(
               TinkerTags.Blocks.SLIMY_SOIL,
               TinkerWorld.bloodshroom.getLog().defaultBlockState(),
               TinkerWorld.slimeLeaves.get(FoliageType.BLOOD).defaultBlockState(),
               TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
               slimyFungusGrowThrough,
               true));
    register(context, bloodSlimeIslandFungus, slimeFungus,
             new SlimeFungusConfig(
               TinkerTags.Blocks.SLIMY_NYLIUM,
               TinkerWorld.bloodshroom.getLog().defaultBlockState(),
               TinkerWorld.slimeLeaves.get(FoliageType.BLOOD).defaultBlockState(),
               TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
               slimyFungusGrowThrough,
               false));
    register(context, ichorSlimeFungus, slimeFungus,
             new SlimeFungusConfig(
               TinkerTags.Blocks.SLIMY_SOIL,
               TinkerWorld.bloodshroom.getLog().defaultBlockState(),
               TinkerWorld.slimeLeaves.get(FoliageType.ICHOR).defaultBlockState(),
               TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
               slimyFungusGrowThrough,
               false));

    // ores
    RuleTest netherrack = new BlockMatchTest(Blocks.NETHERRACK);
    BlockState cobaltOre = TinkerWorld.cobaltOre.get().defaultBlockState();
    register(context, configuredSmallCobaltOre, Feature.ORE, new OreConfiguration(netherrack, cobaltOre, 4));
    register(context, configuredLargeCobaltOre, Feature.ORE, new OreConfiguration(netherrack, cobaltOre, 6));

    // geodes
    configureGeode(context, configuredEarthGeode, earthGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.CLAY), null,
                   new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 5.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), UniformInt.of(6, 9), UniformInt.of(3, 4), UniformInt.of(1, 2), 16, 1);
    configureGeode(context, configuredSkyGeode, skyGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.MOSSY_COBBLESTONE), TinkerWorld.steelCluster,
                   new GeodeLayerSettings(1.5D, 2.0D, 3.0D, 4.5D), new GeodeCrackSettings(0.55D, 0.5D, 2), UniformInt.of(3, 4), ConstantInt.of(2), ConstantInt.of(1), 8, 3);
    configureGeode(context, configuredIchorGeode, ichorGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.NETHERRACK), null,
                   new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.75D, 2.0D, 2), UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), 24, 20);
    configureGeode(context, configuredEnderGeode, enderGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.END_STONE), TinkerWorld.knightmetalCluster,
                   new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 5.2D), new GeodeCrackSettings(0.45, 1.0D, 2), UniformInt.of(4, 10), UniformInt.of(3, 4), UniformInt.of(1, 2), 16, 10000);
  }

  /** Registers all structures */
  private static void registerPlacedFeatures(BootstapContext<PlacedFeature> context) {
    // ores
    register(context, placedSmallCobaltOre, configuredSmallCobaltOre, CountPlacement.of(5), InSquarePlacement.spread(), PlacementUtils.RANGE_8_8, BiomeFilter.biome());
    register(context, placedLargeCobaltOre, configuredLargeCobaltOre, CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(32)), BiomeFilter.biome());

    // geodes
    placeGeode(context, placedEarthGeode, configuredEarthGeode, RarityFilter.onAverageOnceEvery(128), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6),  VerticalAnchor.aboveBottom(54)));
    placeGeode(context, placedSkyGeode,   configuredSkyGeode,   RarityFilter.onAverageOnceEvery(64),  HeightRangePlacement.uniform(VerticalAnchor.absolute(16),    VerticalAnchor.absolute(54)));
    placeGeode(context, placedIchorGeode, configuredIchorGeode, RarityFilter.onAverageOnceEvery(52),  HeightRangePlacement.uniform(VerticalAnchor.belowTop(48),    VerticalAnchor.belowTop(16)));
    placeGeode(context, placedEnderGeode, configuredEnderGeode, RarityFilter.onAverageOnceEvery(256), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(16), VerticalAnchor.aboveBottom(64)));
  }

  /** Registers all structures */
  private static void registerStructures(BootstapContext<Structure> context) {
    HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
    HolderGetter<ConfiguredFeature<?,?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
    // earthslime island
    context.register(earthSlimeIsland, IslandStructure.seaBuilder()
      .addDefaultTemplates(getResource("islands/earth/"))
      .addTree(configured.getOrThrow(earthSlimeIslandTree), 1)
      .addSlimyGrass(FoliageType.EARTH)
      .build(new StructureSettings(biomes.getOrThrow(TinkerTags.Biomes.EARTHSLIME_ISLANDS), monsterOverride(EntityType.SLIME, 4, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    // skyslime island
    context.register(skySlimeIsland, IslandStructure.skyBuilder()
      .addDefaultTemplates(getResource("islands/sky/"))
      .addTree(configured.getOrThrow(skySlimeIslandTree), 1)
      .addSlimyGrass(FoliageType.SKY)
      .vines(TinkerWorld.skySlimeVine.get())
      .build(new StructureSettings(biomes.getOrThrow(TinkerTags.Biomes.SKYSLIME_ISLANDS), monsterOverride(TinkerWorld.skySlimeEntity.get(), 3, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    // clay island
    context.register(clayIsland, IslandStructure.skyBuilder().addDefaultTemplates(getResource("islands/dirt/"))
      .addTree(configured.getOrThrow(TreeFeatures.OAK), 4)
      .addTree(configured.getOrThrow(TreeFeatures.BIRCH), 3)
      .addTree(configured.getOrThrow(TreeFeatures.SPRUCE), 2)
      .addTree(configured.getOrThrow(TreeFeatures.ACACIA), 1)
      .addTree(configured.getOrThrow(TreeFeatures.JUNGLE_TREE_NO_VINE), 1)
      .addGrass(Blocks.GRASS, 7)
      .addGrass(Blocks.FERN, 1)
      .build(new StructureSettings(biomes.getOrThrow(TinkerTags.Biomes.CLAY_ISLANDS), monsterOverride(TinkerWorld.terracubeEntity.get(), 2, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    // blood island
    context.register(bloodIsland, IslandStructure.seaBuilder().addDefaultTemplates(getResource("islands/blood/"))
      .addTree(configured.getOrThrow(bloodSlimeIslandFungus), 1)
      .addSlimyGrass(FoliageType.BLOOD)
      .build(new StructureSettings(biomes.getOrThrow(TinkerTags.Biomes.BLOOD_ISLANDS), monsterOverride(EntityType.MAGMA_CUBE, 4, 6), Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
    // enderslime
    context.register(endSlimeIsland, IslandStructure.skyBuilder().addDefaultTemplates(getResource("islands/ender/"))
      .addTree(configured.getOrThrow(enderSlimeTree), 3)
      .addTree(configured.getOrThrow(enderSlimeTreeTall), 17)
      .addSlimyGrass(FoliageType.ENDER)
      .vines(TinkerWorld.enderSlimeVine.get())
      .build(new StructureSettings(biomes.getOrThrow(TinkerTags.Biomes.ENDERSLIME_ISLANDS), monsterOverride(TinkerWorld.enderSlimeEntity.get(), 4, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
  }

  /** Registers all structures */
  private static void registerStructureSets(BootstapContext<StructureSet> context) {
    HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
    context.register(overworldOceanIsland, structureSet(30, 9, RandomSpreadType.LINEAR, 25988585,  0.5f, entry(structures, earthSlimeIsland, 1)));
    context.register(overworldSkyIsland,   structureSet(35, 4, RandomSpreadType.LINEAR, 14357800,  0.5f,  entry(structures, skySlimeIsland, 4), entry(structures, clayIsland, 1)));
    context.register(netherOceanIsland,    structureSet(15, 7, RandomSpreadType.LINEAR, 65245622,  0.5f, entry(structures, bloodIsland, 1)));
    context.register(endSkyIsland,         structureSet(25, 6, RandomSpreadType.LINEAR, 368963602, 0.5f, entry(structures, endSlimeIsland, 1)));
  }

  /** Registers all biome modifiers */
  private static void registerBiomeModifiers(BootstapContext<BiomeModifier> context) {
    HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
    HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
    HolderSet<Biome> overworld = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
    HolderSet<Biome> nether = biomes.getOrThrow(BiomeTags.IS_NETHER);
    HolderSet<Biome> end = biomes.getOrThrow(BiomeTags.IS_END);

    context.register(spawnCobaltOre, new AddFeaturesBiomeModifier(nether, direct(placed.getOrThrow(TinkerWorld.placedSmallCobaltOre), placed.getOrThrow(placedLargeCobaltOre)), Decoration.UNDERGROUND_DECORATION));
    // geodes
    context.register(spawnEarthGeode, new AddFeaturesBiomeModifier(overworld, direct(placed.getOrThrow(placedEarthGeode)), Decoration.LOCAL_MODIFICATIONS));
    context.register(spawnSkyGeode,   new AddFeaturesBiomeModifier(and(overworld, not(or(biomes.getOrThrow(BiomeTags.IS_OCEAN), biomes.getOrThrow(BiomeTags.IS_DEEP_OCEAN), biomes.getOrThrow(BiomeTags.IS_BEACH), biomes.getOrThrow(BiomeTags.IS_RIVER)))), direct(placed.getOrThrow(TinkerWorld.placedSkyGeode)), Decoration.LOCAL_MODIFICATIONS));
    context.register(spawnIchorGeode, new AddFeaturesBiomeModifier(nether, direct(placed.getOrThrow(TinkerWorld.placedIchorGeode)), Decoration.LOCAL_MODIFICATIONS));
    context.register(spawnEnderGeode, new AddFeaturesBiomeModifier(and(end, not(direct(biomes.getOrThrow(Biomes.THE_END)))), direct(placed.getOrThrow(TinkerWorld.placedEnderGeode)), Decoration.LOCAL_MODIFICATIONS));
    // spawns
    context.register(spawnOverworldSlime, new AddSpawnsBiomeModifier(overworld, List.of(new SpawnerData(TinkerWorld.skySlimeEntity.get(), 100, 2, 4))));
    context.register(spawnEndSlime,       new AddSpawnsBiomeModifier(end,       List.of(new SpawnerData(TinkerWorld.enderSlimeEntity.get(), 10, 2, 4))));
  }


  /* Helpers */

  /** Ands the holder sets together */
  @SafeVarargs
  private static <T> AndHolderSet<T> and(HolderSet<T>... sets) {
    return new AndHolderSet<>(List.of(sets));
  }

  /** Ors the holder sets together */
  @SafeVarargs
  private static <T> OrHolderSet<T> or(HolderSet<T>... sets) {
    return new OrHolderSet<>(List.of(sets));
  }

  /** Nots the set */
  private static <T> NotHolderSet<T> not(HolderSet<T> set) {
    // passing in null as its impossible to create the object Forge demands of us during datagen, and seems it work without it
    return new SerializableNotHolderSet<>(set);
  }

  private static class SerializableNotHolderSet<T> extends NotHolderSet<T> {
    public SerializableNotHolderSet(HolderSet<T> value) {
      super(null, value);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
      return true;
    }
  }


  /* Configured features */

  /** Registers a configured feature */
  private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?,?>> context, ResourceKey<ConfiguredFeature<?,?>> key, F feature, FC config) {
    context.register(key, new ConfiguredFeature<>(feature, config));
  }

  /** Registers a configured feature */
  private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?,?>> context, ResourceKey<ConfiguredFeature<?,?>> key, Supplier<F> feature, FC config) {
    register(context, key, feature.get(), config);
  }

  /** Configures a geode feature */
  private static void configureGeode(BootstapContext<ConfiguredFeature<?,?>> context, ResourceKey<ConfiguredFeature<?,?>> key, GeodeItemObject geode,
                                     BlockStateProvider middleLayer, BlockStateProvider outerLayer, @Nullable Supplier<? extends Block> extraCluster, GeodeLayerSettings layerSettings, GeodeCrackSettings crackSettings,
                                     IntProvider outerWall, IntProvider distributionPoints, IntProvider pointOffset, int genOffset, int invalidBlocks) {
    // allow adding in an extra cluster type to the geode
    Stream<BlockState> buds = Arrays.stream(BudSize.values()).map(type -> geode.getBud(type).defaultBlockState());
    if (extraCluster != null) {
      buds = Stream.concat(buds, Stream.of(extraCluster.get().defaultBlockState()));
    }
    register(context, key, Feature.GEODE, new GeodeConfiguration(
      new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
                             BlockStateProvider.simple(geode.getBlock()),
                             BlockStateProvider.simple(geode.getBudding()),
                             middleLayer, outerLayer,
                             buds.toList(),
                             BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GEODE_INVALID_BLOCKS),
      layerSettings, crackSettings, 0.335, 0.083, true, outerWall, distributionPoints, pointOffset, -genOffset, genOffset, 0.05D, invalidBlocks)
    );
  }


  /* Placed features */

  /** Registers a placed feature */
  private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?,?>> configured, PlacementModifier... placement) {
    context.register(key, new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configured), List.of(placement)));
  }

  /** Registers a placed feature */
  private static void placeGeode(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?,?>> configured, RarityFilter rarity, HeightRangePlacement height) {
    register(context, key, configured, rarity, InSquarePlacement.spread(), height, BiomeFilter.biome());
  }

  /* Structure sets */

  /** Saves a structure set */
  private static StructureSet structureSet(int spacing, int separation, RandomSpreadType spreadType, int salt, float frequency, StructureSelectionEntry... structures) {
    return new StructureSet(List.of(structures), new RandomSpreadStructurePlacement(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, frequency, salt, Optional.empty(), spacing, separation, spreadType));
  }

  /** Creates an entry for a registry object */
  private static StructureSelectionEntry entry(HolderGetter<Structure> structures, ResourceKey<Structure> structure, int weight) {
    return new StructureSelectionEntry(structures.getOrThrow(structure), weight);
  }


  /* Biome modifiers */

  /** Creates a spawn override for a single mob */
  private static Map<MobCategory,StructureSpawnOverride> monsterOverride(EntityType<?> entity, int min, int max) {
    return Map.of(MobCategory.MONSTER, new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(entity, 1, min, max))));
  }
}
