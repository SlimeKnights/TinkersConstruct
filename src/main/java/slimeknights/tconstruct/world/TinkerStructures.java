package slimeknights.tconstruct.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock.VineStage;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.end.EndSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.overworld.OverworldSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {

  static final Logger log = Util.getLogger("tinker_structures");

  /*
   * Misc
   */
  public static final RegistryObject<BlockStateProviderType<SupplierBlockStateProvider>> supplierBlockstateProvider = BLOCK_STATE_PROVIDER_TYPES.register("supplier_state_provider", () -> new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC));

  /*
   * Features
   */
  public static final RegistryObject<Feature<BaseSlimeTreeFeatureConfig>> SLIME_TREE = FEATURES.register("slime_tree", () -> new SlimeTreeFeature(BaseSlimeTreeFeatureConfig.CODEC));
  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> SKY_SLIME_TREE;
  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> SKY_SLIME_ISLAND_TREE;

  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> ENDER_SLIME_TREE;
  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> ENDER_SLIME_ISLAND_TREE;

  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> BLOOD_SLIME_TREE;
  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> ICHOR_SLIME_TREE;

  /*
   * Structures
   */
  public static IStructurePieceType slimeIslandPiece;
  public static final RegistryObject<Structure<NoFeatureConfig>> overworldSlimeIsland = STRUCTURE_FEATURES.register("overworld_slime_island", () -> new OverworldSlimeIslandStructure(NoFeatureConfig.field_236558_a_));
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> SLIME_ISLAND;

  public static final RegistryObject<Structure<NoFeatureConfig>> netherSlimeIsland = STRUCTURE_FEATURES.register("nether_slime_island", () -> new NetherSlimeIslandStructure(NoFeatureConfig.field_236558_a_));
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> NETHER_SLIME_ISLAND;

  public static final RegistryObject<Structure<NoFeatureConfig>> endSlimeIsland = STRUCTURE_FEATURES.register("end_slime_island", () -> new EndSlimeIslandStructure(NoFeatureConfig.field_236558_a_));
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> END_SLIME_ISLAND;

  @SubscribeEvent
  void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, location("slime_island_piece"), SlimeIslandPiece::new);
  }

  /**
   * Feature configuration
   *
   * PLACEMENT MOVED TO WorldEvents#onBiomeLoad
   */
  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("overworld_slime_island"), overworldSlimeIsland.get().withConfiguration(NoFeatureConfig.field_236559_b_));
    Structure.NAME_STRUCTURE_BIMAP.put("tconstruct:overworld_slime_island", overworldSlimeIsland.get());
    DimensionSettings.func_242746_i().getStructures().func_236195_a_().put(overworldSlimeIsland.get(), new StructureSeparationSettings(30, 22, 14357800));

    NETHER_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("nether_slime_island"), netherSlimeIsland.get().withConfiguration(NoFeatureConfig.field_236559_b_));
    Structure.NAME_STRUCTURE_BIMAP.put("tconstruct:nether_slime_island", netherSlimeIsland.get());

    END_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("end_slime_island"), endSlimeIsland.get().withConfiguration(NoFeatureConfig.field_236559_b_));
    Structure.NAME_STRUCTURE_BIMAP.put("tconstruct:end_slime_island", endSlimeIsland.get());

    SKY_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("sky_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    SKY_SLIME_ISLAND_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("sky_slime_island_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.skySlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
        5,
        4,
        true))
      .build()));

    ENDER_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("ender_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    ENDER_SLIME_ISLAND_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("ender_slime_island_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.enderSlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
        5,
        4,
        true))
      .build()));

    BLOOD_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("blood_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.BLOOD).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLOOD).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    ICHOR_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("ichor_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.ICHOR).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ICHOR).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
  }
}
