package slimeknights.tconstruct.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock.SlimeType;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SupplierBlockStateProvider;

import java.util.function.Supplier;

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_structures");

  /*
   * Structure pieces
   */
  public static IStructurePieceType slimeIslandPiece;
  public static IStructurePieceType netherSlimeIslandPiece;

  @SubscribeEvent
  void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, location("slime_island_piece"), SlimeIslandPiece::new);
    netherSlimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, location("nether_slime_island_piece"), NetherSlimeIslandPiece::new);
  }

  /*
   * Config
   */
  public static SlimeTreeFeatureConfig blueSlimeTreeConfig;
  public static SlimeTreeFeatureConfig blueSlimeIslandTreeConfig;
  public static SlimeTreeFeatureConfig purpleSlimeTreeConfig;
  public static SlimeTreeFeatureConfig purpleSlimeIslandTreeConfig;
  public static SlimeTreeFeatureConfig magmaSlimeTreeConfig;

  @SubscribeEvent
  void registerFeatureConfig(RegistryEvent.Register<Feature<?>> event) {
    // blue
    Supplier<BlockState> congealedGreen = () -> TinkerWorld.congealedSlime.get(SlimeType.GREEN).getDefaultState();
    Supplier<BlockState> leavesBlue = () -> TinkerWorld.slimeLeaves.get(FoliageType.BLUE).getDefaultState();
    blueSlimeTreeConfig = createTreeConfig(congealedGreen, leavesBlue, Blocks.AIR::getDefaultState, 5, 4, false, TinkerWorld.slimeSapling.get(SlimeGrassBlock.FoliageType.BLUE));
    blueSlimeIslandTreeConfig = createTreeConfig(congealedGreen, leavesBlue, () -> TinkerWorld.blueSlimeVineMiddle.get().getDefaultState(), 5, 4, true, TinkerWorld.slimeSapling.get(SlimeGrassBlock.FoliageType.BLUE));

    // purple
    Supplier<BlockState> leavesPurple = () -> TinkerWorld.slimeLeaves.get(FoliageType.PURPLE).getDefaultState();
    purpleSlimeTreeConfig = createTreeConfig(congealedGreen, leavesPurple, Blocks.AIR::getDefaultState, 5, 4, false, TinkerWorld.slimeSapling.get(SlimeGrassBlock.FoliageType.PURPLE));
    purpleSlimeIslandTreeConfig = createTreeConfig(congealedGreen, leavesPurple, () -> TinkerWorld.purpleSlimeVineMiddle.get().getDefaultState(), 5, 4, true, TinkerWorld.slimeSapling.get(SlimeGrassBlock.FoliageType.PURPLE));

    // magma
    magmaSlimeTreeConfig = createTreeConfig(() -> TinkerWorld.congealedSlime.get(SlimeBlock.SlimeType.MAGMA).getDefaultState(),
                                            () -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState(),
                                            Blocks.AIR::getDefaultState, 5, 4, false, TinkerWorld.slimeSapling.get(SlimeGrassBlock.FoliageType.ORANGE));
  }

  private static SlimeTreeFeatureConfig createTreeConfig(Supplier<BlockState> logSupplierIn, Supplier<BlockState> leafSupplierIn, Supplier<BlockState> vineSupplierIn, int baseHeightIn, int randomHeightIn, boolean hasVines, IPlantable saplingIn) {
    return new SlimeTreeFeatureConfig.Builder(new SupplierBlockStateProvider(logSupplierIn), new SupplierBlockStateProvider(leafSupplierIn), new SupplierBlockStateProvider(vineSupplierIn))
                                      .baseHeight(baseHeightIn)
                                      .randomHeight(randomHeightIn)
                                      .hasVines(hasVines)
                                      .setSapling(saplingIn)
                                      .build();
  }

  /*
   * Misc
   */
  public static final BlockStateProviderType<SupplierBlockStateProvider> supplierBlockstateProvider = Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, location("supplier_state_provider"), new BlockStateProviderType<>(SupplierBlockStateProvider::new));;

  /*
   * Features
   */
  public static final RegistryObject<Feature<SlimeTreeFeatureConfig>> tree = FEATURES.register("tree", () -> new SlimeTreeFeature(SlimeTreeFeatureConfig::deserialize));
  // islands
  public static final RegistryObject<Structure<NoFeatureConfig>> slimeIsland = FEATURES.register("slime_island", () -> new SlimeIslandStructure(NoFeatureConfig::deserialize));
  public static final RegistryObject<Structure<NoFeatureConfig>> netherSlimeIsland = FEATURES.register("nether_slime_island", () -> new NetherSlimeIslandStructure(NoFeatureConfig::deserialize));

  /*
   * Feature placement and configuration
   */
  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    // slime island
    ConfiguredFeature<?, ?> slimeIslandFeature = slimeIsland.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
    FlatGenerationSettings.FEATURE_STAGES.put(slimeIslandFeature, GenerationStage.Decoration.SURFACE_STRUCTURES);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:slime_island", new ConfiguredFeature[]{slimeIslandFeature});
    FlatGenerationSettings.FEATURE_CONFIGS.put(slimeIslandFeature, IFeatureConfig.NO_FEATURE_CONFIG);
    // nether island
    ConfiguredFeature<?, ?> NETHER_SLIME_ISLAND_FEATURE = netherSlimeIsland.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
    FlatGenerationSettings.FEATURE_STAGES.put(NETHER_SLIME_ISLAND_FEATURE, GenerationStage.Decoration.UNDERGROUND_DECORATION);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:nether_slime_island", new ConfiguredFeature[]{NETHER_SLIME_ISLAND_FEATURE});
    FlatGenerationSettings.FEATURE_CONFIGS.put(NETHER_SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

    // add islands and ores to worldgen
    ForgeRegistries.BIOMES.forEach(biome -> {
      // nether slime island to the nether
      // nether ores to the nether
      if (biome.getCategory() == Biome.Category.NETHER) {
        // FIXME: constant config
        if (Config.COMMON.generateSlimeIslands.get()) {
          biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, netherSlimeIsland.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
          biome.addStructure(netherSlimeIsland.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        }

        // FIXME: constant config
        if (Config.COMMON.generateCobalt.get()) {
          addNetherOre(biome, TinkerWorld.cobaltOre, Config.COMMON.veinCountCobalt);
        }
        // FIXME: constant config
        if (Config.COMMON.generateArdite.get()) {
          addNetherOre(biome, TinkerWorld.arditeOre, Config.COMMON.veinCountArdite);
        }
      // overworld islands to the overworld
      } else if (biome.getCategory() != Biome.Category.THEEND) {
        // FIXME: constant config
        if (Config.COMMON.generateSlimeIslands.get()) {
          biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, slimeIsland.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
          biome.addStructure(slimeIsland.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
          biome.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(TinkerWorld.blueSlimeEntity.get(), 15, 2, 4));
        }
      }
    });
  }

  /**
   * Adds an ore with nether placement
   * @param biome  Nether biome
   * @param block  Block to generate
   * @param count  Vein side config
   */
  private static void addNetherOre(Biome biome, Supplier<? extends Block> block, ConfigValue<Integer> count) {
    // FIXME: constant config
    int veinCount = count.get() / 2;
    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
                     Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, block.get().getDefaultState(), 5))
                                .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(veinCount, 32, 0, 64))));
    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION,
                     Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, block.get().getDefaultState(), 5))
                                .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(veinCount, 0, 0, 128))));
  }
}
