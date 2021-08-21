package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.data.DataGenerator;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.item.crafting.FireworkStarRecipe;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tileentity.TileEntityType;
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
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.BloodSlimeBlock;
import slimeknights.tconstruct.world.block.CongealedSlimeBlock;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeFungusBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeNyliumBlock;
import slimeknights.tconstruct.world.block.SlimeSaplingBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeWartBlock;
import slimeknights.tconstruct.world.block.StickySlimeBlock;
import slimeknights.tconstruct.world.data.WorldRecipeProvider;
import slimeknights.tconstruct.world.entity.EarthSlimeEntity;
import slimeknights.tconstruct.world.entity.EnderSlimeEntity;
import slimeknights.tconstruct.world.entity.SkySlimeEntity;
import slimeknights.tconstruct.world.entity.SlimePlacementPredicate;
import slimeknights.tconstruct.world.entity.TerracubeEntity;
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
  public static final ItemGroup TAB_WORLD = new SupplierItemGroup(TConstruct.MOD_ID, "world", () -> new ItemStack(TinkerWorld.cobaltOre));
	static final Logger log = Util.getLogger("tinker_world");

  public static final PlantType SLIME_PLANT_TYPE = PlantType.get("slime");

  /*
   * Block base properties
   */
  private static final Item.Properties WORLD_PROPS = new Item.Properties().group(TAB_WORLD);
  private static final Function<Block, ? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, WORLD_PROPS);
  private static final Function<Block, ? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, WORLD_PROPS);
  private static final Item.Properties HEAD_PROPS = new Item.Properties().group(TAB_WORLD).rarity(Rarity.UNCOMMON);

  /** Flamable variant of clay, as in flamable shoveling material */
  public static final Material SLIME_WOOD = new Material.Builder(MaterialColor.CLAY).flammable().build();

  /*
   * Blocks
   */
  // ores
  public static final ItemObject<Block> cobaltOre = BLOCKS.register("cobalt_ore", () -> new Block(builder(Material.ROCK, MaterialColor.NETHERRACK, ToolType.PICKAXE, SoundType.NETHER_ORE).setRequiresTool().harvestLevel(HarvestLevels.IRON).hardnessAndResistance(10.0F)), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<Block> copperOre = BLOCKS.register("copper_ore", builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).setRequiresTool().harvestLevel(HarvestLevels.STONE).hardnessAndResistance(3.0F, 3.0F), DEFAULT_BLOCK_ITEM);

  // slime
  public static final EnumObject<SlimeType, SlimeBlock> slime = Util.make(() -> {
    Function<SlimeType,AbstractBlock.Properties> slimeProps = type -> builder(Material.CLAY, type.getMapColor(), NO_TOOL, SoundType.SLIME).slipperiness(0.8F).sound(SoundType.SLIME).notSolid();
    return new EnumObject.Builder<SlimeType, SlimeBlock>(SlimeType.class)
      .putDelegate(SlimeType.EARTH, Blocks.SLIME_BLOCK.delegate)
      // sky slime: sticks to anything, but will not pull back
      .put(SlimeType.SKY,   BLOCKS.register("sky_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.SKY), (state, other) -> true), TOOLTIP_BLOCK_ITEM))
      // ichor: does not stick to self, but sticks to anything else
      .put(SlimeType.ICHOR, BLOCKS.register("ichor_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.ICHOR).setLightLevel(s -> SlimeType.ICHOR.getLightLevel()),
                                                                                      (state, other) -> other.getBlock() != state.getBlock()), TOOLTIP_BLOCK_ITEM))
      // ender: only sticks to self
      .put(SlimeType.ENDER, BLOCKS.register("ender_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.ENDER), (state, other) -> other.getBlock() == state.getBlock()), TOOLTIP_BLOCK_ITEM))
      // blood slime: not sticky, and honey won't stick to it, good for bounce pads
      .put(SlimeType.BLOOD, BLOCKS.register("blood_slime", () -> new BloodSlimeBlock(slimeProps.apply(SlimeType.BLOOD)), TOOLTIP_BLOCK_ITEM))
      .build();
  });

  public static final EnumObject<SlimeType, CongealedSlimeBlock> congealedSlime = BLOCKS.registerEnum(SlimeType.values(), "congealed_slime", type -> new CongealedSlimeBlock(builder(Material.CLAY, type.getMapColor(), ToolType.SHOVEL, SoundType.SLIME).hardnessAndResistance(0.5F).slipperiness(0.5F).setLightLevel(s -> type.getLightLevel())), TOOLTIP_BLOCK_ITEM);

  // island blocks
  public static final EnumObject<SlimeType, Block> slimeDirt = Util.make(() -> {
    Function<SlimeType,MaterialColor> color = type -> {
      switch(type) {
        case EARTH: default: return MaterialColor.GRASS;
        case SKY: return MaterialColor.WARPED_STEM;
        case ENDER: return MaterialColor.LIGHT_BLUE_TERRACOTTA;
        case ICHOR: return MaterialColor.ORANGE_TERRACOTTA;
      }
    };
    return BLOCKS.registerEnum(SlimeType.TRUE_SLIME, "slime_dirt", (type) -> new SlimeDirtBlock(builder(Material.EARTH, color.apply(type), ToolType.SHOVEL, SoundType.SLIME).hardnessAndResistance(0.55F)), TOOLTIP_BLOCK_ITEM);
  });
  public static final EnumObject<SlimeType, Block> allDirt = new EnumObject.Builder<SlimeType, Block>(SlimeType.class).put(SlimeType.BLOOD, Blocks.DIRT.delegate).putAll(slimeDirt).build();

  // grass variants
  public static final EnumObject<SlimeType, Block> vanillaSlimeGrass, earthSlimeGrass, skySlimeGrass, enderSlimeGrass, ichorSlimeGrass;
  /** Map of dirt type to slime grass type. Each slime grass is a map from foliage to grass type */
  public static final Map<SlimeType, EnumObject<SlimeType, Block>> slimeGrass = new EnumMap<>(SlimeType.class);

	static {
    Function<SlimeType,AbstractBlock.Properties> slimeGrassProps = type -> builder(Material.ORGANIC, type.getMapColor(), ToolType.SHOVEL, SoundType.SLIME).hardnessAndResistance(0.65F).tickRandomly();
    Function<SlimeType, Block> slimeGrassRegister = type -> type.isNether() ? new SlimeNyliumBlock(slimeGrassProps.apply(type), type) : new SlimeGrassBlock(slimeGrassProps.apply(type), type);
    // blood is not an exact match for vanilla, but close enough
    vanillaSlimeGrass = BLOCKS.registerEnum(SlimeType.values(), "vanilla_slime_grass", slimeGrassRegister, TOOLTIP_BLOCK_ITEM);
    earthSlimeGrass   = BLOCKS.registerEnum(SlimeType.values(), "earth_slime_grass",   slimeGrassRegister, TOOLTIP_BLOCK_ITEM);
    skySlimeGrass     = BLOCKS.registerEnum(SlimeType.values(), "sky_slime_grass",     slimeGrassRegister, TOOLTIP_BLOCK_ITEM);
    enderSlimeGrass   = BLOCKS.registerEnum(SlimeType.values(), "ender_slime_grass",   slimeGrassRegister, TOOLTIP_BLOCK_ITEM);
    ichorSlimeGrass   = BLOCKS.registerEnum(SlimeType.values(), "ichor_slime_grass",   slimeGrassRegister, TOOLTIP_BLOCK_ITEM);
    slimeGrass.put(SlimeType.BLOOD, vanillaSlimeGrass); // not an exact fit, but good enough
    slimeGrass.put(SlimeType.EARTH, earthSlimeGrass);
    slimeGrass.put(SlimeType.SKY,   skySlimeGrass);
    slimeGrass.put(SlimeType.ENDER, enderSlimeGrass);
    slimeGrass.put(SlimeType.ICHOR, ichorSlimeGrass);
  }
  public static final EnumObject<SlimeType, SlimeGrassSeedItem> slimeGrassSeeds = ITEMS.registerEnum(SlimeType.values(), "slime_grass_seeds", type -> new SlimeGrassSeedItem(WORLD_PROPS, type));

  // wood
  public static final WoodBlockObject greenheart  = BLOCKS.registerWood("greenheart",  SLIME_WOOD,    MaterialColor.LIME, SoundType.SLIME, ToolType.SHOVEL, Material.WOOD,        MaterialColor.GREEN,           SoundType.WOOD,   TAB_WORLD);
  public static final WoodBlockObject skyroot     = BLOCKS.registerWood("skyroot",     SLIME_WOOD,    MaterialColor.CYAN, SoundType.SLIME, ToolType.SHOVEL, Material.WOOD,        MaterialColor.CYAN_TERRACOTTA, SoundType.WOOD,   TAB_WORLD);
  public static final WoodBlockObject bloodshroom = BLOCKS.registerWood("bloodshroom", Material.CLAY, MaterialColor.RED,  SoundType.SLIME, ToolType.SHOVEL, Material.NETHER_WOOD, MaterialColor.ADOBE,           SoundType.HYPHAE, TAB_WORLD);

  // plants
  public static final EnumObject<SlimeType, SlimeTallGrassBlock> slimeFern, slimeTallGrass;
  static {
    Function<SlimeType,AbstractBlock.Properties> props = type -> {
      AbstractBlock.Properties properties;
      if (type.isNether()) {
        properties = builder(Material.NETHER_PLANTS, type.getMapColor(), NO_TOOL, SoundType.ROOT);
      } else {
        properties = builder(Material.TALL_PLANTS, type.getMapColor(), NO_TOOL, SoundType.PLANT);
      }
      return properties.zeroHardnessAndResistance().doesNotBlockMovement();
    };
    slimeFern = BLOCKS.registerEnum(SlimeType.values(), "slime_fern", type -> new SlimeTallGrassBlock(props.apply(type), type), DEFAULT_BLOCK_ITEM);
    slimeTallGrass = BLOCKS.registerEnum(SlimeType.values(), "slime_tall_grass", type -> new SlimeTallGrassBlock(props.apply(type), type), DEFAULT_BLOCK_ITEM);
  }

  // trees
  public static final EnumObject<SlimeType, Block> slimeSapling = Util.make(() -> {
    Function<SlimeType,AbstractBlock.Properties> props = type -> builder(Material.PLANTS, type.getMapColor(), NO_TOOL, type.isNether() ? SoundType.FUNGUS : SoundType.PLANT).zeroHardnessAndResistance().doesNotBlockMovement();
    return new EnumObject.Builder<SlimeType,Block>(SlimeType.class)
      .putAll(BLOCKS.registerEnum(SlimeType.OVERWORLD, "slime_sapling", (type) -> new SlimeSaplingBlock(new SlimeTree(type), type, props.apply(type).tickRandomly()), TOOLTIP_BLOCK_ITEM))
      .put(SlimeType.BLOOD, BLOCKS.register("blood_slime_sapling", () -> new SlimeFungusBlock(props.apply(SlimeType.BLOOD), () -> TinkerStructures.BLOOD_SLIME_FUNGUS), TOOLTIP_BLOCK_ITEM))
      .put(SlimeType.ICHOR, BLOCKS.register("ichor_slime_sapling", () -> new SlimeFungusBlock(props.apply(SlimeType.ICHOR), () -> TinkerStructures.ICHOR_SLIME_FUNGUS), HIDDEN_BLOCK_ITEM))
      .build();
  });
  public static final EnumObject<SlimeType, Block> slimeLeaves = BLOCKS.registerEnum(SlimeType.values(), "slime_leaves", type -> {
    if (type.isNether()) {
      return new SlimeWartBlock(builder(Material.ORGANIC, type.getMapColor(), NO_TOOL, SoundType.WART).hardnessAndResistance(1.0F).setAllowsSpawn((s, w, p, e) -> false), type);
    }
    return new SlimeLeavesBlock(builder(Material.LEAVES, type.getMapColor(), NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3f).tickRandomly().notSolid().setAllowsSpawn((s, w, p, e) -> false), type);
  }, DEFAULT_BLOCK_ITEM);

  // slime vines
  public static final ItemObject<SlimeVineBlock> skySlimeVine, enderSlimeVine;
  static {
    Function<SlimeType,AbstractBlock.Properties> props = type -> builder(Material.TALL_PLANTS, type.getMapColor(), NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3F).doesNotBlockMovement().tickRandomly();
    skySlimeVine = BLOCKS.register("sky_slime_vine", () -> new SlimeVineBlock(props.apply(SlimeType.SKY), SlimeType.SKY), DEFAULT_BLOCK_ITEM);
    enderSlimeVine = BLOCKS.register("ender_slime_vine", () -> new SlimeVineBlock(props.apply(SlimeType.ENDER), SlimeType.ENDER), DEFAULT_BLOCK_ITEM);
  }

  // heads
  public static final EnumObject<TinkerHeadType,SkullBlock>     heads     = BLOCKS.registerEnumNoItem(TinkerHeadType.values(), "head", type -> new SkullBlock(type, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F)));
  public static final EnumObject<TinkerHeadType,WallSkullBlock> wallHeads = BLOCKS.registerEnumNoItem(TinkerHeadType.values(), "wall_head", type -> new WallSkullBlock(type, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F).lootFrom(() -> heads.get(type))));
  public static final EnumObject<TinkerHeadType,WallOrFloorItem> headItems = ITEMS.registerEnum(TinkerHeadType.values(), "head", type -> new WallOrFloorItem(heads.get(type), wallHeads.get(type), HEAD_PROPS));

  /*
   * Entities
   */
  // our own copy of the slime to make spawning a bit easier
  public static final RegistryObject<EntityType<EarthSlimeEntity>> earthSlimeEntity = ENTITIES.register("earth_slime", () ->
    EntityType.Builder.create(EarthSlimeEntity::new, EntityClassification.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(10)
                      .size(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.earthSlimeEntity.get().create(world)));
  public static final RegistryObject<EntityType<SkySlimeEntity>> skySlimeEntity = ENTITIES.registerWithEgg("sky_slime", () ->
    EntityType.Builder.create(SkySlimeEntity::new, EntityClassification.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(20)
                      .size(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.skySlimeEntity.get().create(world)), 0x47eff5, 0xacfff4);
  public static final RegistryObject<EntityType<EnderSlimeEntity>> enderSlimeEntity = ENTITIES.registerWithEgg("ender_slime", () ->
    EntityType.Builder.create(EnderSlimeEntity::new, EntityClassification.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(32)
                      .size(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.enderSlimeEntity.get().create(world)), 0x6300B0, 0xD37CFF);
  public static final RegistryObject<EntityType<TerracubeEntity>> terracubeEntity = ENTITIES.registerWithEgg("terracube", () ->
    EntityType.Builder.create(TerracubeEntity::new, EntityClassification.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(8)
                      .size(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.terracubeEntity.get().create(world)), 0xAFB9D6, 0xA1A7B1);

  /*
   * Particles
   */
  public static final RegistryObject<BasicParticleType> skySlimeParticle = PARTICLE_TYPES.register("sky_slime", () -> new BasicParticleType(false));
  public static final RegistryObject<BasicParticleType> enderSlimeParticle = PARTICLE_TYPES.register("ender_slime", () -> new BasicParticleType(false));
  public static final RegistryObject<BasicParticleType> terracubeParticle = PARTICLE_TYPES.register("terracube", () -> new BasicParticleType(false));

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
    event.put(terracubeEntity.get(), MonsterEntity.func_234295_eP_().create());
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
    EntitySpawnPlacementRegistry.register(earthSlimeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(SlimeType.EARTH));
    EntitySpawnPlacementRegistry.register(skySlimeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(SlimeType.SKY));
    EntitySpawnPlacementRegistry.register(enderSlimeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(SlimeType.ENDER));
    EntitySpawnPlacementRegistry.register(terracubeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TerracubeEntity::canSpawnHere);

    // compostables
    event.enqueueWork(() -> {
      slimeLeaves.forEach((type, block) -> ComposterBlock.registerCompostable(type.isNether() ? 0.85f : 0.35f, block));
      slimeSapling.forEach(block -> ComposterBlock.registerCompostable(0.35f, block));
      slimeTallGrass.forEach(block -> ComposterBlock.registerCompostable(0.35f, block));
      slimeFern.forEach(block -> ComposterBlock.registerCompostable(0.65f, block));
      slimeGrassSeeds.forEach(block -> ComposterBlock.registerCompostable(0.35F, block));
      ComposterBlock.registerCompostable(0.5f, skySlimeVine);
      ComposterBlock.registerCompostable(0.5f, enderSlimeVine);

      // head equipping
      IDispenseItemBehavior dispenseArmor = new OptionalDispenseBehavior() {
        @Override
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
          this.setSuccessful(ArmorItem.func_226626_a_(source, stack));
          return stack;
        }
      };
      TinkerWorld.heads.forEach(head -> DispenserBlock.registerDispenseBehavior(head, dispenseArmor));
      // heads in firework stars
      TinkerWorld.heads.forEach(head -> FireworkStarRecipe.ITEM_SHAPE_MAP.put(head.asItem(), FireworkRocketItem.Shape.CREEPER));
      // inject heads into the tile entity type
      event.enqueueWork(() -> {
        ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
        builder.addAll(TileEntityType.SKULL.validBlocks);
        //noinspection Convert2MethodRef
        TinkerWorld.heads.forEach(head -> builder.add(head));
        //noinspection Convert2MethodRef
        TinkerWorld.wallHeads.forEach(head -> builder.add(head));
        TileEntityType.SKULL.validBlocks = builder.build();
      });
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
      COPPER_ORE_FEATURE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, resource("copper_ore"),
																						 Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.BASE_STONE_OVERWORLD, TinkerWorld.copperOre.get().getDefaultState(), 9))
                                                        .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(40, 0, 60)))
                                                        .square()
                                                        .count(Config.COMMON.veinCountCopper.get()));
      // small veins, standard distribution
      COBALT_ORE_FEATURE_SMALL = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, resource("cobalt_ore_small"),
																									 Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.NETHERRACK, cobaltOre.get().getDefaultState(), 4))
                                                              .withPlacement(Features.Placements.NETHER_SPRING_ORE_PLACEMENT)
                                                              .square().count(Config.COMMON.veinCountCobalt.get() / 2));
      // large veins, around y=16, up to 48
      COBALT_ORE_FEATURE_LARGE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, resource("cobalt_ore_large"),
																									 Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.NETHERRACK, cobaltOre.get().getDefaultState(), 8))
                                                              .withPlacement(Placement.DEPTH_AVERAGE.configure(new DepthAverageConfig(32, 16)))
                                                              .square().count(Config.COMMON.veinCountCobalt.get() / 2));
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
