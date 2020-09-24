package slimeknights.tconstruct.world;

import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
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
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.feature.SupplierBlockStateProvider;

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
   * Config
   */
  /*
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
   */

  /*
   * Features
   */
  //public static final RegistryObject<Feature<SlimeTreeFeatureConfig>> tree = FEATURES.register("tree", () -> new SlimeTreeFeature(SlimeTreeFeatureConfig::deserialize));

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
    //DynamicRegistries.func_239770_b_().getRegistry(Registry.NOISE_SETTINGS_KEY).getOrThrow(DimensionSettings.field_242736_e).getStructures().func_236195_a_().put(netherSlimeIsland.get(), new StructureSeparationSettings(20, 11, 14357800));
  }
}
