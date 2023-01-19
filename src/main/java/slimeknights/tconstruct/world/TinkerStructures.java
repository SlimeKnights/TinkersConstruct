package slimeknights.tconstruct.world;

import net.minecraft.core.Registry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType.StructureTemplateType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
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

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_structures");
  private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TConstruct.MOD_ID);
  private static final DeferredRegister<StructureFeature<?>> STRUCTURE_FEATURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, TConstruct.MOD_ID);
  private static final DeferredRegister<ConfiguredStructureFeature<?,?>> CONFIGURED_STRUCTURE_FEATURES = DeferredRegister.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, TConstruct.MOD_ID);
  private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, TConstruct.MOD_ID);
  private static final DeferredRegister<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, TConstruct.MOD_ID);

  public TinkerStructures() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    FEATURES.register(bus);
    STRUCTURE_FEATURES.register(bus);
    STRUCTURE_PIECE.register(bus);
    CONFIGURED_STRUCTURE_FEATURES.register(bus);
    BLOCK_STATE_PROVIDER_TYPES.register(bus);
  }


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
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> earthSlimeTree = CONFIGURED_FEATURES.registerStatic(
    "earth_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());
  /** Greenheart tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> earthSlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "earth_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());

  /** Skyroot tree variant */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeTree = CONFIGURED_FEATURES.registerStatic(
    "sky_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted().canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
      .build());
  /** Skyroot tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "sky_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
      .vines(() -> TinkerWorld.skySlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
      .build());

  /** Enderslime island tree variant */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> enderSlimeTree = CONFIGURED_FEATURES.registerStatic(
    "ender_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
      .build());
  /** Enderslime island tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> enderSlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "ender_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
      .vines(() -> TinkerWorld.enderSlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
      .build());

  /** Bloodshroom tree variant */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> bloodSlimeFungus = CONFIGURED_FEATURES.registerSupplier(
    "blood_slime_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      true));
  /** Bloodshroom island tree variant */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> bloodSlimeIslandFungus = CONFIGURED_FEATURES.registerSupplier(
    "blood_slime_island_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_NYLIUM,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));
  /* Deprecated ichor tree */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> ichorSlimeFungus = CONFIGURED_FEATURES.registerSupplier(
    "ichor_slime_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.ICHOR).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));

  /*
   * Structures
   */
  public static final RegistryObject<StructurePieceType> slimeIslandPiece = STRUCTURE_PIECE.register("slime_island_piece", () -> (StructureTemplateType)SlimeIslandPiece::new);
  // earthslime)
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> earthSlimeIsland = STRUCTURE_FEATURES.register("earth_slime_island", EarthSlimeIslandStructure::new);
  public static final RegistryObject<ConfiguredStructureFeature<?,?>> configuredEarthSlimeIsland = CONFIGURED_STRUCTURE_FEATURES.register("earth_slime_island", () -> earthSlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE, TinkerTags.Biomes.EARTHSLIME_ISLANDS, false, monsterOverride(TinkerWorld.earthSlimeEntity.get(), 4, 4)));
  // skyslime
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> skySlimeIsland = STRUCTURE_FEATURES.register("sky_slime_island", SkySlimeIslandStructure::new);
  public static final RegistryObject<ConfiguredStructureFeature<?,?>> configuredSkySlimeIsland = CONFIGURED_STRUCTURE_FEATURES.register("sky_slime_island", () -> skySlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE, TinkerTags.Biomes.SKYSLIME_ISLANDS, false, monsterOverride(TinkerWorld.skySlimeEntity.get(), 3, 4)));
  // ckay
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> clayIsland = STRUCTURE_FEATURES.register("clay_island", ClayIslandStructure::new);
  public static final RegistryObject<ConfiguredStructureFeature<?,?>> configuredClayIsland = CONFIGURED_STRUCTURE_FEATURES.register("clay_island", () -> clayIsland.get().configured(NoneFeatureConfiguration.INSTANCE, TinkerTags.Biomes.CLAY_ISLANDS, false, monsterOverride(TinkerWorld.terracubeEntity.get(), 2, 4)));
  // nether
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> bloodIsland = STRUCTURE_FEATURES.register("blood_island", BloodSlimeIslandStructure::new);
  public static final RegistryObject<ConfiguredStructureFeature<?,?>> configuredBloodIsland = CONFIGURED_STRUCTURE_FEATURES.register("blood_island", () -> bloodIsland.get().configured(NoneFeatureConfiguration.INSTANCE, TinkerTags.Biomes.BLOOD_ISLANDS, false, monsterOverride(EntityType.MAGMA_CUBE, 4, 6)));
  // end
  public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> endSlimeIsland = STRUCTURE_FEATURES.register("end_slime_island", EnderSlimeIslandStructure::new);
  public static final RegistryObject<ConfiguredStructureFeature<?,?>> configuredEndSlimeIsland = CONFIGURED_STRUCTURE_FEATURES.register("end_slime_island", () -> endSlimeIsland.get().configured(NoneFeatureConfiguration.INSTANCE, TinkerTags.Biomes.ENDERSLIME_ISLANDS, false, monsterOverride(TinkerWorld.enderSlimeEntity.get(), 4, 4)));

  /** Creates a spawn override for a single mob */
  private static Map<MobCategory,StructureSpawnOverride> monsterOverride(EntityType<?> entity, int min, int max) {
    return Map.of(MobCategory.MONSTER, new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(entity, 1, min, max))));
  }
}
