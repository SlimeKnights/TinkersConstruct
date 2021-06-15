package slimeknights.tconstruct.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.registration.WoodBlockObject;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.BloodSlimeBlock;
import slimeknights.tconstruct.world.block.CongealedSlimeBlock;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeSaplingBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.StickySlimeBlock;
import slimeknights.tconstruct.world.data.WorldRecipeProvider;
import slimeknights.tconstruct.world.entity.EnderSlimeEntity;
import slimeknights.tconstruct.world.entity.SkySlimeEntity;
import slimeknights.tconstruct.world.item.SlimeGrassSeedItem;
import slimeknights.tconstruct.world.worldgen.trees.SlimeTree;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Contains blocks and items relevant to structures and world gen
 */
@SuppressWarnings("unused")
public final class TinkerWorld extends TinkerModule {

  /** Tab for anything generated in the world */
  @SuppressWarnings("WeakerAccess")
  public static final ItemGroup TAB_WORLD = new SupplierItemGroup(TConstruct.modID, "world", () -> new ItemStack(TinkerWorld.cobaltOre));
  static final Logger log = Util.getLogger("tinker_world");

  public static final PlantType SLIME_PLANT_TYPE = PlantType.get("slime");

  /*
   * Block base properties
   */
  private static final Item.Properties WORLD_PROPS = new Item.Properties().group(TAB_WORLD);
  private static final Function<Block, ? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, WORLD_PROPS);
  private static final Function<Block, ? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, WORLD_PROPS);

  /*
   * Blocks
   */
  // ores
  private static final Block.Properties NETHER_ORE = builder(Material.ROCK, ToolType.PICKAXE, SoundType.NETHER_ORE).setRequiresTool().harvestLevel(HarvestLevels.DIAMOND).hardnessAndResistance(10.0F);
  public static final ItemObject<Block> cobaltOre = BLOCKS.register("cobalt_ore", () -> new Block(NETHER_ORE), DEFAULT_BLOCK_ITEM);

  private static final Block.Properties OVERWORLD_ORE = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).setRequiresTool().harvestLevel(HarvestLevels.STONE).hardnessAndResistance(3.0F, 3.0F);
  public static final ItemObject<Block> copperOre = BLOCKS.register("copper_ore", OVERWORLD_ORE, DEFAULT_BLOCK_ITEM);

  // slime
  private static final Block.Properties SLIME = Block.Properties.from(Blocks.SLIME_BLOCK);
  public static final EnumObject<SlimeType, SlimeBlock> slime = new EnumObject.Builder<SlimeType, SlimeBlock>(SlimeType.class)
    .putDelegate(SlimeType.EARTH, Blocks.SLIME_BLOCK.delegate)
    // sky slime: sticks to anything, but will not pull back
    .put(SlimeType.SKY,   BLOCKS.register("sky_slime", () -> new StickySlimeBlock(SLIME, (state, other) -> true), TOOLTIP_BLOCK_ITEM))
    // ichor: does not stick to self, but sticks to anything else
    .put(SlimeType.ICHOR, BLOCKS.register("ichor_slime", () -> new StickySlimeBlock(SLIME, (state, other) -> other.getBlock() != state.getBlock()), TOOLTIP_BLOCK_ITEM))
    // ender: only sticks to self
    .put(SlimeType.ENDER, BLOCKS.register("ender_slime", () -> new StickySlimeBlock(SLIME, (state, other) -> other.getBlock() == state.getBlock()), TOOLTIP_BLOCK_ITEM))
    // blood slime: not sticky, and honey won't stick to it, good for bounce pads
    .put(SlimeType.BLOOD, BLOCKS.register("blood_slime", () -> new BloodSlimeBlock(SLIME), TOOLTIP_BLOCK_ITEM))
    .build();
  private static final Block.Properties CONGEALED_SLIME = builder(Material.CLAY, ToolType.SHOVEL, SoundType.SLIME).hardnessAndResistance(0.5F).slipperiness(0.5F);
  public static final EnumObject<SlimeType, CongealedSlimeBlock> congealedSlime = BLOCKS.registerEnum(SlimeType.values(), "congealed_slime", (type) -> new CongealedSlimeBlock(CONGEALED_SLIME), TOOLTIP_BLOCK_ITEM);

  // island blocks
  private static final Block.Properties SLIME_DIRT = builder(Material.EARTH, ToolType.SHOVEL, SoundType.SLIME).hardnessAndResistance(0.55F);
  private static final Block.Properties SLIME_GRASS = builder(Material.ORGANIC, ToolType.SHOVEL, SoundType.SLIME).hardnessAndResistance(0.65F).tickRandomly();
  public static final EnumObject<SlimeType, Block> slimeDirt = BLOCKS.registerEnum(SlimeType.TRUE_SLIME, "slime_dirt", (type) -> new SlimeDirtBlock(SLIME_DIRT), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<SlimeType, Block> allDirt = new EnumObject.Builder<SlimeType, Block>(SlimeType.class).put(SlimeType.BLOOD, Blocks.DIRT.delegate).putAll(slimeDirt).build();
  public static final EnumObject<SlimeType, SlimeGrassBlock> vanillaSlimeGrass = BLOCKS.registerEnum(SlimeType.values(), "vanilla_slime_grass", (type) -> new SlimeGrassBlock(SLIME_GRASS, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<SlimeType, SlimeGrassBlock> earthSlimeGrass = BLOCKS.registerEnum(SlimeType.values(), "earth_slime_grass", (type) -> new SlimeGrassBlock(SLIME_GRASS, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<SlimeType, SlimeGrassBlock> skySlimeGrass = BLOCKS.registerEnum(SlimeType.values(), "sky_slime_grass", (type) -> new SlimeGrassBlock(SLIME_GRASS, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<SlimeType, SlimeGrassBlock> enderSlimeGrass = BLOCKS.registerEnum(SlimeType.values(), "ender_slime_grass", (type) -> new SlimeGrassBlock(SLIME_GRASS, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<SlimeType, SlimeGrassBlock> ichorSlimeGrass = BLOCKS.registerEnum(SlimeType.values(), "ichor_slime_grass", (type) -> new SlimeGrassBlock(SLIME_GRASS, type), TOOLTIP_BLOCK_ITEM);
  /** Map of dirt type to slime grass type. Each slime grass is a map from foliage to grass type */
  public static final Map<SlimeType, EnumObject<SlimeType, SlimeGrassBlock>> slimeGrass;
  static {
    slimeGrass = new EnumMap<>(SlimeType.class);
    slimeGrass.put(SlimeType.BLOOD, vanillaSlimeGrass); // not exact match, but whatever
    slimeGrass.put(SlimeType.EARTH, earthSlimeGrass);
    slimeGrass.put(SlimeType.SKY, skySlimeGrass);
    slimeGrass.put(SlimeType.ENDER, enderSlimeGrass);
    slimeGrass.put(SlimeType.ICHOR, ichorSlimeGrass);
  }
  public static final EnumObject<SlimeType, SlimeGrassSeedItem> slimeGrassSeeds = ITEMS.registerEnum(SlimeType.values(), "slime_grass_seeds", type -> new SlimeGrassSeedItem(WORLD_PROPS, type));

  // plants
  private static final Block.Properties GRASS = builder(Material.TALL_PLANTS, NO_TOOL, SoundType.PLANT).zeroHardnessAndResistance().doesNotBlockMovement().tickRandomly();
  public static final EnumObject<SlimeType, SlimeTallGrassBlock> slimeFern = BLOCKS.registerEnum(SlimeType.values(), "slime_fern", (type) -> new SlimeTallGrassBlock(GRASS, type, SlimeTallGrassBlock.SlimePlantType.FERN), DEFAULT_BLOCK_ITEM);
  public static final EnumObject<SlimeType, SlimeTallGrassBlock> slimeTallGrass = BLOCKS.registerEnum(SlimeType.values(), "slime_tall_grass", (type) -> new SlimeTallGrassBlock(GRASS, type, SlimeTallGrassBlock.SlimePlantType.TALL_GRASS), DEFAULT_BLOCK_ITEM);

  // wood
  public static final Material SLIME_WOOD = new Material.Builder(MaterialColor.CLAY).flammable().build(); // yep. flammable clay. New material so none of the existing tooltips try mining this
  public static final WoodBlockObject greenheart =  BLOCKS.registerWood("greenheart",  SLIME_WOOD,    MaterialColor.LIME, SoundType.SLIME, ToolType.SHOVEL, Material.WOOD,        MaterialColor.GREEN,           SoundType.WOOD,   TAB_WORLD);
  public static final WoodBlockObject skyroot =     BLOCKS.registerWood("skyroot",     SLIME_WOOD,    MaterialColor.CYAN, SoundType.SLIME, ToolType.SHOVEL, Material.WOOD,        MaterialColor.CYAN_TERRACOTTA, SoundType.WOOD,   TAB_WORLD);
  public static final WoodBlockObject bloodshroom = BLOCKS.registerWood("bloodshroom", Material.CLAY, MaterialColor.RED,  SoundType.SLIME, ToolType.SHOVEL, Material.NETHER_WOOD, MaterialColor.ADOBE,           SoundType.HYPHAE, TAB_WORLD);

  // trees
  private static final Block.Properties SAPLING = builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).zeroHardnessAndResistance().doesNotBlockMovement().tickRandomly();
  public static final EnumObject<SlimeType, SlimeSaplingBlock> slimeSapling = BLOCKS.registerEnum(SlimeType.values(), "slime_sapling", (type) -> new SlimeSaplingBlock(new SlimeTree(type), type, SAPLING), TOOLTIP_BLOCK_ITEM);
  private static final Block.Properties SLIME_LEAVES = builder(Material.LEAVES, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3f).tickRandomly().notSolid().setAllowsSpawn((s, w, p, e) -> false);
  private static final Block.Properties SLIME_WART = builder(Material.ORGANIC, NO_TOOL, SoundType.WART).hardnessAndResistance(0.6f).tickRandomly().setAllowsSpawn((s, w, p, e) -> false);
  public static final EnumObject<SlimeType, SlimeLeavesBlock> slimeLeaves = BLOCKS.registerEnum(SlimeType.values(), "slime_leaves", type -> new SlimeLeavesBlock(type == SlimeType.BLOOD ? SLIME_WART : SLIME_LEAVES, type), DEFAULT_BLOCK_ITEM);

  // slime vines
  private static final Block.Properties VINE = builder(Material.TALL_PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3F).doesNotBlockMovement().tickRandomly();
  public static final ItemObject<SlimeVineBlock> enderSlimeVine = BLOCKS.register("ender_slime_vine", () -> new SlimeVineBlock(VINE, SlimeType.ENDER), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<SlimeVineBlock> skySlimeVine = BLOCKS.register("sky_slime_vine", () -> new SlimeVineBlock(VINE, SlimeType.SKY), DEFAULT_BLOCK_ITEM);

  /*
   * Entities
   */
  // our own copy of the slime to make spawning a bit easier
  public static final RegistryObject<EntityType<SlimeEntity>> earthSlimeEntity = ENTITIES.register("earth_slime", () ->
    EntityType.Builder.create(SlimeEntity::new, EntityClassification.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setUpdateInterval(5)
                      .setTrackingRange(64)
                      .size(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.earthSlimeEntity.get().create(world)));
  public static final RegistryObject<EntityType<SkySlimeEntity>> skySlimeEntity = ENTITIES.registerWithEgg("sky_slime", () ->
    EntityType.Builder.create(SkySlimeEntity::new, EntityClassification.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setUpdateInterval(5)
                      .setTrackingRange(64)
                      .size(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.skySlimeEntity.get().create(world)), 0x47eff5, 0xacfff4);
  public static final RegistryObject<EntityType<EnderSlimeEntity>> enderSlimeEntity = ENTITIES.registerWithEgg("ender_slime", () ->
    EntityType.Builder.create(EnderSlimeEntity::new, EntityClassification.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setUpdateInterval(5)
                      .setTrackingRange(64)
                      .size(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.enderSlimeEntity.get().create(world)), 0x6300B0, 0xD37CFF);

  /*
   * Particles
   */
  public static final RegistryObject<BasicParticleType> skySlimeParticle = PARTICLE_TYPES.register("sky_slime", () -> new BasicParticleType(false));
  public static final RegistryObject<BasicParticleType> enderSlimeParticle = PARTICLE_TYPES.register("ender_slime", () -> new BasicParticleType(false));

  /*
   * Features
   */
  public static ConfiguredFeature<?, ?> COPPER_ORE_FEATURE;
  public static ConfiguredFeature<?, ?> COBALT_ORE_FEATURE_SMALL;
  public static ConfiguredFeature<?, ?> COBALT_ORE_FEATURE_LARGE;

  /*
   * Events
   */

  @SubscribeEvent
  void entityAttributes(EntityAttributeCreationEvent event) {
    event.put(earthSlimeEntity.get(), MonsterEntity.func_234295_eP_().create());
    event.put(skySlimeEntity.get(), MonsterEntity.func_234295_eP_().create());
    event.put(enderSlimeEntity.get(), MonsterEntity.func_234295_eP_().create());
  }

  /** Sets all fire info for the given wood */
  private static void setWoodFireInfo(FireBlock fireBlock, WoodBlockObject wood) {
    // planks
    fireBlock.setFireInfo(wood.get(), 5, 20);
    fireBlock.setFireInfo(wood.getSlab(), 5, 20);
    fireBlock.setFireInfo(wood.getStairs(), 5, 20);
    fireBlock.setFireInfo(wood.getFence(), 5, 20);
    fireBlock.setFireInfo(wood.getFenceGate(), 5, 20);
    // logs
    fireBlock.setFireInfo(wood.getLog(), 5, 5);
    fireBlock.setFireInfo(wood.getStrippedLog(), 5, 5);
    fireBlock.setFireInfo(wood.getWood(), 5, 5);
    fireBlock.setFireInfo(wood.getStrippedWood(), 5, 5);
  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    EntitySpawnPlacementRegistry.register(earthSlimeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, SkySlimeEntity::canSpawnHere);
    EntitySpawnPlacementRegistry.register(skySlimeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, SkySlimeEntity::canSpawnHere);
    EntitySpawnPlacementRegistry.register(enderSlimeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, SkySlimeEntity::canSpawnHere);

    // compostables
    event.enqueueWork(() -> {
      slimeLeaves.forEach(block -> ComposterBlock.registerCompostable(0.35f, block));
      slimeSapling.forEach(block -> ComposterBlock.registerCompostable(0.35f, block));
      slimeTallGrass.forEach(block -> ComposterBlock.registerCompostable(0.35f, block));
      slimeFern.forEach(block -> ComposterBlock.registerCompostable(0.65f, block));
      slimeGrassSeeds.forEach(block -> ComposterBlock.registerCompostable(0.35F, block));
      ComposterBlock.registerCompostable(0.5f, skySlimeVine);
      ComposterBlock.registerCompostable(0.5f, enderSlimeVine);
    });

    // flammability
    event.enqueueWork(() -> {
      FireBlock fireblock = (FireBlock)Blocks.FIRE;
      // wood
      setWoodFireInfo(fireblock, greenheart);
      setWoodFireInfo(fireblock, skyroot);
      // plants
      BiConsumer<SlimeType, Block> plantFireInfo = (type, block) -> {
        if (type != SlimeType.BLOOD && type != SlimeType.ICHOR) {
          fireblock.setFireInfo(block, 30, 60);
        }
      };
      slimeLeaves.forEach(plantFireInfo);
      slimeTallGrass.forEach(plantFireInfo);
      slimeFern.forEach(plantFireInfo);
      // vines
      fireblock.setFireInfo(skySlimeVine.get(), 15, 100);
      fireblock.setFireInfo(enderSlimeVine.get(), 15, 100);
    });

    // ores
    event.enqueueWork(() -> {
      COPPER_ORE_FEATURE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("copper_ore"),
                                             Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.BASE_STONE_OVERWORLD, TinkerWorld.copperOre.get().getDefaultState(), 9))
                                                        .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(40, 0, 60)))
                                                        .square()
                                                        .func_242731_b(Config.COMMON.veinCountCopper.get()));
      // small veins, standard distribution
      COBALT_ORE_FEATURE_SMALL = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("cobalt_ore_small"),
                                                   Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.NETHERRACK, cobaltOre.get().getDefaultState(), 4))
                                                              .withPlacement(Features.Placements.NETHER_SPRING_ORE_PLACEMENT)
                                                              .square().func_242731_b(Config.COMMON.veinCountCobalt.get() / 2));
      // large veins, around y=16, up to 48
      COBALT_ORE_FEATURE_LARGE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, location("cobalt_ore_large"),
                                                   Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.NETHERRACK, cobaltOre.get().getDefaultState(), 8))
                                                              .withPlacement(Placement.DEPTH_AVERAGE.configure(new DepthAverageConfig(32, 16)))
                                                              .square().func_242731_b(Config.COMMON.veinCountCobalt.get() / 2));
    });
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new WorldRecipeProvider(datagenerator));
    }
  }
}
