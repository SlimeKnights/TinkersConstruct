package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.StructurePieceType.StructureTemplateType;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

import java.util.Collection;
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
  public static final RegistryObject<SlimeTreeFeature> slimeTree = FEATURES.register("slime_tree", () -> new SlimeTreeFeature(SlimeTreeConfig.CODEC));
  /** Nether variant of slimy trees */
  public static final RegistryObject<SlimeFungusFeature> slimeFungus = FEATURES.register("slime_fungus", () -> new SlimeFungusFeature(SlimeFungusConfig.CODEC));

  /** Greenheart tree variant */
  public static ConfiguredFeature<SlimeTreeConfig, ?> earthSlimeTree;
  /** Greenheart tree variant on islands */
  public static ConfiguredFeature<SlimeTreeConfig, ?> earthSlimeIslandTree;

  /** Skyroot tree variant */
  public static ConfiguredFeature<SlimeTreeConfig, ?> skySlimeTree;
  /** Skyroot tree variant on islands */
  public static ConfiguredFeature<SlimeTreeConfig, ?> skySlimeIslandTree;

  /** Enderslime island tree variant */
  public static ConfiguredFeature<SlimeTreeConfig, ?> enderSlimeTree;
  /** Enderslime island tree variant on islands */
  public static ConfiguredFeature<SlimeTreeConfig, ?> enderSlimeIslandTree;

  /** Bloodshroom tree variant */
  public static ConfiguredFeature<HugeFungusConfiguration, ?> bloodSlimeFungus;
  /** Bloodshroom island tree variant */
  public static ConfiguredFeature<HugeFungusConfiguration, ?> bloodSlimeIslandFungus;

  /* Deprecated ichor tree */
  public static ConfiguredFeature<HugeFungusConfiguration, ?> ichorSlimeFungus;

  /*
   * Structures
   */
  // overworld
  public static StructurePieceType slimeIslandPiece;
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> earthSlimeIsland = STRUCTURE_FEATURES.register("earth_slime_island", EarthSlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> configuredEarthSlimeIsland;
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> skySlimeIsland = STRUCTURE_FEATURES.register("sky_slime_island", SkySlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> configuredSkySlimeIsland;
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> clayIsland = STRUCTURE_FEATURES.register("clay_island", ClayIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> configuredClayIsland;
  // nether
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> bloodIsland = STRUCTURE_FEATURES.register("blood_island", BloodSlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> configuredBloodIsland;
  // end
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> endSlimeIsland = STRUCTURE_FEATURES.register("end_slime_island", EnderSlimeIslandStructure::new);
  public static ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> configuredEndSlimeIsland;

  @SubscribeEvent
  void onFeaturesRegistry(RegistryEvent.Register<StructureFeature<?>> event) {
    slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, resource("slime_island_piece"), (StructureTemplateType)SlimeIslandPiece::new);
  }

  /** Adds the settings to the given dimension */
  private static void addStructureSettings(ImmutableMap.Builder<StructureFeature<?>, StructureFeatureConfiguration> defaultSettings, StructureFeature<?> structure, StructureFeatureConfiguration settings) {
    defaultSettings.put(structure, settings);
    for (NoiseGeneratorSettings dimensionSettings : BuiltinRegistries.NOISE_GENERATOR_SETTINGS) {
      dimensionSettings.structureSettings().structureConfig().put(structure, settings);
    }
  }

  /** Adds the settings to the given dimension */
  private static void removeStructureSettings(StructureFeature<?> structure) {
    for (NoiseGeneratorSettings dimensionSettings : BuiltinRegistries.NOISE_GENERATOR_SETTINGS) {
      dimensionSettings.structureSettings().structureConfig().remove(structure);
    }
  }

  /** Adds the settings to the given dimension */
  private static void addStructures() {
    // floating islands skips earth
    ImmutableMultimap<ConfiguredStructureFeature<?,?>,ResourceKey<Biome>> clayIslandMap = multimapOf(configuredClayIsland, BiomeDictionary.getBiomes(Type.OVERWORLD).stream().filter(biome -> BiomeDictionary.hasType(biome, Type.FOREST)).toList());
    ImmutableMultimap<ConfiguredStructureFeature<?,?>,ResourceKey<Biome>> skyIslandMap = multimapOf(configuredSkySlimeIsland, BiomeDictionary.getBiomes(Type.OVERWORLD));
    ImmutableMultimap<ConfiguredStructureFeature<?,?>,ResourceKey<Biome>> earthIslandMap = multimapOf(configuredEarthSlimeIsland, BiomeDictionary.getBiomes(Type.OVERWORLD).stream().filter(biome -> BiomeDictionary.hasType(biome, Type.OCEAN)).toList());
    ImmutableMultimap<ConfiguredStructureFeature<?,?>,ResourceKey<Biome>> bloodIslandMap = multimapOf(configuredBloodIsland, BiomeDictionary.getBiomes(Type.NETHER));
    ImmutableMultimap<ConfiguredStructureFeature<?,?>,ResourceKey<Biome>> endIslandMap = multimapOf(configuredEndSlimeIsland, BiomeDictionary.getBiomes(Type.END).stream().filter(key -> key != Biomes.THE_END).toList());

    // simply add to all dimensions, if the island does not belong it won't add the biome
    for (NoiseGeneratorSettings dimensionSettings : BuiltinRegistries.NOISE_GENERATOR_SETTINGS) {
      StructureSettings settings = dimensionSettings.structureSettings();
      ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> builder = ImmutableMap.builder();
      builder.putAll(settings.configuredStructures);
      builder.put(clayIsland.get(), clayIslandMap);
      builder.put(skySlimeIsland.get(), skyIslandMap);
      builder.put(earthSlimeIsland.get(), earthIslandMap);
      builder.put(bloodIsland.get(), bloodIslandMap);
      builder.put(endSlimeIsland.get(), endIslandMap);
      settings.configuredStructures = builder.build();
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
    // need to rebuild default, start by copying default values
    ImmutableMap.Builder<StructureFeature<?>, StructureFeatureConfiguration> defaultSettings = ImmutableMap.builder();
    // skip old values that match one of the islands, we are replacing those
    Set<StructureFeature<?>> ignore = Sets.newHashSet(earthSlimeIsland.get(), skySlimeIsland.get(), clayIsland.get(), bloodIsland.get(), endSlimeIsland.get());
    defaultSettings.putAll(StructureSettings.DEFAULTS.entrySet().stream().filter(entry -> !ignore.contains(entry.getKey())).collect(Collectors.toList()));

    // overworld //
    // earth
    if (Config.COMMON.earthslimeIslands.doesGenerate()) {
      addStructureSettings(defaultSettings, earthSlimeIsland.get(), Config.COMMON.earthslimeIslands.makeConfiguration());
    } else {
      removeStructureSettings(earthSlimeIsland.get());
    }
    // sky
    if (Config.COMMON.skyslimeIslands.doesGenerate()) {
      addStructureSettings(defaultSettings, skySlimeIsland.get(), Config.COMMON.skyslimeIslands.makeConfiguration());
    } else {
      removeStructureSettings(skySlimeIsland.get());
    }
    // clay
    if (Config.COMMON.clayIslands.doesGenerate()) {
      addStructureSettings(defaultSettings, clayIsland.get(), Config.COMMON.clayIslands.makeConfiguration());
    } else {
      removeStructureSettings(clayIsland.get());
    }

    // nether //
    if (Config.COMMON.bloodIslands.doesGenerate()) {
      addStructureSettings(defaultSettings, bloodIsland.get(), Config.COMMON.bloodIslands.makeConfiguration());
    } else {
      removeStructureSettings(bloodIsland.get());
    }

    // end //
    if (Config.COMMON.endslimeIslands.doesGenerate()) {
      addStructureSettings(defaultSettings, endSlimeIsland.get(), Config.COMMON.endslimeIslands.makeConfiguration());
    } else {
      removeStructureSettings(endSlimeIsland.get());
    }

    // build default settings
    StructureSettings.DEFAULTS = defaultSettings.build();
  }

  /** Creates an immutable multimap with one key and a collection of values */
  private static <K,V> ImmutableMultimap<K,V> multimapOf(K key, Collection<V> values) {
    ImmutableMultimap.Builder<K,V> builder = ImmutableMultimap.builder();
    builder.putAll(key, values);
    return builder.build();
  }

  /**
   * Feature configuration
   */
  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      addStructureToMap(earthSlimeIsland.get());
      addStructureToMap(skySlimeIsland.get());
      addStructureToMap(clayIsland.get());
      addStructureToMap(bloodIsland.get());
      addStructureToMap(endSlimeIsland.get());
    });

    // mark ready, so the config can also call that method
    structureSettingsReady = true;
    event.enqueueWork(TinkerStructures::addStructureSeparation);

    event.enqueueWork(() -> {
      configuredEarthSlimeIsland = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("earth_slime_island"), earthSlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      configuredSkySlimeIsland = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("sky_slime_island"), skySlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      configuredClayIsland = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("clay_island"), clayIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      configuredBloodIsland = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("blood_island"), bloodIsland.get().configured(NoneFeatureConfiguration.INSTANCE));
      configuredEndSlimeIsland = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resource("end_slime_island"), endSlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE));

      // trees
      earthSlimeTree = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("earth_slime_tree"),
        slimeTree.get().configured((
          new SlimeTreeConfig.Builder()
            .planted()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
            .baseHeight(4).randomHeight(3)
            .build())));
      earthSlimeIslandTree = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("earth_slime_island_tree"),
        slimeTree.get().configured((
          new SlimeTreeConfig.Builder()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
            .baseHeight(4).randomHeight(3)
            .build())));

      skySlimeTree = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("sky_slime_tree"),
        slimeTree.get().configured((
          new SlimeTreeConfig.Builder()
            .planted().canDoubleHeight()
            .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
            .build())));
      skySlimeIslandTree = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("sky_slime_island_tree"),
        slimeTree.get().configured((
          new SlimeTreeConfig.Builder()
            .canDoubleHeight()
            .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
            .vines(() -> TinkerWorld.skySlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
            .build())));

      enderSlimeTree = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("ender_slime_tree"),
        slimeTree.get().configured((
          new SlimeTreeConfig.Builder()
            .planted()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
            .build())));
      enderSlimeIslandTree = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("ender_slime_island_tree"),
        slimeTree.get().configured((
          new SlimeTreeConfig.Builder()
            .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
            .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
            .vines(() -> TinkerWorld.enderSlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
            .build())));

      bloodSlimeFungus = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("blood_slime_fungus"),
        slimeFungus.get().configured(new SlimeFungusConfig(
          TinkerTags.Blocks.SLIMY_SOIL,
          TinkerWorld.bloodshroom.getLog().defaultBlockState(),
          TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
          TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
          true)));
      bloodSlimeIslandFungus = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("blood_slime_island_fungus"),
        slimeFungus.get().configured(new SlimeFungusConfig(
          TinkerTags.Blocks.SLIMY_NYLIUM,
          TinkerWorld.bloodshroom.getLog().defaultBlockState(),
          TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
          TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
          false)));
      ichorSlimeFungus = Registry.register(
        BuiltinRegistries.CONFIGURED_FEATURE, resource("ichor_slime_fungus"),
        slimeFungus.get().configured(
          new SlimeFungusConfig(
            TinkerTags.Blocks.SLIMY_SOIL,
            TinkerWorld.bloodshroom.getLog().defaultBlockState(),
            TinkerWorld.slimeLeaves.get(SlimeType.ICHOR).defaultBlockState(),
            TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
            false)));
    });
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  void loadComplete(FMLLoadCompleteEvent event) {
    event.enqueueWork(TinkerStructures::addStructures);
  }
}
