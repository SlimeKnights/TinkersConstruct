package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.nether.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.overworld.SlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.feature.NoSlimeVineTreeDecorator;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFoliagePlacer;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeLeaveVineTreeDecorator;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeatureConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SupplierBlockStateProvider;

import java.util.List;
import java.util.function.Supplier;

@Pulse(id = TinkerPulseIds.TINKER_WORLD_PULSE_ID, description = "Everything that's found in the world and worldgen")
@ObjectHolder(TConstruct.modID)
public class TinkerWorld extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_WORLD_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> WorldClientProxy::new, () -> ServerProxy::new);

  // todo: create own planttype
  public static PlantType slimePlantType = PlantType.Nether;

  public static IStructurePieceType SLIME_ISLAND_PIECE;
  public static final Structure<NoFeatureConfig> SLIME_ISLAND = injected();

  public static IStructurePieceType NETHER_SLIME_ISLAND_PIECE;
  public static final Structure<NoFeatureConfig> NETHER_SLIME_ISLAND = injected();

  public static final Feature<TreeFeatureConfig> TREE = injected();

  public static BlockStateProviderType<SupplierBlockStateProvider> SUPPLIER_BLOCKSTATE_PROVIDER = Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, "tconstruct:supplier_state_provider", new BlockStateProviderType<>(SupplierBlockStateProvider::new));
  public static FoliagePlacerType<SlimeFoliagePlacer> SLIME_FOLIAGE_PLACER = Registry.register(Registry.FOLIAGE_PLACER_TYPE, "tconstruct:slime_foliage_placer", new FoliagePlacerType<>(SlimeFoliagePlacer::new));
  public static TreeDecoratorType<SlimeLeaveVineTreeDecorator> SLIME_LEAVE_VINE_TREE_DECORATOR = Registry.register(Registry.TREE_DECORATOR_TYPE, "tconstruct:slime_leave_vine", new TreeDecoratorType<>(SlimeLeaveVineTreeDecorator::new));
  public static TreeDecoratorType<NoSlimeVineTreeDecorator> SLIME_NO_VINE_TREE_DECORATOR = Registry.register(Registry.TREE_DECORATOR_TYPE, "tconstruct:slime_no_vine", new TreeDecoratorType<>(NoSlimeVineTreeDecorator::new));

  public static TreeFeatureConfig BLUE_SLIME_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_green_slime.getDefaultState(), () -> WorldBlocks.blue_slime_leaves.getDefaultState(),
    2, 0, 5, 3, 3, ImmutableList.of(new NoSlimeVineTreeDecorator()), WorldBlocks.blue_slime_sapling);

  public static TreeFeatureConfig BLUE_SLIME_ISLAND_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_green_slime.getDefaultState(), () -> WorldBlocks.blue_slime_leaves.getDefaultState(),
    2, 0, 5, 3, 3, ImmutableList.of(new SlimeLeaveVineTreeDecorator()), WorldBlocks.blue_slime_sapling);

  public static TreeFeatureConfig PURPLE_SLIME_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_green_slime.getDefaultState(), () -> WorldBlocks.purple_slime_leaves.getDefaultState(),
    2, 0, 5, 3, 3, ImmutableList.of(new NoSlimeVineTreeDecorator()), WorldBlocks.purple_slime_sapling);

  public static TreeFeatureConfig PURPLE_SLIME_ISLAND_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_green_slime.getDefaultState(), () -> WorldBlocks.purple_slime_leaves.getDefaultState(),
    2, 0, 5, 3, 3, ImmutableList.of(new SlimeLeaveVineTreeDecorator()), WorldBlocks.purple_slime_sapling);

  public static TreeFeatureConfig MAGMA_SLIME_TREE_CONFIG = createTreeConfig(() -> WorldBlocks.congealed_magma_slime.getDefaultState(), () -> WorldBlocks.orange_slime_leaves.getDefaultState(),
    2, 0, 5, 3, 3, ImmutableList.of(new NoSlimeVineTreeDecorator()), WorldBlocks.orange_slime_sapling);

  public TinkerWorld() {
    proxy.construct();
    //slimePlantType = PlantType.create("slime"); TODO: RE-ENABLE THIS AFTER FORGE FIXES IT
  }

  @SubscribeEvent
  public void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    BaseRegistryAdapter<Feature<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new SlimeTreeFeature(TreeFeatureConfig::func_227338_a_), "tree");

    SLIME_ISLAND_PIECE = Registry.register(Registry.STRUCTURE_PIECE, registry.getResource("slime_island_piece"), SlimeIslandPiece::new);
    registry.register(new SlimeIslandStructure(NoFeatureConfig::deserialize), "slime_island");

    NETHER_SLIME_ISLAND_PIECE = Registry.register(Registry.STRUCTURE_PIECE, registry.getResource("nether_slime_island_piece"), NetherSlimeIslandPiece::new);
    registry.register(new NetherSlimeIslandStructure(NoFeatureConfig::deserialize), "nether_slime_island");
  }

  @SubscribeEvent
  public void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();

    applyFeatures();
  }

  @SubscribeEvent
  public void init(final InterModEnqueueEvent event) {
    proxy.init();
  }

  @SubscribeEvent
  public void postInit(final InterModProcessEvent event) {
    MinecraftForge.EVENT_BUS.register(new WorldEvents());
    proxy.postInit();
    TinkerRegistry.tabWorld.setDisplayIcon(new ItemStack(WorldBlocks.blue_slime_sapling));
  }

  public static void applyFeatures() {
    ConfiguredFeature<?, ?> SLIME_ISLAND_FEATURE = SLIME_ISLAND.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
    FlatGenerationSettings.FEATURE_STAGES.put(SLIME_ISLAND_FEATURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:slime_island", new ConfiguredFeature[]{SLIME_ISLAND_FEATURE});
    FlatGenerationSettings.FEATURE_CONFIGS.put(SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

    ConfiguredFeature<?, ?> NETHER_SLIME_ISLAND_FEATURE = NETHER_SLIME_ISLAND.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
    FlatGenerationSettings.FEATURE_STAGES.put(NETHER_SLIME_ISLAND_FEATURE, GenerationStage.Decoration.UNDERGROUND_DECORATION);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:nether_slime_island", new ConfiguredFeature[]{NETHER_SLIME_ISLAND_FEATURE});
    FlatGenerationSettings.FEATURE_CONFIGS.put(NETHER_SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

    ForgeRegistries.BIOMES.forEach(biome -> {
      if (biome.getCategory() == Biome.Category.NETHER) {
        if (Config.COMMON.generateSlimeIslands.get()) {
          biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, NETHER_SLIME_ISLAND.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
          biome.addStructure(NETHER_SLIME_ISLAND.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        }

        if (Config.COMMON.generateCobalt.get()) {
          addCobaltOre(biome);
        }

        if (Config.COMMON.generateArdite.get()) {
          addArditeOre(biome);
        }
      } else if (biome.getCategory() != Biome.Category.THEEND) {
        if (Config.COMMON.generateSlimeIslands.get()) {
          biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SLIME_ISLAND.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
          biome.addStructure(SLIME_ISLAND.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        }
      }
    });
  }

  private static void addCobaltOre(Biome biome) {
    int veinCount = Config.COMMON.veinCountCobalt.get() / 2;

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.cobalt_ore.getDefaultState(), 5))
      .func_227228_a_(Placement.COUNT_RANGE.func_227446_a_(new CountRangeConfig(veinCount, 32, 0, 64))));

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.cobalt_ore.getDefaultState(), 5))
      .func_227228_a_(Placement.COUNT_RANGE.func_227446_a_(new CountRangeConfig(veinCount, 0, 0, 128))));
  }

  private static void addArditeOre(Biome biome) {
    int veinCount = Config.COMMON.veinCountArdite.get() / 2;

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.ardite_ore.getDefaultState(), 5))
      .func_227228_a_(Placement.COUNT_RANGE.func_227446_a_(new CountRangeConfig(veinCount, 32, 0, 64))));

    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.ardite_ore.getDefaultState(), 5))
      .func_227228_a_(Placement.COUNT_RANGE.func_227446_a_(new CountRangeConfig(veinCount, 0, 0, 128))));
  }

  public static TreeFeatureConfig createTreeConfig(Supplier<BlockState> logSupplierIn, Supplier<BlockState> leafSupplierIn, int foliageRadiusIn, int foliageRandomRadiusIn, int baseHeightIn, int randomHeightIn, int foliageHeightIn, List<TreeDecorator> decoratorsIn, IPlantable saplingIn) {
    return (new TreeFeatureConfig.Builder(new SupplierBlockStateProvider(logSupplierIn), new SupplierBlockStateProvider(leafSupplierIn), new SlimeFoliagePlacer(foliageRadiusIn, foliageRandomRadiusIn)))
      .baseHeight(baseHeightIn).heightRandA(randomHeightIn).foliageHeight(foliageHeightIn).ignoreVines()
      .decorators(decoratorsIn).setSapling(saplingIn).build();
  }

  public static SlimeTreeFeatureConfig createTreeConfig(Supplier<BlockState> logSupplierIn, Supplier<BlockState> leafSupplierIn, Supplier<BlockState> vineSupplierIn, int baseHeightIn, int randomHeightIn, List<TreeDecorator> decoratorsIn, IPlantable saplingIn) {
    return (new SlimeTreeFeatureConfig.Builder(new SupplierBlockStateProvider(logSupplierIn),
      new SupplierBlockStateProvider(leafSupplierIn), new SupplierBlockStateProvider(vineSupplierIn))).baseHeight(baseHeightIn).randomHeight(randomHeightIn)
      .decorators(decoratorsIn).setSapling(saplingIn).build();
  }

}
