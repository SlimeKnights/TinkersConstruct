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
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
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
import slimeknights.tconstruct.world.worldgen.PurpleSlimeTree;
import slimeknights.tconstruct.world.worldgen.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.SlimeIslandStructure;

@Pulse(id = TinkerPulseIds.TINKER_WORLD_PULSE_ID, description = "Everything that's found in the world and worldgen")
@ObjectHolder(TConstruct.modID)
public class TinkerWorld extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_WORLD_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> WorldClientProxy::new, () -> ServerProxy::new);

  public static final SlimeDirtBlock green_slime_dirt = null;
  public static final SlimeDirtBlock blue_slime_dirt = null;
  public static final SlimeDirtBlock purple_slime_dirt = null;
  public static final SlimeDirtBlock magma_slime_dirt = null;

  public static final SlimeGrassBlock blue_vanilla_slime_grass = null;
  public static final SlimeGrassBlock purple_vanilla_slime_grass = null;
  public static final SlimeGrassBlock orange_vanilla_slime_grass = null;
  public static final SlimeGrassBlock blue_green_slime_grass = null;
  public static final SlimeGrassBlock purple_green_slime_grass = null;
  public static final SlimeGrassBlock orange_green_slime_grass = null;
  public static final SlimeGrassBlock blue_blue_slime_grass = null;
  public static final SlimeGrassBlock purple_blue_slime_grass = null;
  public static final SlimeGrassBlock orange_blue_slime_grass = null;
  public static final SlimeGrassBlock blue_purple_slime_grass = null;
  public static final SlimeGrassBlock purple_purple_slime_grass = null;
  public static final SlimeGrassBlock orange_purple_slime_grass = null;
  public static final SlimeGrassBlock blue_magma_slime_grass = null;
  public static final SlimeGrassBlock purple_magma_slime_grass = null;
  public static final SlimeGrassBlock orange_magma_slime_grass = null;

  public static final SlimeLeavesBlock blue_slime_leaves = null;
  public static final SlimeLeavesBlock purple_slime_leaves = null;
  public static final SlimeLeavesBlock orange_slime_leaves = null;

  public static final SlimeTallGrassBlock blue_slime_fern = null;
  public static final SlimeTallGrassBlock purple_slime_fern = null;
  public static final SlimeTallGrassBlock orange_slime_fern = null;

  public static final SlimeTallGrassBlock blue_slime_tall_grass = null;
  public static final SlimeTallGrassBlock purple_slime_tall_grass = null;
  public static final SlimeTallGrassBlock orange_slime_tall_grass = null;

  public static final SlimeSaplingBlock blue_slime_sapling = null;
  public static final SlimeSaplingBlock orange_slime_sapling = null;
  public static final SlimeSaplingBlock purple_slime_sapling = null;

  public static final SlimeVineBlock purple_slime_vine = null;
  public static final SlimeVineBlock purple_slime_vine_middle = null;
  public static final SlimeVineBlock purple_slime_vine_end = null;

  public static final SlimeVineBlock blue_slime_vine = null;
  public static final SlimeVineBlock blue_slime_vine_middle = null;
  public static final SlimeVineBlock blue_slime_vine_end = null;

  public static final EntityType<BlueSlimeEntity> blue_slime_entity = null;

  public static PlantType slimePlantType = PlantType.Nether;

  public static IStructurePieceType SLIME_ISLAND_PIECE;
  public static final Structure<NoFeatureConfig> SLIME_ISLAND = null;

  public TinkerWorld() {
    proxy.construct();
    //slimePlantType = PlantType.create("slime");
    //System.out.println(slimePlantType);
  }

  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    register(registry, new SlimeDirtBlock(), "green_slime_dirt");
    register(registry, new SlimeDirtBlock(), "blue_slime_dirt");
    register(registry, new SlimeDirtBlock(), "purple_slime_dirt");
    register(registry, new SlimeDirtBlock(), "magma_slime_dirt");

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      register(registry, new SlimeGrassBlock(type), type.getName() + "_vanilla_slime_grass");
      register(registry, new SlimeGrassBlock(type), type.getName() + "_green_slime_grass");
      register(registry, new SlimeGrassBlock(type), type.getName() + "_blue_slime_grass");
      register(registry, new SlimeGrassBlock(type), type.getName() + "_purple_slime_grass");
      register(registry, new SlimeGrassBlock(type), type.getName() + "_magma_slime_grass");
    }

    register(registry, new SlimeLeavesBlock(SlimeGrassBlock.FoliageType.BLUE), "blue_slime_leaves");
    register(registry, new SlimeLeavesBlock(SlimeGrassBlock.FoliageType.PURPLE), "purple_slime_leaves");
    register(registry, new SlimeLeavesBlock(SlimeGrassBlock.FoliageType.ORANGE), "orange_slime_leaves");

    for (SlimeGrassBlock.FoliageType foliageType : SlimeGrassBlock.FoliageType.values()) {
      for (SlimeTallGrassBlock.SlimePlantType plantType : SlimeTallGrassBlock.SlimePlantType.values()) {
        register(registry, new SlimeTallGrassBlock(foliageType, plantType), foliageType.getName() + "_slime_" + plantType.getName());
      }
    }

    register(registry, new SlimeSaplingBlock(new BlueSlimeTree(false)), "blue_slime_sapling");
    register(registry, new SlimeSaplingBlock(new MagmaSlimeTree()), "orange_slime_sapling");
    register(registry, new SlimeSaplingBlock(new PurpleSlimeTree(false)), "purple_slime_sapling");

    register(registry, new SlimeVineBlock(SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.START), "purple_slime_vine");
    register(registry, new SlimeVineBlock(SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.MIDDLE), "purple_slime_vine_middle");
    register(registry, new SlimeVineBlock(SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.END), "purple_slime_vine_end");

    register(registry, new SlimeVineBlock(SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.START), "blue_slime_vine");
    register(registry, new SlimeVineBlock(SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.MIDDLE), "blue_slime_vine_middle");
    register(registry, new SlimeVineBlock(SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.END), "blue_slime_vine_end");
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    register(registry, new SpawnEggItem(WorldEntities.blue_slime_entity, 0x47eff5, 0xacfff4, (new Item.Properties()).group(ItemGroup.MISC)), "blue_slime_spawn_egg");

    registerBlockItem(registry, green_slime_dirt, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blue_slime_dirt, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime_dirt, TinkerRegistry.tabWorld);
    registerBlockItem(registry, magma_slime_dirt, TinkerRegistry.tabWorld);

    registerBlockItem(registry, blue_vanilla_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_vanilla_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_vanilla_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blue_green_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_green_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_green_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blue_blue_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_blue_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_blue_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blue_purple_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_purple_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_purple_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blue_magma_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_magma_slime_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_magma_slime_grass, TinkerRegistry.tabWorld);

    registerBlockItem(registry, blue_slime_leaves, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime_leaves, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_slime_leaves, TinkerRegistry.tabWorld);

    registerBlockItem(registry, blue_slime_fern, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime_fern, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_slime_fern, TinkerRegistry.tabWorld);

    registerBlockItem(registry, blue_slime_tall_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime_tall_grass, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_slime_tall_grass, TinkerRegistry.tabWorld);

    registerBlockItem(registry, blue_slime_sapling, TinkerRegistry.tabWorld);
    registerBlockItem(registry, orange_slime_sapling, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime_sapling, TinkerRegistry.tabWorld);

    registerBlockItem(registry, purple_slime_vine, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime_vine_middle, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime_vine_end, TinkerRegistry.tabWorld);

    registerBlockItem(registry, blue_slime_vine, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blue_slime_vine_middle, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blue_slime_vine_end, TinkerRegistry.tabWorld);
  }

  @SubscribeEvent
  public void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    IForgeRegistry<EntityType<?>> registry = event.getRegistry();

    registry.register(WorldEntities.blue_slime_entity.setRegistryName("tconstruct:blue_slime_entity"));
  }

  @SubscribeEvent
  public void onFeaturesRegistry(RegistryEvent.Register<Feature<?>> event) {
    IForgeRegistry<Feature<?>> registry = event.getRegistry();
    SLIME_ISLAND_PIECE = Registry.register(Registry.STRUCTURE_PIECE, "tconstruct:slime_island_piece", SlimeIslandPiece::new);
    register(registry, new SlimeIslandStructure(NoFeatureConfig::deserialize), "slime_island");
    //registerFeature(event, new SlimeIslandStructure(NoFeatureConfig::deserialize), "slime_island");
  }

  @SubscribeEvent
  public void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();

    applyFeatures(event);
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

  private static void registerFeature(RegistryEvent.Register<Feature<?>> event, Feature<?> feature, String name) {
    feature.setRegistryName(TConstruct.modID, name);
    event.getRegistry().register(feature);
  }

  public static void applyFeatures(FMLCommonSetupEvent event) {
    for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
      addStructure(biome, GenerationStage.Decoration.SURFACE_STRUCTURES, SLIME_ISLAND);
    }

    ConfiguredFeature<?> SLIME_ISLAND_FEATURE = Biome.createDecoratedFeature(SLIME_ISLAND, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG);

    FlatGenerationSettings.FEATURE_STAGES.put(SLIME_ISLAND_FEATURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
    FlatGenerationSettings.STRUCTURES.put("tconstruct:slime_island", new ConfiguredFeature[] { SLIME_ISLAND_FEATURE });
    FlatGenerationSettings.FEATURE_CONFIGS.put(SLIME_ISLAND_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG);
  }

  private static void addStructure(Biome biome, GenerationStage.Decoration stage, Structure structure) {
    biome.addFeature(stage, Biome.createDecoratedFeature(structure, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
    biome.addStructure(structure, IFeatureConfig.NO_FEATURE_CONFIG);
  }

}
