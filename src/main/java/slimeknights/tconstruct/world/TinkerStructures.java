package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
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
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock.VineStage;
import slimeknights.tconstruct.world.worldgen.islands.BloodSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EnderSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SkySlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;
import slimeknights.tconstruct.world.worldgen.trees.config.BaseSlimeTreeFeatureConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

import java.util.Objects;

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
  public static final RegistryObject<Structure<NoFeatureConfig>> skySlimeIsland = STRUCTURE_FEATURES.register("overworld_slime_island", SkySlimeIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> SKY_SLIME_ISLAND;

  public static final RegistryObject<Structure<NoFeatureConfig>> bloodSlimeIsland = STRUCTURE_FEATURES.register("nether_slime_island", BloodSlimeIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> BLOOD_SLIME_ISLAND;

  public static final RegistryObject<Structure<NoFeatureConfig>> endSlimeIsland = STRUCTURE_FEATURES.register("end_slime_island", EnderSlimeIslandStructure::new);
  public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> END_SLIME_ISLAND;

  @SubscribeEvent
  void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    slimeIslandPiece = Registry.register(Registry.STRUCTURE_PIECE, location("slime_island_piece"), SlimeIslandPiece::new);
  }

  /** Adds the settings to the given dimension */
  private static void addStructureSettings(RegistryKey<DimensionSettings> key, Structure<?> structure, StructureSeparationSettings settings) {
    DimensionSettings dimensionSettings = WorldGenRegistries.NOISE_SETTINGS.getValueForKey(key);
    if (dimensionSettings != null) {
      dimensionSettings.getStructures().func_236195_a_().put(structure, settings);
    }
  }

  /** Adds the structure to the structure map */
  private static void addStructureToMap(Structure<?> structure) {
    Structure.NAME_STRUCTURE_BIMAP.put(Objects.requireNonNull(structure.getRegistryName()).toString(), structure);
  }

  /**
   * Feature configuration
   *
   * PLACEMENT MOVED TO WorldEvents#onBiomeLoad
   */
  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    addStructureToMap(skySlimeIsland.get());
    addStructureToMap(bloodSlimeIsland.get());
    addStructureToMap(endSlimeIsland.get());

    // sky slime islands
    SKY_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("sky_slime_island"), skySlimeIsland.get().withConfiguration(NoFeatureConfig.field_236559_b_));
    StructureSeparationSettings overworldSettings = new StructureSeparationSettings(30, 22, 14357800);
    DimensionSettings.func_242746_i().getStructures().func_236195_a_().put(skySlimeIsland.get(), overworldSettings);
    addStructureSettings(DimensionSettings.field_242735_d, skySlimeIsland.get(), overworldSettings);
    addStructureSettings(DimensionSettings.field_242739_h, skySlimeIsland.get(), overworldSettings);

    // blood slime islands
    BLOOD_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("blood_slime_island"), bloodSlimeIsland.get().withConfiguration(NoFeatureConfig.field_236559_b_));
    StructureSeparationSettings netherSettings = new StructureSeparationSettings(15, 11, 65245622);
    addStructureSettings(DimensionSettings.field_242736_e, bloodSlimeIsland.get(), netherSettings);

    // end slime islands
    END_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, location("end_slime_island"), endSlimeIsland.get().withConfiguration(NoFeatureConfig.field_236559_b_));
    StructureSeparationSettings endSettings = new StructureSeparationSettings(15, 11, 65245622);
    addStructureSettings(DimensionSettings.field_242737_f, endSlimeIsland.get(), endSettings);

    // add to the default for anyone creating dimension settings later, hopefully its soon enough
    event.enqueueWork(() -> {
      ImmutableMap.Builder<Structure<?>, StructureSeparationSettings> builder = ImmutableMap.builder();
      builder.putAll(DimensionStructuresSettings.field_236191_b_);
      builder.put(skySlimeIsland.get(), overworldSettings);
      builder.put(bloodSlimeIsland.get(), netherSettings);
      builder.put(endSlimeIsland.get(), endSettings);
      DimensionStructuresSettings.field_236191_b_ = builder.build();
    });

    SKY_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("sky_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.skyroot.getLog().getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    SKY_SLIME_ISLAND_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("sky_slime_island_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.skyroot.getLog().getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.SKY).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.skySlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
        5,
        4,
        true))
      .build()));

    ENDER_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("ender_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.greenheart.getLog().getDefaultState()), // TODO: temporary until we have proper green trees and ender shrooms
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    ENDER_SLIME_ISLAND_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("ender_slime_island_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.greenheart.getLog().getDefaultState()), // TODO: temporary until we have proper green trees and ender shrooms
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ENDER).getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.enderSlimeVine.get().getDefaultState().with(SlimeVineBlock.STAGE, VineStage.MIDDLE)),
        5,
        4,
        true))
      .build()));

    BLOOD_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("blood_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.bloodshroom.getLog().getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLOOD).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
    ICHOR_SLIME_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("ichor_slime_tree"), SLIME_TREE.get().withConfiguration((
      new BaseSlimeTreeFeatureConfig.Builder(
        new SupplierBlockStateProvider(() -> TinkerWorld.bloodshroom.getLog().getDefaultState()),
        new SupplierBlockStateProvider(() -> TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ICHOR).getDefaultState()),
        new SupplierBlockStateProvider(Blocks.AIR::getDefaultState),
        5,
        4,
        false))
      .build()));
  }
}
