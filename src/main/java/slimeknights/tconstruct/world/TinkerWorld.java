package slimeknights.tconstruct.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.trees.OakTree;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.library.utils.SupplierItemGroup;
import slimeknights.tconstruct.shared.block.CongealedSlimeBlock;
import slimeknights.tconstruct.shared.block.StickySlimeBlock;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeDirtBlock.SlimeDirtType;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeSaplingBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.data.WorldRecipeProvider;
import slimeknights.tconstruct.world.entity.BlueSlimeEntity;

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
  private static final Function<Block,? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, WORLD_PROPS);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, WORLD_PROPS);

  /*
   * Blocks
   */
  // ores
  private static final Block.Properties NETHER_ORE = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).harvestLevel(HarvestLevels.MANYULLYN).hardnessAndResistance(10.0F).notSolid();
  public static final ItemObject<Block> cobaltOre = BLOCKS.register("cobalt_ore", () -> new Block(NETHER_ORE), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<Block> arditeOre = BLOCKS.register("ardite_ore", () -> new Block(NETHER_ORE), DEFAULT_BLOCK_ITEM);

  private static final Block.Properties OVERWORLD_ORE = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).hardnessAndResistance(3.0F, 3.0F);
  public static final ItemObject<Block> copperOre = BLOCKS.register("copper_ore", OVERWORLD_ORE, DEFAULT_BLOCK_ITEM);

  // slime
  private static final Block.Properties SLIME = Block.Properties.from(Blocks.SLIME_BLOCK);
  public static final EnumObject<SlimeType,SlimeBlock> slime = new EnumObject.Builder<SlimeType,SlimeBlock>(SlimeType.class)
    .putDelegate(SlimeType.GREEN, Blocks.SLIME_BLOCK.delegate)
    .putAll(BLOCKS.registerEnum(StickySlimeBlock.SlimeType.TINKER, "slime", (type) -> new StickySlimeBlock(SLIME), TOOLTIP_BLOCK_ITEM))
    .build();
  private static final Block.Properties CONGEALED_SLIME = builder(Material.CLAY, NO_TOOL, SoundType.SLIME).hardnessAndResistance(0.5F).slipperiness(0.5F);
  public static final EnumObject<SlimeType,CongealedSlimeBlock> congealedSlime = BLOCKS.registerEnum(SlimeType.values(), "congealed_slime", (type) -> new CongealedSlimeBlock(CONGEALED_SLIME), TOOLTIP_BLOCK_ITEM);

  // island blocks
  private static final Block.Properties SLIME_DIRT = builder(Material.ORGANIC, NO_TOOL, SoundType.SLIME).hardnessAndResistance(0.55F);
  public static final EnumObject<SlimeDirtType, SlimeDirtBlock> slimeDirt = BLOCKS.registerEnum(SlimeDirtBlock.SlimeDirtType.values(), "slime_dirt", (type) -> new SlimeDirtBlock(SLIME_DIRT), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<FoliageType, SlimeGrassBlock> vanillaSlimeGrass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "vanilla_slime_grass", (type) -> new SlimeGrassBlock(SLIME_DIRT, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<FoliageType, SlimeGrassBlock> greenSlimeGrass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "green_slime_grass", (type) -> new SlimeGrassBlock(SLIME_DIRT, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<FoliageType, SlimeGrassBlock> blueSlimeGrass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "blue_slime_grass", (type) -> new SlimeGrassBlock(SLIME_DIRT, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<FoliageType, SlimeGrassBlock> purpleSlimeGrass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "purple_slime_grass", (type) -> new SlimeGrassBlock(SLIME_DIRT, type), TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<FoliageType, SlimeGrassBlock> magmaSlimeGrass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "magma_slime_grass", (type) -> new SlimeGrassBlock(SLIME_DIRT, type), TOOLTIP_BLOCK_ITEM);

  // plants
  private static final Block.Properties GRASS = builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.1F).doesNotBlockMovement().tickRandomly();
  public static final EnumObject<FoliageType,SlimeTallGrassBlock> slimeFern = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_fern", (type) -> new SlimeTallGrassBlock(GRASS, type, SlimeTallGrassBlock.SlimePlantType.FERN), DEFAULT_BLOCK_ITEM);
  public static final EnumObject<FoliageType,SlimeTallGrassBlock> slimeTallGrass = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_tall_grass", (type) -> new SlimeTallGrassBlock(GRASS, type, SlimeTallGrassBlock.SlimePlantType.TALL_GRASS), DEFAULT_BLOCK_ITEM);

  // trees
  private static final Block.Properties SAPLING = builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.1F).doesNotBlockMovement().tickRandomly();
  // TODO: bring back slime tree
  public static final EnumObject<FoliageType,SlimeSaplingBlock> slimeSapling = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_sapling", (type) -> new SlimeSaplingBlock(/*new SlimeTree(type, false)*/new OakTree(), SAPLING), TOOLTIP_BLOCK_ITEM);
  private static final Block.Properties SLIME_LEAVES = builder(Material.LEAVES, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3F).tickRandomly().notSolid().setAllowsSpawn((s,w,p,e) -> false);
  public static final EnumObject<FoliageType,SlimeLeavesBlock> slimeLeaves = BLOCKS.registerEnum(SlimeGrassBlock.FoliageType.values(), "slime_leaves", (type) -> new SlimeLeavesBlock(SLIME_LEAVES, type), DEFAULT_BLOCK_ITEM);

  // slime vines
  private static final Block.Properties VINE = builder(Material.TALL_PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3F).doesNotBlockMovement().tickRandomly();
  public static final ItemObject<SlimeVineBlock> purpleSlimeVine = BLOCKS.register("purple_slime_vine", () -> new SlimeVineBlock(VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.START), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<SlimeVineBlock> purpleSlimeVineMiddle = BLOCKS.register("purple_slime_vine_middle", () -> new SlimeVineBlock(VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.MIDDLE), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<SlimeVineBlock> purpleSlimeVineEnd = BLOCKS.register("purple_slime_vine_end", () -> new SlimeVineBlock(VINE, SlimeGrassBlock.FoliageType.PURPLE, SlimeVineBlock.VineStage.END), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<SlimeVineBlock> blueSlimeVine = BLOCKS.register("blue_slime_vine", () -> new SlimeVineBlock(VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.START), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<SlimeVineBlock> blueSlimeVineMiddle = BLOCKS.register("blue_slime_vine_middle", () -> new SlimeVineBlock(VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.MIDDLE), DEFAULT_BLOCK_ITEM);
  public static final ItemObject<SlimeVineBlock> blueSlimeVineEnd = BLOCKS.register("blue_slime_vine_end", () -> new SlimeVineBlock(VINE, SlimeGrassBlock.FoliageType.BLUE, SlimeVineBlock.VineStage.END), DEFAULT_BLOCK_ITEM);

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<BlueSlimeEntity>> blueSlimeEntity = ENTITIES.registerWithEgg("blue_slime", () -> {
    return EntityType.Builder.create(BlueSlimeEntity::new, EntityClassification.MONSTER)
      .setShouldReceiveVelocityUpdates(true)
      .setUpdateInterval(5)
      .setTrackingRange(64)
      .size(2.04F, 2.04F)
      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.blueSlimeEntity.get().create(world));
  }, 0x47eff5, 0xacfff4);

  /*
   * Particles
   */
  public static final RegistryObject<BasicParticleType> slimeParticle = PARTICLE_TYPES.register("slime",() -> new BasicParticleType(false));

  /*
   * Events
   */
  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    GlobalEntityTypeAttributes.put(blueSlimeEntity.get(), MonsterEntity.func_234295_eP_().create());
    EntitySpawnPlacementRegistry.register(blueSlimeEntity.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, BlueSlimeEntity::canSpawnHere);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new WorldRecipeProvider(datagenerator));
    }
  }
}
