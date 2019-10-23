package slimeknights.tconstruct.world;

import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
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
import slimeknights.tconstruct.world.worldgen.NetherSlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.SlimeIslandStructure;

@Pulse(id = TinkerPulseIds.TINKER_WORLD_PULSE_ID, description = "Everything that's found in the world and worldgen")
@ObjectHolder(TConstruct.modID)
public class TinkerWorld extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_WORLD_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> WorldClientProxy::new, () -> ServerProxy::new);

  public static PlantType slimePlantType = PlantType.Nether;

  public static IStructurePieceType SLIME_ISLAND_PIECE;
  public static final Structure<NoFeatureConfig> SLIME_ISLAND = injected();

  public static IStructurePieceType NETHER_SLIME_ISLAND_PIECE;
  public static final Structure<NoFeatureConfig> NETHER_SLIME_ISLAND = injected();

  public TinkerWorld() {
    proxy.construct();
    //slimePlantType = PlantType.create("slime"); TODO: RE-ENABLE THIS AFTER FORGE FIXES IT
  }

  @SubscribeEvent
  public void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    BaseRegistryAdapter<Feature<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

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
    ConfiguredFeature<?> SLIME_ISLAND_FEATURE = Biome.createDecoratedFeature(SLIME_ISLAND, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG);
    FlatGenerationSettings.FEATURE_STAGES.put(SLIME_ISLAND_FEATURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:slime_island", new ConfiguredFeature[] { SLIME_ISLAND_FEATURE });
    FlatGenerationSettings.FEATURE_CONFIGS.put(SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

    ConfiguredFeature<?> NETHER_SLIME_ISLAND_FEATURE = Biome.createDecoratedFeature(NETHER_SLIME_ISLAND, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG);
    FlatGenerationSettings.FEATURE_STAGES.put(NETHER_SLIME_ISLAND_FEATURE, GenerationStage.Decoration.UNDERGROUND_DECORATION);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:nether_slime_island", new ConfiguredFeature[] { NETHER_SLIME_ISLAND_FEATURE });
    FlatGenerationSettings.FEATURE_CONFIGS.put(NETHER_SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

    if (Config.SERVER.generateSlimeIslands.get()) {
      for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
        if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
          addStructure(biome, GenerationStage.Decoration.SURFACE_STRUCTURES, SLIME_ISLAND);
        }
        else if (biome.getCategory() == Biome.Category.NETHER) {
          addStructure(biome, GenerationStage.Decoration.UNDERGROUND_DECORATION, NETHER_SLIME_ISLAND);

          if (Config.SERVER.generateCobalt.get()) {
            addCobaltOre(biome);
          }

          if (Config.SERVER.generateArdite.get()) {
            addArditeOre(biome);
          }
        }
      }
    }
  }

  private static void addStructure(Biome biome, GenerationStage.Decoration stage, Structure structure) {
    biome.addFeature(stage, Biome.createDecoratedFeature(structure, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
    biome.addStructure(structure, IFeatureConfig.NO_FEATURE_CONFIG);
  }

  private static void addCobaltOre(Biome biome) {
    int veinCount = Config.SERVER.veinCountCobalt.get() / 2;
    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.cobalt_ore.getDefaultState(), 5), Placement.COUNT_RANGE, new CountRangeConfig(veinCount, 32, 0, 64)));
    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.cobalt_ore.getDefaultState(), 5), Placement.COUNT_RANGE, new CountRangeConfig(veinCount, 0, 0, 128)));
  }

  private static void addArditeOre(Biome biome) {
    int veinCount = Config.SERVER.veinCountArdite.get() / 2;
    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.ardite_ore.getDefaultState(), 5), Placement.COUNT_RANGE, new CountRangeConfig(veinCount, 32, 0, 64)));
    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, WorldBlocks.ardite_ore.getDefaultState(), 5), Placement.COUNT_RANGE, new CountRangeConfig(veinCount, 0, 0, 128)));
  }

}
