package slimeknights.tconstruct.world;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
import slimeknights.tconstruct.world.data.StructureRepalleter;
import slimeknights.tconstruct.world.worldgen.islands.IslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.IslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

import java.util.Map;

import static slimeknights.tconstruct.TConstruct.getResource;

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_structures");
  private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TConstruct.MOD_ID);
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, TConstruct.MOD_ID);
  private static final DeferredRegister<Structure> STRUCTURE = DeferredRegister.create(Registry.STRUCTURE_REGISTRY, TConstruct.MOD_ID);
  private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, TConstruct.MOD_ID);
  private static final DeferredRegister<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, TConstruct.MOD_ID);

  public TinkerStructures() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    FEATURES.register(bus);
    STRUCTURE_TYPE.register(bus);
    STRUCTURE.register(bus);
    STRUCTURE_PIECE.register(bus);
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
  public static final RegistryObject<StructurePieceType> islandPiece = STRUCTURE_PIECE.register("island", () -> IslandPiece::new);
  public static final RegistryObject<StructureType<IslandStructure>> island = STRUCTURE_TYPE.register("island", () -> () -> IslandStructure.CODEC);
  // earthslime
  public static final RegistryObject<Structure> earthSlimeIsland = STRUCTURE.register("earth_slime_island", () -> IslandStructure.seaBuilder().addDefaultTemplates(getResource("islands/earth/")).addTree(earthSlimeIslandTree, 1).addSlimyGrass(SlimeType.EARTH)
      .build(new StructureSettings(BuiltinRegistries.BIOME.getOrCreateTag(TinkerTags.Biomes.EARTHSLIME_ISLANDS), monsterOverride(TinkerWorld.earthSlimeEntity.get(), 4, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
  // skyslime
  public static final RegistryObject<Structure> skySlimeIsland = STRUCTURE.register("sky_slime_island", () -> IslandStructure.skyBuilder().addDefaultTemplates(getResource("islands/sky/")).addTree(skySlimeIslandTree, 1).addSlimyGrass(SlimeType.SKY).vines(TinkerWorld.skySlimeVine.get())
      .build(new StructureSettings(BuiltinRegistries.BIOME.getOrCreateTag(TinkerTags.Biomes.SKYSLIME_ISLANDS), monsterOverride(TinkerWorld.skySlimeEntity.get(), 3, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
  // blood
  public static final RegistryObject<Structure> bloodIsland = STRUCTURE.register("blood_island", () -> IslandStructure.seaBuilder().addDefaultTemplates(getResource("islands/blood/")).addTree(bloodSlimeIslandFungus, 1).addSlimyGrass(SlimeType.BLOOD)
      .build(new StructureSettings(BuiltinRegistries.BIOME.getOrCreateTag(TinkerTags.Biomes.BLOOD_ISLANDS), monsterOverride(EntityType.MAGMA_CUBE, 4, 6), Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
  // enderslime
  public static final RegistryObject<Structure> endSlimeIsland = STRUCTURE.register("end_slime_island", () -> IslandStructure.skyBuilder().addDefaultTemplates(getResource("islands/ender/")).addTree(enderSlimeIslandTree, 1).addSlimyGrass(SlimeType.ENDER).vines(TinkerWorld.enderSlimeVine.get())
      .build(new StructureSettings(BuiltinRegistries.BIOME.getOrCreateTag(TinkerTags.Biomes.ENDERSLIME_ISLANDS), monsterOverride(TinkerWorld.enderSlimeEntity.get(), 4, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
  // clay
  public static final RegistryObject<Structure> clayIsland = STRUCTURE.register("clay_island", () -> IslandStructure.skyBuilder().addDefaultTemplates(getResource("islands/dirt/")).addTree(TreeFeatures.OAK, 4).addTree(TreeFeatures.BIRCH, 3).addTree(TreeFeatures.SPRUCE, 2).addTree(TreeFeatures.ACACIA, 1).addTree(TreeFeatures.JUNGLE_TREE_NO_VINE, 1).addGrass(Blocks.GRASS, 7).addGrass(Blocks.FERN, 1)
      .build(new StructureSettings(BuiltinRegistries.BIOME.getOrCreateTag(TinkerTags.Biomes.CLAY_ISLANDS), monsterOverride(TinkerWorld.terracubeEntity.get(), 2, 4), Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));


  /** Creates a spawn override for a single mob */
  private static Map<MobCategory,StructureSpawnOverride> monsterOverride(EntityType<?> entity, int min, int max) {
    return Map.of(MobCategory.MONSTER, new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(entity, 1, min, max))));
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();
    datagenerator.addProvider(server, new StructureRepalleter(datagenerator, existingFileHelper));
    //    datagenerator.addProvider(server, new StructureUpdater(datagenerator, existingFileHelper, TConstruct.MOD_ID, PackType.SERVER_DATA, "structures"));
    //    datagenerator.addProvider(event.includeClient(), new StructureUpdater(datagenerator, existingFileHelper, TConstruct.MOD_ID, PackType.CLIENT_RESOURCES, "book/structures"));
  }
}
