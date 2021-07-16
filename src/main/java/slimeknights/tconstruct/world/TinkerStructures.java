package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeFungusConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock.VineStage;
import slimeknights.tconstruct.world.worldgen.islands.BloodSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.ClayIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EarthSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EnderSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SkySlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {
  private static boolean structureSettingsReady = false;

  static final Logger log = Util.getLogger("tinker_structures");

  /*
   * Misc
   */
  public static final RegistryObject<BlockStateProviderType<SupplierBlockStateProvider>> supplierBlockstateProvider = BLOCK_STATE_PROVIDER_TYPES.register("supplier_state_provider", () -> new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC));

  /*
   * Features
   */
  /** Overworld variant of slimy trees */
  public static final RegistryObject<SlimeTreeFeature> SLIME_TREE = FEATURES.register("slime_tree", () -> new SlimeTreeFeature(SlimeTreeConfig.CODEC));
  /** Nether variant of slimy trees */
  public static final RegistryObject<SlimeFungusFeature> SLIME_FUNGUS = FEATURES.register("slime_fungus", () -> new SlimeFungusFeature(SlimeFungusConfig.CODEC));

  /** Greenheart tree variant */
  public static ConfiguredFeature<SlimeTreeConfig, ?> EARTH_SLIME_TREE;
  /** Greenheart tree variant on islands */
  public static ConfiguredFeature<SlimeTreeConfig, ?> EARTH_SLIME_ISLAND_TREE;

  /** Skyroot tree variant */
  public static ConfiguredFeature<SlimeTreeConfig, ?> SKY_SLIME_TREE;
  /** Skyroot tree variant on islands */
  public static ConfiguredFeature<SlimeTreeConfig, ?> SKY_SLIME_ISLAND_TREE;

  /** Enderslime island tree variant */
  public static ConfiguredFeature<SlimeTreeConfig, ?> ENDER_SLIME_TREE;
  /** Enderslime island tree variant on islands */
  public static ConfiguredFeature<SlimeTreeConfig, ?> ENDER_SLIME_ISLAND_TREE;

  /** Bloodshroom tree variant */
  public static ConfiguredFeature<HugeFungusConfig, ?> BLOOD_SLIME_FUNGUS;
  /** Bloodshroom island tree variant */
  public static ConfiguredFeature<HugeFungusConfig, ?> BLOOD_SLIME_ISLAND_FUNGUS;

  /* Deprecated ichor tree */
  public static ConfiguredFeature<HugeFungusConfig, ?> ICHOR_SLIME_FUNGUS;

  /*
   * Structures
   */
  public static IStructurePieceType slimeIslandPiece;
  public static final RegistryObject<Structure<NoFeatureConfig>> earthSlimeIsland = STRUCTURE_FEATURES.register("earth_slime_island", EarthSlimeIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> EARTH_SLIME_ISLAND;

  public static final RegistryObject<Structure<NoFeatureConfig>> skySlimeIsland = STRUCTURE_FEATURES.register("overworld_slime_island", SkySlimeIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> SKY_SLIME_ISLAND;
  public static final RegistryObject<Structure<NoFeatureConfig>> clayIsland = STRUCTURE_FEATURES.register("clay_island", ClayIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> CLAY_ISLAND;

  public static final RegistryObject<Structure<NoFeatureConfig>> bloodSlimeIsland = STRUCTURE_FEATURES.register("nether_slime_island", BloodSlimeIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> BLOOD_SLIME_ISLAND;

  public static final RegistryObject<Structure<NoFeatureConfig>> endSlimeIsland = STRUCTURE_FEATURES.register("end_slime_island", EnderSlimeIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> END_SLIME_ISLAND;

  @SubscribeEvent
  void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, resource("slime_island_piece"), SlimeIslandPiece::new);
  }

  /** Adds the settings to the given dimension */
  private static void addStructureSettings(RegistryKey<DimensionSettings> key, Structure<?> structure, StructureSeparationSettings settings) {
    DimensionSettings dimensionSettings = WorldGenRegistries.NOISE_SETTINGS.getValueForKey(key);
    if (dimensionSettings != null) {
      dimensionSettings.getStructures().func_236195_a_().put(structure, settings);
    }
  }

  /** Adds the structure to the structure map */
  private static void addStructureToMap(Structure<?> structure) {
    Structure.NAME_STRUCTURE_BIMAP.put(Objects.requireNonNull(structure.getRegistryName()).toString(), structure);
  }

  /** Adds all structure separation settings to the relevant maps */
  public static void addStructureSeparation() {
    if (!structureSettingsReady) {
      return;
    }
    // add each structure to all relevant dimensions, note this may not allow changing after first world load as everything is serialized
    StructureSeparationSettings earthSettings = new StructureSeparationSettings(Config.COMMON.earthSlimeIslandSeparation.get(), 5, 25988585);
    Map<Structure<?>, StructureSeparationSettings> defaultStructures = DimensionSettings.getDefaultDimensionSettings().getStructures().func_236195_a_();
    defaultStructures.put(earthSlimeIsland.get(), earthSettings);
    addStructureSettings(DimensionSettings.AMPLIFIED, earthSlimeIsland.get(), earthSettings);
    addStructureSettings(DimensionSettings.FLOATING_ISLANDS, earthSlimeIsland.get(), earthSettings);

    StructureSeparationSettings skySettings = new StructureSeparationSettings(Config.COMMON.skySlimeIslandSeparation.get(), 5, 14357800);
    defaultStructures.put(skySlimeIsland.get(), skySettings);
    addStructureSettings(DimensionSettings.AMPLIFIED, skySlimeIsland.get(), skySettings);
    addStructureSettings(DimensionSettings.FLOATING_ISLANDS, skySlimeIsland.get(), skySettings);

    StructureSeparationSettings claySettings = new StructureSeparationSettings(Config.COMMON.clayIslandSeparation.get(), 5, 162976988);
    defaultStructures.put(clayIsland.get(), claySettings);
    addStructureSettings(DimensionSettings.AMPLIFIED, clayIsland.get(), claySettings);
    addStructureSettings(DimensionSettings.FLOATING_ISLANDS, clayIsland.get(), claySettings);

    StructureSeparationSettings netherSettings = new StructureSeparationSettings(Config.COMMON.bloodIslandSeparation.get(), 5, 65245622);
    addStructureSettings(DimensionSettings.NETHER, bloodSlimeIsland.get(), netherSettings);

    StructureSeparationSettings endSettings = new StructureSeparationSettings(Config.COMMON.endSlimeIslandSeparation.get(), 5, 368963602);
    addStructureSettings(DimensionSettings.END, endSlimeIsland.get(), endSettings);

    // add to the default for anyone creating dimension settings later, hopefully its soon enough
    ImmutableMap.Builder<Structure<?>, StructureSeparationSettings> builder = ImmutableMap.builder();
    // skip old values that match one of the islands, we are replacing those
    Set<Structure<?>> ignore = Sets.newHashSet(earthSlimeIsland.get(), skySlimeIsland.get(), clayIsland.get(), bloodSlimeIsland.get(), endSlimeIsland.get());
    builder.putAll(DimensionStructuresSettings.field_236191_b_.entrySet().stream()
                                                              .filter(entry -> !ignore.contains(entry.getKey())).collect(Collectors.toList()));
    // add new islands
    builder.put(earthSlimeIsland.get(), earthSettings);
    builder.put(skySlimeIsland.get(), skySettings);
    builder.put(clayIsland.get(), claySettings);
    builder.put(bloodSlimeIsland.get(), netherSettings);
    builder.put(endSlimeIsland.get(), endSettings);
    DimensionStructuresSettings.field_236191_b_ = builder.build();
  }

  /**
   * Feature configuration
   *
   * PLACEMENT MOVED TO WorldEvents#onBiomeLoad
   */
  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      addStructureToMap(earthSlimeIsland.get());
      addStructureToMap(skySlimeIsland.get());
      addStructureToMap(clayIsland.get());
      addStructureToMap(bloodSlimeIsland.get());
      addStructureToMap(endSlimeIsland.get());
    });

    // mark ready, so the config can also call that method
    structureSettingsReady = true;
    event.enqueueWork(TinkerStructures::addStructureSeparation);

    event.enqueueWork(() -> {
      EARTH_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("earth_slime_island"), earthSlimeIsland.get().withConfiguration(NoFeatureConfig.INSTANCE));
      SKY_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("sky_slime_island"), skySlimeIsland.get().withConfiguration(NoFeatureConfig.INSTANCE));
      CLAY_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("clay_island"), clayIsland.get().withConfiguration(NoFeatureConfig.INSTANCE));
      BLOOD_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("blood_slime_island"), bloodSlimeIsland.get().withConfiguration(NoFeatureConfig.INSTANCE));
      END_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("end_slime_island"), endSlimeIsland.get().withConfiguration(NoFeatureConfig.INSTANCE));

      // trees
      EARTH_SLIME_TREE = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("earth_slime_tree"),
        SLIME_TREE.get().withConfiguration((
          new SlimeTreeConfig.Builder()
            .planted()
            .trunk(() -> TinkerWorld.greenheart.getLog().getDefaultState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).getDefaultState())
            .baseHeight(4).randomHeight(3)
            .build())));
      EARTH_SLIME_ISLAND_TREE = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("earth_slime_tree"),
        SLIME_TREE.get().withConfiguration((
          new SlimeTreeConfig.Builder()
            .trunk(() -> TinkerWorld.greenheart.getLog().getDefaultState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).getDefaultState())
            .baseHeight(4).randomHeight(3)
            .build())));

      SKY_SLIME_TREE = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("sky_slime_tree"),
        SLIME_TREE.get().withConfiguration((
          new SlimeTreeConfig.Builder()
            .planted().canDoubleHeight()
            .trunk(() -> TinkerWorld.skyroot.getLog().getDefaultState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).getDefaultState())
            .build())));
      SKY_SLIME_ISLAND_TREE = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("sky_slime_island_tree"),
        SLIME_TREE.get().withConfiguration((
          new SlimeTreeConfig.Builder()
            .canDoubleHeight()
            .trunk(() -> TinkerWorld.skyroot.getLog().getDefaultState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).getDefaultState())
            .vines(() -> TinkerWorld.skySlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE))
            .build())));

      ENDER_SLIME_TREE = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("ender_slime_tree"),
        SLIME_TREE.get().withConfiguration((
          new SlimeTreeConfig.Builder()
            .planted()
            .trunk(() -> TinkerWorld.greenheart.getLog().getDefaultState()) // TODO: temporary until we have proper green trees and ender shrooms
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).getDefaultState())
            .build())));
      ENDER_SLIME_ISLAND_TREE = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("ender_slime_island_tree"),
        SLIME_TREE.get().withConfiguration((
          new SlimeTreeConfig.Builder()
            .trunk(() -> TinkerWorld.greenheart.getLog().getDefaultState()) // TODO: temporary until we have proper green trees and ender shrooms
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).getDefaultState())
            .vines(() -> TinkerWorld.enderSlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE))
            .build())));

      BLOOD_SLIME_FUNGUS = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("blood_slime_fungus"),
        SLIME_FUNGUS.get().withConfiguration(new SlimeFungusConfig(
          TinkerTags.Blocks.SLIMY_SOIL,
          TinkerWorld.bloodshroom.getLog().getDefaultState(),
          TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).getDefaultState(),
          TinkerWorld.congealedSlime.get(SlimeType.ICHOR).getDefaultState(),
          true)));
      BLOOD_SLIME_ISLAND_FUNGUS = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("blood_slime_island_fungus"),
        SLIME_FUNGUS.get().withConfiguration(new SlimeFungusConfig(
          TinkerTags.Blocks.SLIMY_NYLIUM,
          TinkerWorld.bloodshroom.getLog().getDefaultState(),
          TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).getDefaultState(),
          TinkerWorld.congealedSlime.get(SlimeType.ICHOR).getDefaultState(),
          false)));
      ICHOR_SLIME_FUNGUS = Registry.register(
        WorldGenRegistries.CONFIGURED_FEATURE, resource("ichor_slime_fungus"),
        SLIME_FUNGUS.get().withConfiguration(
          new SlimeFungusConfig(
            TinkerTags.Blocks.SLIMY_SOIL,
            TinkerWorld.bloodshroom.getLog().getDefaultState(),
            TinkerWorld.slimeLeaves.get(SlimeType.ICHOR).getDefaultState(),
            TinkerWorld.congealedSlime.get(SlimeType.ICHOR).getDefaultState(),
            false)));
    });
  }
}
