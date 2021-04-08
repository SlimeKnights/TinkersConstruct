package slimeknights.tconstruct.world;

import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
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
  public static final RegistryObject<BlockStateProviderType<SupplierBlockStateProvider>> supplierBlockstateProvider = BLOCK_STATE_PROVIDER_TYPES.register("supplier_state_provider", () -> new BlockStateProviderType<>(SupplierBlockStateProvider.TYPE_CODEC));

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
  public static StructurePieceType slimeIslandPiece;
  public static final RegistryObject<StructureFeature<DefaultFeatureConfig>> overworldSlimeIsland = STRUCTURE_FEATURES.register("overworld_slime_island", () -> new OverworldSlimeIslandStructure(DefaultFeatureConfig.CODEC));
  public static ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> SLIME_ISLAND;

  public static final RegistryObject<StructureFeature<DefaultFeatureConfig>> netherSlimeIsland = STRUCTURE_FEATURES.register("nether_slime_island", () -> new NetherSlimeIslandStructure(DefaultFeatureConfig.CODEC));
  public static ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> NETHER_SLIME_ISLAND;

  public static final RegistryObject<StructureFeature<DefaultFeatureConfig>> endSlimeIsland = STRUCTURE_FEATURES.register("end_slime_island", () -> new EndSlimeIslandStructure(DefaultFeatureConfig.CODEC));
  public static ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> END_SLIME_ISLAND;

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
    SLIME_ISLAND = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, location("overworld_slime_island"), overworldSlimeIsland.get().configure(DefaultFeatureConfig.INSTANCE));
    StructureFeature.STRUCTURES.put("tconstruct:overworld_slime_island", overworldSlimeIsland.get());
    ChunkGeneratorSettings.getInstance().getStructuresConfig().getStructures().put(overworldSlimeIsland.get(), new StructureConfig(30, 22, 14357800));

    NETHER_SLIME_ISLAND = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, location("nether_slime_island"), netherSlimeIsland.get().configure(DefaultFeatureConfig.INSTANCE));
    StructureFeature.STRUCTURES.put("tconstruct:nether_slime_island", netherSlimeIsland.get());

    END_SLIME_ISLAND = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, location("end_slime_island"), endSlimeIsland.get().configure(DefaultFeatureConfig.INSTANCE));
    StructureFeature.STRUCTURES.put("tconstruct:end_slime_island", endSlimeIsland.get());

    SKY_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("sky_slime_tree"), SLIME_TREE.get().configure((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    SKY_SLIME_ISLAND_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("sky_slime_island_tree"), SLIME_TREE.get().configure((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.skySlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
        5,
        4,
        true))
      .build()));

    ENDER_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("ender_slime_tree"), SLIME_TREE.get().configure((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    ENDER_SLIME_ISLAND_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("ender_slime_island_tree"), SLIME_TREE.get().configure((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.enderSlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
        5,
        4,
        true))
      .build()));

    BLOOD_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("blood_slime_tree"), SLIME_TREE.get().configure((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.BLOOD).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLOOD).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    ICHOR_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("ichor_slime_tree"), SLIME_TREE.get().configure((
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
