package slimeknights.tconstruct.world;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
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

import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.ItemRegistryAdapter;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeSaplingBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.entity.BlueSlimeEntity;
import slimeknights.tconstruct.world.worldgen.BlueSlimeTree;
import slimeknights.tconstruct.world.worldgen.MagmaSlimeTree;
import slimeknights.tconstruct.world.worldgen.NetherSlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.NetherSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.PurpleSlimeTree;
import slimeknights.tconstruct.world.worldgen.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.SlimeIslandStructure;

@Pulse(id = TinkerPulseIds.TINKER_WORLD_PULSE_ID, description = "Everything that's found in the world and worldgen")
@ObjectHolder(TConstruct.modID)
public class TinkerWorld extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_WORLD_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> WorldClientProxy::new, () -> ServerProxy::new);

  public static final SlimeDirtBlock green_slime_dirt = injected();
  public static final SlimeDirtBlock blue_slime_dirt = injected();
  public static final SlimeDirtBlock purple_slime_dirt = injected();
  public static final SlimeDirtBlock magma_slime_dirt = injected();

  public static final SlimeGrassBlock blue_vanilla_slime_grass = injected();
  public static final SlimeGrassBlock purple_vanilla_slime_grass = injected();
  public static final SlimeGrassBlock orange_vanilla_slime_grass = injected();
  public static final SlimeGrassBlock blue_green_slime_grass = injected();
  public static final SlimeGrassBlock purple_green_slime_grass = injected();
  public static final SlimeGrassBlock orange_green_slime_grass = injected();
  public static final SlimeGrassBlock blue_blue_slime_grass = injected();
  public static final SlimeGrassBlock purple_blue_slime_grass = injected();
  public static final SlimeGrassBlock orange_blue_slime_grass = injected();
  public static final SlimeGrassBlock blue_purple_slime_grass = injected();
  public static final SlimeGrassBlock purple_purple_slime_grass = injected();
  public static final SlimeGrassBlock orange_purple_slime_grass = injected();
  public static final SlimeGrassBlock blue_magma_slime_grass = injected();
  public static final SlimeGrassBlock purple_magma_slime_grass = injected();
  public static final SlimeGrassBlock orange_magma_slime_grass = injected();

  public static final SlimeLeavesBlock blue_slime_leaves = injected();
  public static final SlimeLeavesBlock purple_slime_leaves = injected();
  public static final SlimeLeavesBlock orange_slime_leaves = injected();

  public static final SlimeTallGrassBlock blue_slime_fern = injected();
  public static final SlimeTallGrassBlock purple_slime_fern = injected();
  public static final SlimeTallGrassBlock orange_slime_fern = injected();

  public static final SlimeTallGrassBlock blue_slime_tall_grass = injected();
  public static final SlimeTallGrassBlock purple_slime_tall_grass = injected();
  public static final SlimeTallGrassBlock orange_slime_tall_grass = injected();

  public static final SlimeSaplingBlock blue_slime_sapling = injected();
  public static final SlimeSaplingBlock orange_slime_sapling = injected();
  public static final SlimeSaplingBlock purple_slime_sapling = injected();

  public static final SlimeVineBlock purple_slime_vine = injected();
  public static final SlimeVineBlock purple_slime_vine_middle = injected();
  public static final SlimeVineBlock purple_slime_vine_end = injected();

  public static final SlimeVineBlock blue_slime_vine = injected();
  public static final SlimeVineBlock blue_slime_vine_middle = injected();
  public static final SlimeVineBlock blue_slime_vine_end = injected();

  public static final EntityType<BlueSlimeEntity> blue_slime_entity = injected();

  public static PlantType slimePlantType = PlantType.Nether;

  public static IStructurePieceType SLIME_ISLAND_PIECE;
  public static final Structure<NoFeatureConfig> SLIME_ISLAND = injected();

  public static IStructurePieceType NETHER_SLIME_ISLAND_PIECE;
  public static final Structure<NoFeatureConfig> NETHER_SLIME_ISLAND = injected();

  public TinkerWorld() {
    proxy.construct();
    //slimePlantType = PlantType.create("slime");
    //System.out.println(slimePlantType);
  }

  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new SlimeDirtBlock(), "green_slime_dirt");
    registry.register(new SlimeDirtBlock(), "blue_slime_dirt");
    registry.register(new SlimeDirtBlock(), "purple_slime_dirt");
    registry.register(new SlimeDirtBlock(), "magma_slime_dirt");

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      registry.register(new SlimeGrassBlock(type), type.getName() + "_vanilla_slime_grass");
      registry.register(new SlimeGrassBlock(type), type.getName() + "_green_slime_grass");
      registry.register(new SlimeGrassBlock(type), type.getName() + "_blue_slime_grass");
      registry.register(new SlimeGrassBlock(type), type.getName() + "_purple_slime_grass");
      registry.register(new SlimeGrassBlock(type), type.getName() + "_magma_slime_grass");
    }

    registry.register(new SlimeLeavesBlock(SlimeGrassBlock.FoliageType.BLUE), "blue_slime_leaves");
    registry.register(new SlimeLeavesBlock(SlimeGrassBlock.FoliageType.PURPLE), "purple_slime_leaves");
    registry.register(new SlimeLeavesBlock(SlimeGrassBlock.FoliageType.ORANGE), "orange_slime_leaves");

    for (SlimeGrassBlock.FoliageType foliageType : SlimeGrassBlock.FoliageType.values()) {
      for (SlimeTallGrassBlock.SlimePlantType plantType : SlimeTallGrassBlock.SlimePlantType.values()) {
        registry.register(new SlimeTallGrassBlock(foliageType, plantType), foliageType.getName() + "_slime_" + plantType.getName());
      }
    }

    registry.register(new SlimeSaplingBlock(new BlueSlimeTree(false)), "blue_slime_sapling");
    registry.register(new SlimeSaplingBlock(new MagmaSlimeTree()), "orange_slime_sapling");
    registry.register(new SlimeSaplingBlock(new PurpleSlimeTree(false)), "purple_slime_sapling");

    registry.register(new SlimeVineBlock(SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.START), "purple_slime_vine");
    registry.register(new SlimeVineBlock(SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.MIDDLE), "purple_slime_vine_middle");
    registry.register(new SlimeVineBlock(SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.END), "purple_slime_vine_end");

    registry.register(new SlimeVineBlock(SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.START), "blue_slime_vine");
    registry.register(new SlimeVineBlock(SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.MIDDLE), "blue_slime_vine_middle");
    registry.register(new SlimeVineBlock(SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.END), "blue_slime_vine_end");
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
    CreativeTab tabWorld = TinkerRegistry.tabWorld;

    SpawnEggItem blueSlimeSpawnEgg = new SpawnEggItem(WorldEntities.blue_slime_entity, 0x47eff5, 0xacfff4, (new Item.Properties()).group(ItemGroup.MISC));
    registry.register(blueSlimeSpawnEgg, "blue_slime_spawn_egg");

    registry.registerBlockItem(green_slime_dirt, tabWorld);
    registry.registerBlockItem(blue_slime_dirt, tabWorld);
    registry.registerBlockItem(purple_slime_dirt, tabWorld);
    registry.registerBlockItem(magma_slime_dirt, tabWorld);

    registry.registerBlockItem(blue_vanilla_slime_grass, tabWorld);
    registry.registerBlockItem(purple_vanilla_slime_grass, tabWorld);
    registry.registerBlockItem(orange_vanilla_slime_grass, tabWorld);
    registry.registerBlockItem(blue_green_slime_grass, tabWorld);
    registry.registerBlockItem(purple_green_slime_grass, tabWorld);
    registry.registerBlockItem(orange_green_slime_grass, tabWorld);
    registry.registerBlockItem(blue_blue_slime_grass, tabWorld);
    registry.registerBlockItem(purple_blue_slime_grass, tabWorld);
    registry.registerBlockItem(orange_blue_slime_grass, tabWorld);
    registry.registerBlockItem(blue_purple_slime_grass, tabWorld);
    registry.registerBlockItem(purple_purple_slime_grass, tabWorld);
    registry.registerBlockItem(orange_purple_slime_grass, tabWorld);
    registry.registerBlockItem(blue_magma_slime_grass, tabWorld);
    registry.registerBlockItem(purple_magma_slime_grass, tabWorld);
    registry.registerBlockItem(orange_magma_slime_grass, tabWorld);

    registry.registerBlockItem(blue_slime_leaves, tabWorld);
    registry.registerBlockItem(purple_slime_leaves, tabWorld);
    registry.registerBlockItem(orange_slime_leaves, tabWorld);

    registry.registerBlockItem(blue_slime_fern, tabWorld);
    registry.registerBlockItem(purple_slime_fern, tabWorld);
    registry.registerBlockItem(orange_slime_fern, tabWorld);

    registry.registerBlockItem(blue_slime_tall_grass, tabWorld);
    registry.registerBlockItem(purple_slime_tall_grass, tabWorld);
    registry.registerBlockItem(orange_slime_tall_grass, tabWorld);

    registry.registerBlockItem(blue_slime_sapling, tabWorld);
    registry.registerBlockItem(orange_slime_sapling, tabWorld);
    registry.registerBlockItem(purple_slime_sapling, tabWorld);

    registry.registerBlockItem(purple_slime_vine, tabWorld);
    registry.registerBlockItem(purple_slime_vine_middle, tabWorld);
    registry.registerBlockItem(purple_slime_vine_end, tabWorld);

    registry.registerBlockItem(blue_slime_vine, tabWorld);
    registry.registerBlockItem(blue_slime_vine_middle, tabWorld);
    registry.registerBlockItem(blue_slime_vine_end, tabWorld);
  }

  @SubscribeEvent
  public void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    BaseRegistryAdapter<EntityType<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(WorldEntities.blue_slime_entity, "blue_slime_entity");
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

    applyFeatures(false);
  }

  @SubscribeEvent
  public void init(final InterModEnqueueEvent event) {
    proxy.init();
  }

  @SubscribeEvent
  public void postInit(final InterModProcessEvent event) {
    MinecraftForge.EVENT_BUS.register(new WorldEvents());
    proxy.postInit();
    TinkerRegistry.tabWorld.setDisplayIcon(new ItemStack(blue_slime_sapling));
  }

  public static void applyFeatures(boolean isInDebugMode) {
    if (isInDebugMode) {
      for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
        addStructure(biome, GenerationStage.Decoration.SURFACE_STRUCTURES, SLIME_ISLAND);
        addStructure(biome, GenerationStage.Decoration.UNDERGROUND_DECORATION, NETHER_SLIME_ISLAND);
      }

      ConfiguredFeature<?> SLIME_ISLAND_FEATURE = Biome.createDecoratedFeature(SLIME_ISLAND, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG);

      FlatGenerationSettings.FEATURE_STAGES.put(SLIME_ISLAND_FEATURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
      FlatGenerationSettings.STRUCTURES.put("tconstruct:slime_island", new ConfiguredFeature[] { SLIME_ISLAND_FEATURE });
      FlatGenerationSettings.FEATURE_CONFIGS.put(SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);

      ConfiguredFeature<?> NETHER_SLIME_ISLAND_FEATURE = Biome.createDecoratedFeature(NETHER_SLIME_ISLAND, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG);

      FlatGenerationSettings.FEATURE_STAGES.put(NETHER_SLIME_ISLAND_FEATURE, GenerationStage.Decoration.UNDERGROUND_DECORATION);
      FlatGenerationSettings.STRUCTURES.put("tconstruct:nether_slime_island", new ConfiguredFeature[] { NETHER_SLIME_ISLAND_FEATURE });
      FlatGenerationSettings.FEATURE_CONFIGS.put(NETHER_SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);
    }
    else {
      for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
        if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
          addStructure(biome, GenerationStage.Decoration.SURFACE_STRUCTURES, SLIME_ISLAND);
        }
        else {
          addStructure(biome, GenerationStage.Decoration.UNDERGROUND_DECORATION, NETHER_SLIME_ISLAND);
        }
      }

      ConfiguredFeature<?> SLIME_ISLAND_FEATURE = Biome.createDecoratedFeature(SLIME_ISLAND, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG);
      FlatGenerationSettings.FEATURE_STAGES.put(SLIME_ISLAND_FEATURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
      FlatGenerationSettings.STRUCTURES.put("tconstruct:slime_island", new ConfiguredFeature[] { SLIME_ISLAND_FEATURE });
      FlatGenerationSettings.FEATURE_CONFIGS.put(SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);
    }
    for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
      if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
        addStructure(biome, GenerationStage.Decoration.SURFACE_STRUCTURES, SLIME_ISLAND);
      }
      else {
        addStructure(biome, GenerationStage.Decoration.UNDERGROUND_DECORATION, NETHER_SLIME_ISLAND);
      }
    }
  }

  private static void addStructure(Biome biome, GenerationStage.Decoration stage, Structure structure) {
    biome.addFeature(stage, Biome.createDecoratedFeature(structure, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
    biome.addStructure(structure, IFeatureConfig.NO_FEATURE_CONFIG);
  }

}
