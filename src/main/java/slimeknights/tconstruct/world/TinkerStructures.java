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
import slimeknights.tconstruct.shared.block.StickySlimeBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandStructure;
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
  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> BLUE_SLIME_TREE;
  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> BLUE_SLIME_TREE_ISLAND;

  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> PURPLE_SLIME_TREE;
  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> PURPLE_SLIME_TREE_ISLAND;

  public static ConfiguredFeature<BaseSlimeTreeFeatureConfig, ?> MAGMA_SLIME_TREE;

  /*
   * Structures
   */
  public static IStructurePieceType slimeIslandPiece;
  public static final RegistryObject<Structure<NoFeatureConfig>> slimeIsland = STRUCTURE_FEATURES.register("slime_island", () -> new SlimeIslandStructure(NoFeatureConfig.field_236558_a_));
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> SLIME_ISLAND;

  public static IStructurePieceType netherSlimeIslandPiece;
  public static final RegistryObject<Structure<NoFeatureConfig>> netherSlimeIsland = STRUCTURE_FEATURES.register("nether_slime_island", () -> new NetherSlimeIslandStructure(NoFeatureConfig.field_236558_a_));
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> NETHER_SLIME_ISLAND;

  @SubscribeEvent
  void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, location("slime_island_piece"), SlimeIslandPiece::new);
    netherSlimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, location("nether_slime_island_piece"), NetherSlimeIslandPiece::new);
  }

  /**
   * Feature configuration
   *
   * PLACEMENT MOVED TO WorldEvents#onBiomeLoad
   */
  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("slime_island"), slimeIsland.get().func_236391_a_(NoFeatureConfig.field_236559_b_));
    Structure.field_236365_a_.put("tconstruct:slime_island", slimeIsland.get());
    DimensionSettings.func_242746_i().getStructures().func_236195_a_().put(slimeIsland.get(), new StructureSeparationSettings(20, 11, 14357800));

    NETHER_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("nether_slime_island"), netherSlimeIsland.get().func_236391_a_(NoFeatureConfig.field_236559_b_));
    Structure.field_236365_a_.put("tconstruct:nether_slime_island", netherSlimeIsland.get());
    DimensionSettings.func_242746_i().getStructures().func_236195_a_().put(netherSlimeIsland.get(), new StructureSeparationSettings(20, 11, 14357800));

    BLUE_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("blue_slime"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(StickySlimeBlock.SlimeType.GREEN).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build())
    );
    BLUE_SLIME_TREE_ISLAND = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("blue_slime_island"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(StickySlimeBlock.SlimeType.GREEN).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.blueSlimeVineMiddle.get().getDefaultState()),
        5,
        4,
        true))
      .build())
    );

    PURPLE_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("purple_slime"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(StickySlimeBlock.SlimeType.GREEN).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build())
    );
    PURPLE_SLIME_TREE_ISLAND = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("purple_slime_island"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(StickySlimeBlock.SlimeType.GREEN).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.purpleSlimeVineMiddle.get().getDefaultState()),
        5,
        4,
        true))
      .build())
    );

    MAGMA_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("magma_slime"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(StickySlimeBlock.SlimeType.MAGMA).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build())
    );
  }
}
