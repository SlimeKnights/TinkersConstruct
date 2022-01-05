package slimeknights.tconstruct.world;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.mantle.registration.object.WoodBlockObject.WoodVariant;
import slimeknights.mantle.util.SupplierCreativeTab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.BloodSlimeBlock;
import slimeknights.tconstruct.world.block.CongealedSlimeBlock;
import slimeknights.tconstruct.world.block.PiglinHeadBlock;
import slimeknights.tconstruct.world.block.PiglinWallHeadBlock;
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
  public static final CreativeModeTab TAB_WORLD = new SupplierCreativeTab(TConstruct.MOD_ID, "world", () -> new ItemStack(TinkerWorld.cobaltOre));
	static final Logger log = Util.getLogger("tinker_world");

  public static final PlantType SLIME_PLANT_TYPE = PlantType.get("slime");

  /*
   * Block base properties
   */
  private static final Item.Properties WORLD_PROPS = new Item.Properties().tab(TAB_WORLD);
  private static final Function<Block, ? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, WORLD_PROPS);
  private static final Function<Block, ? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, WORLD_PROPS);
  private static final Item.Properties HEAD_PROPS = new Item.Properties().tab(TAB_WORLD).rarity(Rarity.UNCOMMON);

  /*
   * Blocks
   */
  // ores
  public static final ItemObject<Block> cobaltOre = BLOCKS.register("cobalt_ore", () -> new Block(builder(Material.STONE, MaterialColor.NETHER, SoundType.NETHER_ORE).requiresCorrectToolForDrops().strength(10.0F)), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<Block> rawCobaltBlock = BLOCKS.register("raw_cobalt_block", () -> new Block(builder(Material.STONE, MaterialColor.COLOR_BLUE, SoundType.NETHER_ORE).requiresCorrectToolForDrops().strength(6.0f, 7.0f)), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<Item> rawCobalt = ITEMS.register("raw_cobalt", WORLD_PROPS);

  // slime
  public static final EnumObject<SlimeType, SlimeBlock> slime = Util.make(() -> {
    Function<SlimeType,BlockBehaviour.Properties> slimeProps = type -> builder(Material.CLAY, type.getMapColor(), SoundType.SLIME_BLOCK).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion();
    return new EnumObject.Builder<SlimeType, SlimeBlock>(SlimeType.class)
      .putDelegate(SlimeType.EARTH, Blocks.SLIME_BLOCK.delegate)
      // sky slime: sticks to anything, but will not pull back
      .put(SlimeType.SKY,   BLOCKS.register("sky_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.SKY), (state, other) -> true), TOOLTIP_BLOCK_ITEM))
      // ichor: does not stick to self, but sticks to anything else
      .put(SlimeType.ICHOR, BLOCKS.register("ichor_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.ICHOR).lightLevel(s -> SlimeType.ICHOR.getLightLevel()),
                                                                                      (state, other) -> other.getBlock() != state.getBlock()), TOOLTIP_BLOCK_ITEM))
      // ender: only sticks to self
      .put(SlimeType.ENDER, BLOCKS.register("ender_slime", () -> new StickySlimeBlock(slimeProps.apply(SlimeType.ENDER), (state, other) -> other.getBlock() == state.getBlock()), TOOLTIP_BLOCK_ITEM))
      // blood slime: not sticky, and honey won't stick to it, good for bounce pads
      .put(SlimeType.BLOOD, BLOCKS.register("blood_slime", () -> new BloodSlimeBlock(slimeProps.apply(SlimeType.BLOOD)), TOOLTIP_BLOCK_ITEM))
      .build();
  });

  public static final EnumObject<SlimeType, CongealedSlimeBlock> congealedSlime = BLOCKS.registerEnum(SlimeType.values(), "congealed_slime", type -> new CongealedSlimeBlock(builder(Material.CLAY, type.getMapColor(), SoundType.SLIME_BLOCK).strength(0.5F).friction(0.5F).lightLevel(s -> type.getLightLevel())), TOOLTIP_BLOCK_ITEM);

  // island blocks
  public static final EnumObject<SlimeType, Block> slimeDirt = Util.make(() -> {
    Function<SlimeType,MaterialColor> color = type -> switch (type) {
      default -> MaterialColor.GRASS; // EARTH
      case SKY -> MaterialColor.WARPED_STEM;
      case ENDER -> MaterialColor.TERRACOTTA_LIGHT_BLUE;
      case ICHOR -> MaterialColor.TERRACOTTA_ORANGE;
    };
    return BLOCKS.registerEnum(SlimeType.TRUE_SLIME, "slime_dirt", (type) -> new SlimeDirtBlock(builder(Material.DIRT, color.apply(type), SoundType.SLIME_BLOCK).strength(0.55F)), TOOLTIP_BLOCK_ITEM);
  });
  public static final EnumObject<SlimeType, Block> allDirt = new EnumObject.Builder<SlimeType, Block>(SlimeType.class).put(SlimeType.BLOOD, Blocks.DIRT.delegate).putAll(slimeDirt).build();

  // grass variants
  public static final EnumObject<SlimeType, Block> vanillaSlimeGrass, earthSlimeGrass, skySlimeGrass, enderSlimeGrass, ichorSlimeGrass;
  /** Map of dirt type to slime grass type. Each slime grass is a map from foliage to grass type */
  public static final Map<SlimeType, EnumObject<SlimeType, Block>> slimeGrass = new EnumMap<>(SlimeType.class);

	static {
    Function<SlimeType,BlockBehaviour.Properties> slimeGrassProps = type -> builder(Material.GRASS, type.getMapColor(), SoundType.SLIME_BLOCK).strength(0.65F).randomTicks();
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

  /** Creates a wood variant properties function */
  private static Function<WoodVariant,BlockBehaviour.Properties> createSlimewood(MaterialColor planks, MaterialColor bark) {
    return type -> switch (type) {
      case WOOD -> BlockBehaviour.Properties.of(Material.NETHER_WOOD, bark).sound(SoundType.WOOD).requiresCorrectToolForDrops();
      case LOG -> BlockBehaviour.Properties.of(Material.NETHER_WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? planks : bark).sound(SoundType.WOOD).requiresCorrectToolForDrops();
      default -> BlockBehaviour.Properties.of(Material.NETHER_WOOD, planks).sound(SoundType.SLIME_BLOCK);
    };
  }

  // wood
  public static final WoodBlockObject greenheart  = BLOCKS.registerWood("greenheart",  createSlimewood(MaterialColor.COLOR_LIGHT_GREEN, MaterialColor.COLOR_GREEN),     false, TAB_WORLD);
  public static final WoodBlockObject skyroot     = BLOCKS.registerWood("skyroot",     createSlimewood(MaterialColor.COLOR_CYAN,        MaterialColor.TERRACOTTA_CYAN), false, TAB_WORLD);
  public static final WoodBlockObject bloodshroom = BLOCKS.registerWood("bloodshroom", createSlimewood(MaterialColor.COLOR_RED,         MaterialColor.COLOR_ORANGE),    false, TAB_WORLD);

  // plants
  public static final EnumObject<SlimeType, SlimeTallGrassBlock> slimeFern, slimeTallGrass;
  static {
    Function<SlimeType,BlockBehaviour.Properties> props = type -> {
      BlockBehaviour.Properties properties;
      if (type.isNether()) {
        properties = builder(Material.REPLACEABLE_FIREPROOF_PLANT, type.getMapColor(), SoundType.ROOTS);
      } else {
        properties = builder(Material.REPLACEABLE_PLANT, type.getMapColor(), SoundType.GRASS);
      }
      return properties.instabreak().noCollission();
    };
    slimeFern = BLOCKS.registerEnum(SlimeType.values(), "slime_fern", type -> new SlimeTallGrassBlock(props.apply(type), type), DEFAULT_BLOCK_ITEM);
    slimeTallGrass = BLOCKS.registerEnum(SlimeType.values(), "slime_tall_grass", type -> new SlimeTallGrassBlock(props.apply(type), type), DEFAULT_BLOCK_ITEM);
  }

  // trees
  public static final EnumObject<SlimeType, Block> slimeSapling = Util.make(() -> {
    Function<SlimeType,BlockBehaviour.Properties> props = type -> builder(Material.PLANT, type.getMapColor(), type.isNether() ? SoundType.FUNGUS : SoundType.GRASS).instabreak().noCollission();
    return new EnumObject.Builder<SlimeType,Block>(SlimeType.class)
      .putAll(BLOCKS.registerEnum(SlimeType.OVERWORLD, "slime_sapling", (type) -> new SlimeSaplingBlock(new SlimeTree(type), type, props.apply(type).randomTicks()), TOOLTIP_BLOCK_ITEM))
      .put(SlimeType.BLOOD, BLOCKS.register("blood_slime_sapling", () -> new SlimeFungusBlock(props.apply(SlimeType.BLOOD), () -> TinkerStructures.BLOOD_SLIME_FUNGUS), TOOLTIP_BLOCK_ITEM))
      .put(SlimeType.ICHOR, BLOCKS.register("ichor_slime_sapling", () -> new SlimeFungusBlock(props.apply(SlimeType.ICHOR), () -> TinkerStructures.ICHOR_SLIME_FUNGUS), HIDDEN_BLOCK_ITEM))
      .build();
  });
  public static final EnumObject<SlimeType, Block> slimeLeaves = BLOCKS.registerEnum(SlimeType.values(), "slime_leaves", type -> {
    if (type.isNether()) {
      return new SlimeWartBlock(builder(Material.GRASS, type.getMapColor(), SoundType.WART_BLOCK).strength(1.0F).isValidSpawn((s, w, p, e) -> false), type);
    }
    return new SlimeLeavesBlock(builder(Material.LEAVES, type.getMapColor(), SoundType.GRASS).strength(0.3f).randomTicks().noOcclusion().isValidSpawn((s, w, p, e) -> false), type);
  }, DEFAULT_BLOCK_ITEM);

  // slime vines
  public static final ItemObject<SlimeVineBlock> skySlimeVine, enderSlimeVine;
  static {
    Function<SlimeType,BlockBehaviour.Properties> props = type -> builder(Material.REPLACEABLE_PLANT, type.getMapColor(), SoundType.GRASS).strength(0.3F).noCollission().randomTicks();
    skySlimeVine = BLOCKS.register("sky_slime_vine", () -> new SlimeVineBlock(props.apply(SlimeType.SKY), SlimeType.SKY), DEFAULT_BLOCK_ITEM);
    enderSlimeVine = BLOCKS.register("ender_slime_vine", () -> new SlimeVineBlock(props.apply(SlimeType.ENDER), SlimeType.ENDER), DEFAULT_BLOCK_ITEM);
  }

  // heads
  public static final EnumObject<TinkerHeadType,SkullBlock>               heads     = BLOCKS.registerEnumNoItem(TinkerHeadType.values(), "head", TinkerWorld::makeHead);
  public static final EnumObject<TinkerHeadType,WallSkullBlock>           wallHeads = BLOCKS.registerEnumNoItem(TinkerHeadType.values(), "wall_head", TinkerWorld::makeWallHead);
  public static final EnumObject<TinkerHeadType,StandingAndWallBlockItem> headItems = ITEMS.registerEnum(TinkerHeadType.values(), "head", type -> new StandingAndWallBlockItem(heads.get(type), wallHeads.get(type), HEAD_PROPS));

  /*
   * Entities
   */
  // our own copy of the slime to make spawning a bit easier
  public static final RegistryObject<EntityType<EarthSlimeEntity>> earthSlimeEntity = ENTITIES.register("earth_slime", () ->
    EntityType.Builder.of(EarthSlimeEntity::new, MobCategory.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(10)
                      .sized(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.earthSlimeEntity.get().create(world)));
  public static final RegistryObject<EntityType<SkySlimeEntity>> skySlimeEntity = ENTITIES.registerWithEgg("sky_slime", () ->
    EntityType.Builder.of(SkySlimeEntity::new, MobCategory.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(20)
                      .sized(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.skySlimeEntity.get().create(world)), 0x47eff5, 0xacfff4);
  public static final RegistryObject<EntityType<EnderSlimeEntity>> enderSlimeEntity = ENTITIES.registerWithEgg("ender_slime", () ->
    EntityType.Builder.of(EnderSlimeEntity::new, MobCategory.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(32)
                      .sized(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.enderSlimeEntity.get().create(world)), 0x6300B0, 0xD37CFF);
  public static final RegistryObject<EntityType<TerracubeEntity>> terracubeEntity = ENTITIES.registerWithEgg("terracube", () ->
    EntityType.Builder.of(TerracubeEntity::new, MobCategory.MONSTER)
                      .setShouldReceiveVelocityUpdates(true)
                      .setTrackingRange(8)
                      .sized(2.04F, 2.04F)
                      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.terracubeEntity.get().create(world)), 0xAFB9D6, 0xA1A7B1);

  /*
   * Particles
   */
  public static final RegistryObject<SimpleParticleType> skySlimeParticle = PARTICLE_TYPES.register("sky_slime", () -> new SimpleParticleType(false));
  public static final RegistryObject<SimpleParticleType> enderSlimeParticle = PARTICLE_TYPES.register("ender_slime", () -> new SimpleParticleType(false));
  public static final RegistryObject<SimpleParticleType> terracubeParticle = PARTICLE_TYPES.register("terracube", () -> new SimpleParticleType(false));

  /*
   * Features
   */
  public static PlacedFeature COBALT_ORE_FEATURE_SMALL;
  public static PlacedFeature COBALT_ORE_FEATURE_LARGE;

  /*
   * Events
   */

  @SubscribeEvent
  void entityAttributes(EntityAttributeCreationEvent event) {
    event.put(earthSlimeEntity.get(), Monster.createMonsterAttributes().build());
    event.put(skySlimeEntity.get(), Monster.createMonsterAttributes().build());
    event.put(enderSlimeEntity.get(), Monster.createMonsterAttributes().build());
    event.put(terracubeEntity.get(), Monster.createMonsterAttributes().build());
  }

  /** Sets all fire info for the given wood */
  private static void setWoodFireInfo(FireBlock fireBlock, WoodBlockObject wood) {
    // planks
    fireBlock.setFlammable(wood.get(), 5, 20);
    fireBlock.setFlammable(wood.getSlab(), 5, 20);
    fireBlock.setFlammable(wood.getStairs(), 5, 20);
    fireBlock.setFlammable(wood.getFence(), 5, 20);
    fireBlock.setFlammable(wood.getFenceGate(), 5, 20);
    // logs
    fireBlock.setFlammable(wood.getLog(), 5, 5);
    fireBlock.setFlammable(wood.getStrippedLog(), 5, 5);
    fireBlock.setFlammable(wood.getWood(), 5, 5);
    fireBlock.setFlammable(wood.getStrippedWood(), 5, 5);
  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    SpawnPlacements.register(earthSlimeEntity.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(SlimeType.EARTH));
    SpawnPlacements.register(skySlimeEntity.get(),   SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(SlimeType.SKY));
    SpawnPlacements.register(enderSlimeEntity.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new SlimePlacementPredicate<>(SlimeType.ENDER));
    SpawnPlacements.register(terracubeEntity.get(),  SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TerracubeEntity::canSpawnHere);

    // compostables
    event.enqueueWork(() -> {
      slimeLeaves.forEach((type, block) -> ComposterBlock.add(type.isNether() ? 0.85f : 0.35f, block));
      slimeSapling.forEach(block -> ComposterBlock.add(0.35f, block));
      slimeTallGrass.forEach(block -> ComposterBlock.add(0.35f, block));
      slimeFern.forEach(block -> ComposterBlock.add(0.65f, block));
      slimeGrassSeeds.forEach(block -> ComposterBlock.add(0.35F, block));
      ComposterBlock.add(0.5f, skySlimeVine);
      ComposterBlock.add(0.5f, enderSlimeVine);

      // head equipping
      DispenseItemBehavior dispenseArmor = new OptionalDispenseItemBehavior() {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
          this.setSuccess(ArmorItem.dispenseArmor(source, stack));
          return stack;
        }
      };
      TinkerWorld.heads.forEach(head -> DispenserBlock.registerBehavior(head, dispenseArmor));
      // heads in firework stars
      TinkerWorld.heads.forEach(head -> FireworkStarRecipe.SHAPE_BY_ITEM.put(head.asItem(), FireworkRocketItem.Shape.CREEPER));
      // inject heads into the tile entity type
      event.enqueueWork(() -> {
        ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
        builder.addAll(BlockEntityType.SKULL.validBlocks);
        TinkerWorld.heads.forEach(head -> builder.add(head));
        TinkerWorld.wallHeads.forEach(head -> builder.add(head));
        BlockEntityType.SKULL.validBlocks = builder.build();
      });
    });

    // flammability
    event.enqueueWork(() -> {
      FireBlock fireblock = (FireBlock)Blocks.FIRE;
      // plants
      BiConsumer<SlimeType, Block> plantFireInfo = (type, block) -> {
        if (type != SlimeType.BLOOD && type != SlimeType.ICHOR) {
          fireblock.setFlammable(block, 30, 60);
        }
      };
      slimeLeaves.forEach(plantFireInfo);
      slimeTallGrass.forEach(plantFireInfo);
      slimeFern.forEach(plantFireInfo);
      // vines
      fireblock.setFlammable(skySlimeVine.get(), 15, 100);
      fireblock.setFlammable(enderSlimeVine.get(), 15, 100);
    });

    // ores
    event.enqueueWork(() -> {
      // small veins, standard distribution
      ConfiguredFeature<?,?> cobaltOreSmall = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, resource("cobalt_ore_small"),
                                                   Feature.ORE.configured(new OreConfiguration(OreFeatures.NETHERRACK, cobaltOre.get().defaultBlockState(), 4)));
      COBALT_ORE_FEATURE_SMALL = Registry.register(BuiltinRegistries.PLACED_FEATURE, TConstruct.getResource("cobalt_ore_small"), cobaltOreSmall.placed(CountPlacement.of(5), PlacementUtils.RANGE_8_8, BiomeFilter.biome()));
      // large veins, around y=16, up to 48
      ConfiguredFeature<?,?> cobaltOreLarge = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, resource("cobalt_ore_large"),
																									 Feature.ORE.configured(new OreConfiguration(OreFeatures.NETHERRACK, cobaltOre.get().defaultBlockState(), 6)));
      COBALT_ORE_FEATURE_LARGE = Registry.register(BuiltinRegistries.PLACED_FEATURE, TConstruct.getResource("cobalt_ore_large"), cobaltOreLarge.placed(CountPlacement.of(3), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(32)), BiomeFilter.biome()));
    });
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new WorldRecipeProvider(datagenerator));
    }
  }


  /* helpers */

  /** Creates a skull block for the given head type */
  private static SkullBlock makeHead(TinkerHeadType type) {
    BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F);
    if (type == TinkerHeadType.PIGLIN || type == TinkerHeadType.PIGLIN_BRUTE || type == TinkerHeadType.ZOMBIFIED_PIGLIN) {
      return new PiglinHeadBlock(type, props);
    }
    return new SkullBlock(type, props);
  }

  /** Creates a skull wall block for the given head type */
  private static WallSkullBlock makeWallHead(TinkerHeadType type) {
    BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).lootFrom(() -> heads.get(type));
    if (type == TinkerHeadType.PIGLIN || type == TinkerHeadType.PIGLIN_BRUTE || type == TinkerHeadType.ZOMBIFIED_PIGLIN) {
      return new PiglinWallHeadBlock(type, props);
    }
    return new WallSkullBlock(type, props);
  }
}
