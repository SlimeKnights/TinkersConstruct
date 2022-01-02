package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
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
  public static ConfiguredFeature<HugeFungusConfiguration, ?> BLOOD_SLIME_FUNGUS;
  /** Bloodshroom island tree variant */
  public static ConfiguredFeature<HugeFungusConfiguration, ?> BLOOD_SLIME_ISLAND_FUNGUS;

  /* Deprecated ichor tree */
  public static ConfiguredFeature<HugeFungusConfiguration, ?> ICHOR_SLIME_FUNGUS;

  /*
   * Structures
   */
  public static StructurePieceType slimeIslandPiece;
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> earthSlimeIsland = STRUCTURE_FEATURES.register("earth_slime_island", EarthSlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> EARTH_SLIME_ISLAND;

  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> skySlimeIsland = STRUCTURE_FEATURES.register("overworld_slime_island", SkySlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> SKY_SLIME_ISLAND;
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> clayIsland = STRUCTURE_FEATURES.register("clay_island", ClayIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> CLAY_ISLAND;

  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> bloodSlimeIsland = STRUCTURE_FEATURES.register("nether_slime_island", BloodSlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> BLOOD_SLIME_ISLAND;

  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> endSlimeIsland = STRUCTURE_FEATURES.register("end_slime_island", EnderSlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> END_SLIME_ISLAND;

  @SubscribeEvent
  void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    //slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, resource("slime_island_piece"), SlimeIslandPiece::new);
  }

  /** Adds the settings to the given dimension */
  private static void addStructureSettings(ResourceKey<NoiseGeneratorSettings> key, StructureFeature<?> structure, StructureFeatureConfiguration settings) {
    NoiseGeneratorSettings dimensionSettings = BuiltinRegistries.NOISE_GENERATOR_SETTINGS.get(key);
    if (dimensionSettings != null) {
      dimensionSettings.structureSettings().structureConfig().put(structure, settings);
    }
  }

  /** Adds the structure to the structure map */
  private static void addStructureToMap(StructureFeature<?> structure) {
    StructureFeature.STRUCTURES_REGISTRY.put(Objects.requireNonNull(structure.getRegistryName()).toString(), structure);
  }

  /** Adds all structure separation settings to the relevant maps */
  public static void addStructureSeparation() {
    if (!structureSettingsReady) {
      return;
    }
    // add each structure to all relevant dimensions, note this may not allow changing after first world load as everything is serialized
    StructureFeatureConfiguration earthSettings = new StructureFeatureConfiguration(Config.COMMON.earthSlimeIslandSeparation.get(), 5, 25988585);
    Map<StructureFeature<?>, StructureFeatureConfiguration> defaultStructures = NoiseGeneratorSettings.bootstrap().structureSettings().structureConfig();
    defaultStructures.put(earthSlimeIsland.get(), earthSettings);
    addStructureSettings(NoiseGeneratorSettings.AMPLIFIED, earthSlimeIsland.get(), earthSettings);
    addStructureSettings(NoiseGeneratorSettings.FLOATING_ISLANDS, earthSlimeIsland.get(), earthSettings);

    StructureFeatureConfiguration skySettings = new StructureFeatureConfiguration(Config.COMMON.skySlimeIslandSeparation.get(), 5, 14357800);
    defaultStructures.put(skySlimeIsland.get(), skySettings);
    addStructureSettings(NoiseGeneratorSettings.AMPLIFIED, skySlimeIsland.get(), skySettings);
    addStructureSettings(NoiseGeneratorSettings.FLOATING_ISLANDS, skySlimeIsland.get(), skySettings);

    StructureFeatureConfiguration claySettings = new StructureFeatureConfiguration(Config.COMMON.clayIslandSeparation.get(), 5, 162976988);
    defaultStructures.put(clayIsland.get(), claySettings);
    addStructureSettings(NoiseGeneratorSettings.AMPLIFIED, clayIsland.get(), claySettings);
    addStructureSettings(NoiseGeneratorSettings.FLOATING_ISLANDS, clayIsland.get(), claySettings);

    StructureFeatureConfiguration netherSettings = new StructureFeatureConfiguration(Config.COMMON.bloodIslandSeparation.get(), 5, 65245622);
    addStructureSettings(NoiseGeneratorSettings.NETHER, bloodSlimeIsland.get(), netherSettings);

    StructureFeatureConfiguration endSettings = new StructureFeatureConfiguration(Config.COMMON.endSlimeIslandSeparation.get(), 5, 368963602);
    addStructureSettings(NoiseGeneratorSettings.END, endSlimeIsland.get(), endSettings);

    // add to the default for anyone creating dimension settings later, hopefully its soon enough
    ImmutableMap.Builder<StructureFeature<?>, StructureFeatureConfiguration> builder = ImmutableMap.builder();
    // skip old values that match one of the islands, we are replacing those
    Set<StructureFeature<?>> ignore = Sets.newHashSet(earthSlimeIsland.get(), skySlimeIsland.get(), clayIsland.get(), bloodSlimeIsland.get(), endSlimeIsland.get());
    builder.putAll(StructureSettings.DEFAULTS.entrySet().stream()
                                             .filter(entry -> !ignore.contains(entry.getKey())).collect(Collectors.toList()));
    // add new islands
    builder.put(earthSlimeIsland.get(), earthSettings);
    builder.put(skySlimeIsland.get(), skySettings);
    builder.put(clayIsland.get(), claySettings);
    builder.put(bloodSlimeIsland.get(), netherSettings);
    builder.put(endSlimeIsland.get(), endSettings);
    StructureSettings.DEFAULTS = builder.build();
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
      EARTH_SLIME_ISLAND = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("earth_slime_island"), earthSlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      SKY_SLIME_ISLAND = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("sky_slime_island"), skySlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      CLAY_ISLAND = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("clay_island"), clayIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      BLOOD_SLIME_ISLAND = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("blood_slime_island"), bloodSlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      END_SLIME_ISLAND = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("end_slime_island"), endSlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE));

      // trees
      EARTH_SLIME_TREE = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("earth_slime_tree"),
        SLIME_TREE.get().configured((
          new SlimeTreeConfig.Builder()
            .planted()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
            .baseHeight(4).randomHeight(3)
            .build())));
      EARTH_SLIME_ISLAND_TREE = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("earth_slime_island_tree"),
        SLIME_TREE.get().configured((
          new SlimeTreeConfig.Builder()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
            .baseHeight(4).randomHeight(3)
            .build())));

      SKY_SLIME_TREE = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("sky_slime_tree"),
        SLIME_TREE.get().configured((
          new SlimeTreeConfig.Builder()
            .planted().canDoubleHeight()
            .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
            .build())));
      SKY_SLIME_ISLAND_TREE = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("sky_slime_island_tree"),
        SLIME_TREE.get().configured((
          new SlimeTreeConfig.Builder()
            .canDoubleHeight()
            .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
            .vines(() -> TinkerWorld.skySlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
            .build())));

      ENDER_SLIME_TREE = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("ender_slime_tree"),
        SLIME_TREE.get().configured((
          new SlimeTreeConfig.Builder()
            .planted()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
            .build())));
      ENDER_SLIME_ISLAND_TREE = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("ender_slime_island_tree"),
        SLIME_TREE.get().configured((
          new SlimeTreeConfig.Builder()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
            .vines(() -> TinkerWorld.enderSlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
            .build())));

      BLOOD_SLIME_FUNGUS = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("blood_slime_fungus"),
        SLIME_FUNGUS.get().configured(new SlimeFungusConfig(
          TinkerTags.Blocks.SLIMY_SOIL,
          TinkerWorld.bloodshroom.getLog().defaultBlockState(),
          TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
          TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
          true)));
      BLOOD_SLIME_ISLAND_FUNGUS = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("blood_slime_island_fungus"),
        SLIME_FUNGUS.get().configured(new SlimeFungusConfig(
          TinkerTags.Blocks.SLIMY_NYLIUM,
          TinkerWorld.bloodshroom.getLog().defaultBlockState(),
          TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
          TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
          false)));
      ICHOR_SLIME_FUNGUS = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("ichor_slime_fungus"),
        SLIME_FUNGUS.get().configured(
          new SlimeFungusConfig(
            TinkerTags.Blocks.SLIMY_SOIL,
            TinkerWorld.bloodshroom.getLog().defaultBlockState(),
            TinkerWorld.slimeLeaves.get(SlimeType.ICHOR).defaultBlockState(),
            TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
            false)));
    });
  }
}
