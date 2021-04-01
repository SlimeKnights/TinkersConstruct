package slimeknights.tconstruct.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.gadgets.block.DropperRailBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.data.GadgetRecipeProvider;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.FlintShurikenEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.QuartzShurikenEntity;
import slimeknights.tconstruct.gadgets.item.EflnBallItem;
import slimeknights.tconstruct.gadgets.item.FancyItemFrameItem;
import slimeknights.tconstruct.gadgets.item.GlowBallItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem.CarryPotionEffect;
import slimeknights.tconstruct.gadgets.item.ShurikenItem;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.slimesling.BaseSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.EarthSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.EnderSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.IchorSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.SkySlimeSlingItem;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.piggyback.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.function.Function;

/**
 * Contains any special tools unrelated to the base tools
 */
@SuppressWarnings("unused")
public final class TinkerGadgets extends TinkerModule {
  /** Tab for all special tools added by the mod */
  public static final ItemGroup TAB_GADGETS = new SupplierItemGroup(TConstruct.modID, "gadgets", () -> new ItemStack(TinkerGadgets.slimeSling.get(SlimeType.EARTH)));
  static final Logger log = Util.getLogger("tinker_gadgets");

  /*
   * Block base properties
   */
  private static final Item.Properties GADGET_PROPS = new Item.Properties().group(TAB_GADGETS);
  private static final Item.Properties UNSTACKABLE_PROPS = new Item.Properties().group(TAB_GADGETS).maxStackSize(1);
  private static final Function<Block,? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, GADGET_PROPS);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GADGET_PROPS);

  /*
   * Blocks
   */
  // TODO: moving to natura
  public static final ItemObject<LadderBlock> stoneLadder = BLOCKS.register("stone_ladder", () -> new LadderBlock(builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.STONE).hardnessAndResistance(0.1F).notSolid()) {}, HIDDEN_BLOCK_ITEM);
  // TODO: moving to natura
  public static final ItemObject<PunjiBlock> punji = BLOCKS.register("punji", () -> new PunjiBlock(builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(3.0F).notSolid()), HIDDEN_BLOCK_ITEM);
  // torch
  // TODO: moving to natura
  private static final Block.Properties STONE_TORCH = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.STONE).doesNotBlockMovement().hardnessAndResistance(0.0F).setLightLevel(s -> 14);
  public static final RegistryObject<WallTorchBlock> wallStoneTorch = BLOCKS.registerNoItem("wall_stone_torch", () -> new WallTorchBlock(STONE_TORCH, ParticleTypes.FLAME) {});
  public static final ItemObject<TorchBlock> stoneTorch = BLOCKS.register("stone_torch",
                                                                               () -> new TorchBlock(STONE_TORCH, ParticleTypes.FLAME) {},
                                                                               (block) -> new WallOrFloorItem(block, wallStoneTorch.get(), HIDDEN_PROPS));
  // rails
  // TODO: moving to tinkers' mechworks
  private static final Block.Properties WOODEN_RAIL = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.WOOD).doesNotBlockMovement().hardnessAndResistance(0.2F);
  public static final ItemObject<DropperRailBlock> woodenDropperRail = BLOCKS.register("wooden_dropper_rail", () -> new DropperRailBlock(WOODEN_RAIL), HIDDEN_BLOCK_ITEM);
  public static final ItemObject<RailBlock> woodenRail = BLOCKS.register("wooden_rail", () -> new RailBlock(WOODEN_RAIL) {}, HIDDEN_BLOCK_ITEM);

  /*
   * Items
   */
  // TODO: moving to natura
  public static final ItemObject<Item> stoneStick = ITEMS.register("stone_stick", HIDDEN_PROPS);
  public static final ItemObject<PiggyBackPackItem> piggyBackpack = ITEMS.register("piggy_backpack", PiggyBackPackItem::new);
  public static final EnumObject<FrameType,FancyItemFrameItem> itemFrame = ITEMS.registerEnum(FrameType.values(), "item_frame", (type) -> new FancyItemFrameItem(((world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, type.getId()))));
  // slime tools
  private static final Item.Properties SLING_PROPS = new Item.Properties().group(TAB_GADGETS).maxStackSize(1).maxDamage(250);
  public static final EnumObject<SlimeType, BaseSlimeSlingItem> slimeSling = new EnumObject.Builder<SlimeType, BaseSlimeSlingItem>(SlimeType.class)
    .put(SlimeType.EARTH, ITEMS.register("earth_slime_sling", () -> new EarthSlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.SKY, ITEMS.register("sky_slime_sling", () -> new SkySlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.ICHOR, ITEMS.register("ichor_slime_sling", () -> new IchorSlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.ENDER, ITEMS.register("ender_slime_sling", () -> new EnderSlimeSlingItem(SLING_PROPS)))
    .build();
  public static final EnumObject<SlimeType,SlimeBootsItem> slimeBoots = ITEMS.registerEnum(SlimeType.values(), "slime_boots", (type) -> new SlimeBootsItem(type, UNSTACKABLE_PROPS));
  // throwballs
  public static final ItemObject<GlowBallItem> glowBall = ITEMS.register("glow_ball", GlowBallItem::new);
  public static final ItemObject<EflnBallItem> efln = ITEMS.register("efln_ball", EflnBallItem::new);

  // foods
  public static final EnumObject<SlimeType,EdibleItem> slimeDrop = ITEMS.registerEnum(SlimeType.values(), "slime_drop", (type) -> new EdibleItem(type.getSlimeDropFood(type), TAB_GADGETS));
  // jerkies
  // TODO: moving to natura
  public static final ItemObject<EdibleItem> monsterJerky = ITEMS.register("monster_jerky", () -> new EdibleItem(TinkerFood.MONSTER_JERKY, null));
  public static final ItemObject<EdibleItem> beefJerky = ITEMS.register("beef_jerky", () -> new EdibleItem(TinkerFood.BEEF_JERKY, null));
  public static final ItemObject<EdibleItem> chickenJerky = ITEMS.register("chicken_jerky", () -> new EdibleItem(TinkerFood.CHICKEN_JERKY, null));
  public static final ItemObject<EdibleItem> porkJerky = ITEMS.register("pork_jerky", () -> new EdibleItem(TinkerFood.PORK_JERKY, null));
  public static final ItemObject<EdibleItem> muttonJerky = ITEMS.register("mutton_jerky", () -> new EdibleItem(TinkerFood.MUTTON_JERKY, null));
  public static final ItemObject<EdibleItem> rabbitJerky = ITEMS.register("rabbit_jerky", () -> new EdibleItem(TinkerFood.RABBIT_JERKY, null));
  public static final ItemObject<EdibleItem> fishJerky = ITEMS.register("fish_jerky", () -> new EdibleItem(TinkerFood.FISH_JERKY, null));
  public static final ItemObject<EdibleItem> salmonJerky = ITEMS.register("salmon_jerky", () -> new EdibleItem(TinkerFood.SALMON_JERKY, null));
  public static final ItemObject<EdibleItem> clownfishJerky = ITEMS.register("clownfish_jerky", () -> new EdibleItem(TinkerFood.CLOWNFISH_JERKY, null));
  public static final ItemObject<EdibleItem> pufferfishJerky = ITEMS.register("pufferfish_jerky", () -> new EdibleItem(TinkerFood.PUFFERFISH_JERKY, null));
  // Spicy Memes
  private static final Item.Properties SPAGET_PROPS = new Item.Properties().maxStackSize(1);
  public static final ItemObject<Item> hardSpaghetti = ITEMS.register("hard_spaghetti", SPAGET_PROPS);
  public static final ItemObject<Item> soggySpaghetti = ITEMS.register("soggy_spaghetti", SPAGET_PROPS);
  public static final ItemObject<Item> coldSpaghetti = ITEMS.register("cold_spaghetti", SPAGET_PROPS);

  // Shurikens
  private static final Item.Properties THROWABLE_PROPS = new Item.Properties().maxStackSize(16).group(TAB_GADGETS);
  public static final ItemObject<ShurikenItem> quartzShuriken = ITEMS.register("quartz_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, QuartzShurikenEntity::new));
  public static final ItemObject<ShurikenItem> flintShuriken = ITEMS.register("flint_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, FlintShurikenEntity::new));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<FancyItemFrameEntity>> itemFrameEntity = ENTITIES.register("fancy_item_frame", () -> {
    return EntityType.Builder.<FancyItemFrameEntity>create(
      FancyItemFrameEntity::new, EntityClassification.MISC)
      .size(0.5F, 0.5F)
      .setTrackingRange(160)
      .setUpdateInterval(Integer.MAX_VALUE)
      .setCustomClientFactory((spawnEntity, world) -> new FancyItemFrameEntity(TinkerGadgets.itemFrameEntity.get(), world))
      .setShouldReceiveVelocityUpdates(false);
  });
  public static final RegistryObject<EntityType<GlowballEntity>> glowBallEntity = ENTITIES.register("glow_ball", () -> {
    return EntityType.Builder.<GlowballEntity>create(GlowballEntity::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .setTrackingRange(64)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new GlowballEntity(TinkerGadgets.glowBallEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true);
  });
  public static final RegistryObject<EntityType<EflnBallEntity>> eflnEntity = ENTITIES.register("efln_ball", () -> {
    return EntityType.Builder.<EflnBallEntity>create(EflnBallEntity::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .setTrackingRange(64)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new EflnBallEntity(TinkerGadgets.eflnEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true);
  });
  public static final RegistryObject<EntityType<QuartzShurikenEntity>> quartzShurikenEntity = ENTITIES.register("quartz_shuriken", () -> {
    return EntityType.Builder.<QuartzShurikenEntity>create(QuartzShurikenEntity::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .setTrackingRange(64)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new QuartzShurikenEntity(TinkerGadgets.quartzShurikenEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true);
  });
  public static final RegistryObject<EntityType<FlintShurikenEntity>> flintShurikenEntity = ENTITIES.register("flint_shuriken", () -> {
    return EntityType.Builder.<FlintShurikenEntity>create(FlintShurikenEntity::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .setTrackingRange(64)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new FlintShurikenEntity(TinkerGadgets.flintShurikenEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true);
  });

  /**
   * Potions
   */
  public static final RegistryObject<CarryPotionEffect> carryEffect = POTIONS.register("carry", CarryPotionEffect::new);

  /*
   * Events
   */
  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    CapabilityTinkerPiggyback.register();
    MinecraftForge.EVENT_BUS.register(new GadgetEvents());
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new GadgetRecipeProvider(datagenerator));
    }
  }
}
