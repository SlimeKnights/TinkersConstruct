package slimeknights.tconstruct.world;

import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.TConstruct;
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
  public static final BlockStateProviderType<BlockStateProvider> supplierBlockstateProvider = Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, id("supplier_state_provider"), new BlockStateProviderType<>(SupplierBlockStateProvider.TYPE_CODEC));

  /*
   * Features
   */
  public static final Feature<BaseSlimeTreeFeatureConfig> SLIME_TREE = Registry.register(Registry.FEATURE, new Identifier(TConstruct.modID, "slime_tree"), new SlimeTreeFeature(BaseSlimeTreeFeatureConfig.CODEC));
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
  public static StructureFeature<DefaultFeatureConfig> overworldSlimeIsland = new OverworldSlimeIslandStructure(DefaultFeatureConfig.CODEC);
  public static ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> SLIME_ISLAND;

  public static final StructureFeature<DefaultFeatureConfig> netherSlimeIsland = Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(TConstruct.modID, "nether_slime_island"), new NetherSlimeIslandStructure(DefaultFeatureConfig.CODEC));
  public static ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> NETHER_SLIME_ISLAND;

  public static final StructureFeature<DefaultFeatureConfig> endSlimeIsland = Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(TConstruct.modID, "end_slime_island"), new EndSlimeIslandStructure(DefaultFeatureConfig.CODEC));
  public static ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> END_SLIME_ISLAND;

  @Override
  public void onInitialize() {
    //slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, location("slime_island_piece"), SlimeIslandPiece::new);
    //overworldSlimeIsland = FabricStructureBuilder.create(location("overworld_slime_island"),overworldSlimeIsland)
     // .defaultConfig(new StructureConfig(30, 22, 14357800))
      //.step(GenerationStep.Feature.SURFACE_STRUCTURES)
      //.adjustsSurface()
     // .register();
    //////SLIME_ISLAND = overworldSlimeIsland.configure(DefaultFeatureConfig.INSTANCE);
    //SLIME_ISLAND = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, location("overworld_slime_island"), overworldSlimeIsland.configure(DefaultFeatureConfig.INSTANCE));
    //StructureFeature.STRUCTURES.put("tconstruct:overworld_slime_island", overworldSlimeIsland);
    //ChunkGeneratorSettings.getInstance().getStructuresConfig().getStructures().put(overworldSlimeIsland, new StructureConfig(30, 22, 14357800));

    //RegistryKey<ConfiguredStructureFeature<?,?>> KEY = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, location("overworld_slime_island"));
    //BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, KEY.getValue(), SLIME_ISLAND);
    //BiomeModifications.addStructure(BiomeSelectors.all(), KEY);
//
//    NETHER_SLIME_ISLAND = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, location("nether_slime_island"), netherSlimeIsland.configure(DefaultFeatureConfig.INSTANCE));
//    StructureFeature.STRUCTURES.put("tconstruct:nether_slime_island", netherSlimeIsland);
//
//    END_SLIME_ISLAND = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, location("end_slime_island"), endSlimeIsland.configure(DefaultFeatureConfig.INSTANCE));
//    StructureFeature.STRUCTURES.put("tconstruct:end_slime_island", endSlimeIsland);
//
//    SKY_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("sky_slime_tree"), SLIME_TREE.configure((
//      new BaseSlimeTreeFeatureConfig.Builder(
//        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
 //       new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
 //       new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
//        5,
 //       4,
  //      false))
 //     .build()));
  //  SKY_SLIME_ISLAND_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("sky_slime_island_tree"), SLIME_TREE.configure((
 //     new BaseSlimeTreeFeatureConfig.Builder(
//        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.EARTH).getDefaultState()),
 //       new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
 //       new SupplierBlockStateProvider(() -> TinkerWorld.skySlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
 //       5,
 //       4,
 //       true))
//      .build()));
//
//    ENDER_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("ender_slime_tree"), SLIME_TREE.configure((
//      new BaseSlimeTreeFeatureConfig.Builder(
//        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState()),
//        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
//        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
//        5,
//        4,
//        false))
//      .build()));
//    ENDER_SLIME_ISLAND_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("ender_slime_island_tree"), SLIME_TREE.configure((
//      new BaseSlimeTreeFeatureConfig.Builder(
//        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.SKY).getDefaultState()),
//        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
//        new SupplierBlockStateProvider(() -> TinkerWorld.enderSlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
//        5,
//        4,
//        true))
//      .build()));
//
//    BLOOD_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("blood_slime_tree"), SLIME_TREE.configure((
//      new BaseSlimeTreeFeatureConfig.Builder(
//        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.BLOOD).getDefaultState()),
//        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLOOD).getDefaultState()),
//        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
//        5,
//        4,
//        false))
//      .build()));
//    ICHOR_SLIME_TREE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, location("ichor_slime_tree"), SLIME_TREE.configure((
//      new BaseSlimeTreeFeatureConfig.Builder(
//        new SupplierBlockStateProvider(() -> TinkerWorld.congealedSlime.get(SlimeType.ICHOR).getDefaultState()),
//        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ICHOR).getDefaultState()),
//        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
//        5,
//        4,
//        false))
//      .build()));
  }
}
