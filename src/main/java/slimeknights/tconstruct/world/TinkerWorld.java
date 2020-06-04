package slimeknights.tconstruct.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
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
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.entity.WorldEntities;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.entity.BlueSlimeEntity;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SupplierBlockStateProvider;

import java.util.function.Supplier;

@Pulse(id = TinkerPulseIds.TINKER_WORLD_PULSE_ID, description = "Everything that's found in the world and worldgen")
public class TinkerWorld extends TinkerPulse {

  private static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, TConstruct.modID);

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_WORLD_PULSE_ID);
  
  public static PlantType slimePlantType = PlantType.create("slime");

  public static IStructurePieceType SLIME_ISLAND_PIECE;
  public static RegistryObject<Structure<NoFeatureConfig>> SLIME_ISLAND = FEATURES.register("slime_island", () -> new SlimeIslandStructure(NoFeatureConfig::deserialize));

  public static IStructurePieceType NETHER_SLIME_ISLAND_PIECE;
  public static RegistryObject<Structure<NoFeatureConfig>> NETHER_SLIME_ISLAND = FEATURES.register("nether_slime_island", () -> new NetherSlimeIslandStructure(NoFeatureConfig::deserialize));

  public static RegistryObject<Feature<SlimeTreeFeatureConfig>> TREE = FEATURES.register("tree", () -> new SlimeTreeFeature(SlimeTreeFeatureConfig::deserialize));

  public static BlockStateProviderType<SupplierBlockStateProvider> SUPPLIER_BLOCKSTATE_PROVIDER = Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, "tconstruct:supplier_state_provider", new BlockStateProviderType<>(SupplierBlockStateProvider::new));

  public static SlimeTreeFeatureConfig BLUE_SLIME_TREE_CONFIG;

  public static SlimeTreeFeatureConfig BLUE_SLIME_ISLAND_TREE_CONFIG;

  public static SlimeTreeFeatureConfig PURPLE_SLIME_TREE_CONFIG;

  public static SlimeTreeFeatureConfig PURPLE_SLIME_ISLAND_TREE_CONFIG;

  public static SlimeTreeFeatureConfig MAGMA_SLIME_TREE_CONFIG;


  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    FEATURES.register(modEventBus);
  }

  @SubscribeEvent
  public void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    BaseRegistryAdapter<Feature<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    SLIME_ISLAND_PIECE = Registry.register(Registry.STRUCTURE_PIECE, registry.getResource("slime_island_piece"), SlimeIslandPiece::new);
    NETHER_SLIME_ISLAND_PIECE = Registry.register(Registry.STRUCTURE_PIECE, registry.getResource("nether_slime_island_piece"), NetherSlimeIslandPiece::new);
  }

  @SubscribeEvent
  public void commonSetup(final FMLCommonSetupEvent event) {

    BLUE_SLIME_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.GREEN).getDefaultState(), () -> WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(),
      Blocks.AIR::getDefaultState, 5, 4, false, WorldBlocks.slime_sapling.get(SlimeGrassBlock.FoliageType.BLUE));
    BLUE_SLIME_ISLAND_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.GREEN).getDefaultState(), () -> WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.BLUE).getDefaultState(),
      () -> WorldBlocks.blue_slime_vine_middle.get().getDefaultState(), 5, 4, true, WorldBlocks.slime_sapling.get(SlimeGrassBlock.FoliageType.BLUE));
    PURPLE_SLIME_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.GREEN).getDefaultState(), () -> WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(),
      Blocks.AIR::getDefaultState, 5, 4, false, WorldBlocks.slime_sapling.get(SlimeGrassBlock.FoliageType.PURPLE));
    PURPLE_SLIME_ISLAND_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.GREEN).getDefaultState(), () -> WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.PURPLE).getDefaultState(),
      () -> WorldBlocks.purple_slime_vine_middle.get().getDefaultState(), 5, 4, true, WorldBlocks.slime_sapling.get(SlimeGrassBlock.FoliageType.PURPLE));
    MAGMA_SLIME_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_slime.get(SlimeBlock.SlimeType.MAGMA).getDefaultState(), () -> WorldBlocks.slime_leaves.get(SlimeGrassBlock.FoliageType.ORANGE).getDefaultState(),
      Blocks.AIR::getDefaultState, 5, 4, false, WorldBlocks.slime_sapling.get(SlimeGrassBlock.FoliageType.ORANGE));

    applyFeatures();
    MinecraftForge.EVENT_BUS.register(new WorldEvents());

    TinkerRegistry.tabWorld.setDisplayIcon(new ItemStack(WorldBlocks.slime_sapling.get(SlimeGrassBlock.FoliageType.BLUE)));

    EntitySpawnPlacementRegistry.register(WorldEntities.blue_slime_entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, BlueSlimeEntity::canSpawnHere);
  }


  public static void applyFeatures() {
    ConfiguredFeature<?, ?> SLIME_ISLAND_FEATURE = SLIME_ISLAND.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
    FlatGenerationSettings.FEATURE_STAGES.put(SLIME_ISLAND_FEATURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:slime_island", new ConfiguredFeature[]{SLIME_ISLAND_FEATURE});
    FlatGenerationSettings.FEATURE_CONFIGS.put(SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

    ConfiguredFeature<?, ?> NETHER_SLIME_ISLAND_FEATURE = NETHER_SLIME_ISLAND.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
    FlatGenerationSettings.FEATURE_STAGES.put(NETHER_SLIME_ISLAND_FEATURE, GenerationStage.Decoration.UNDERGROUND_DECORATION);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:nether_slime_island", new ConfiguredFeature[]{NETHER_SLIME_ISLAND_FEATURE});
    FlatGenerationSettings.FEATURE_CONFIGS.put(NETHER_SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

    ForgeRegistries.BIOMES.forEach(biome -> {
      if (biome.getCategory() == Biome.Category.NETHER) {
        if (Config.COMMON.generateSlimeIslands.get()) {
          biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, NETHER_SLIME_ISLAND.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
          biome.addStructure(NETHER_SLIME_ISLAND.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        }

        if (Config.COMMON.generateCobalt.get()) {
          addCobaltOre(biome);
        }

        if (Config.COMMON.generateArdite.get()) {
          addArditeOre(biome);
        }
      } else if (biome.getCategory() != Biome.Category.THEEND) {
        if (Config.COMMON.generateSlimeIslands.get()) {
          biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SLIME_ISLAND.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
          biome.addStructure(SLIME_ISLAND.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
          biome.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(WorldEntities.blue_slime_entity, 15, 2, 4));
        }
      }
    });
  }

  private static void addCobaltOre(Biome biome) {
    int veinCount = Config.COMMON.veinCountCobalt.get() / 2;

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.cobalt_ore.get().getDefaultState(), 5))
      .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(veinCount, 32, 0, 64))));

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.cobalt_ore.get().getDefaultState(), 5))
      .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(veinCount, 0, 0, 128))));
  }

  private static void addArditeOre(Biome biome) {
    int veinCount = Config.COMMON.veinCountArdite.get() / 2;

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.ardite_ore.get().getDefaultState(), 5))
      .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(veinCount, 32, 0, 64))));

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.ardite_ore.get().getDefaultState(), 5))
      .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(veinCount, 0, 0, 128))));
  }

  private static SlimeTreeFeatureConfig createTreeConfig(Supplier<BlockState> logSupplierIn, Supplier<BlockState> leafSupplierIn, Supplier<BlockState> vineSupplierIn, int baseHeightIn, int randomHeightIn, boolean hasVines, IPlantable saplingIn) {
    return (new SlimeTreeFeatureConfig.Builder(new SupplierBlockStateProvider(logSupplierIn),
      new SupplierBlockStateProvider(leafSupplierIn), new SupplierBlockStateProvider(vineSupplierIn))).baseHeight(baseHeightIn).randomHeight(randomHeightIn).hasVines(hasVines).setSapling(saplingIn).build();
  }

}
