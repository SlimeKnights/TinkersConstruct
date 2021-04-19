package slimeknights.tconstruct.gadgets;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.registration.object.ItemEnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.gadgets.block.DropperRailBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
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
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.shared.block.SlimeType;
import java.util.function.Function;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.Material;
import net.minecraft.block.RailBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Contains any special tools unrelated to the base tools
 */
@SuppressWarnings("unused")
public final class TinkerGadgets extends TinkerModule {
  /** Tab for all special tools added by the mod */
  public static final ItemGroup TAB_GADGETS = FabricItemGroupBuilder.create(new Identifier(TConstruct.modID, "gadgets"))
    .icon(() -> new ItemStack(TinkerGadgets.slimeSling.get(SlimeType.EARTH)))
    .build();

  static final Logger log = Util.getLogger("tinker_gadgets");

  /*
   * Block base properties
   */
  private static final Item.Settings GADGET_PROPS = new Item.Settings().group(TAB_GADGETS);
  private static final Item.Settings UNSTACKABLE_PROPS = new Item.Settings().group(TAB_GADGETS).maxCount(1);
  private static final Function<Block, BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, GADGET_PROPS);
  private static final Function<Block, BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GADGET_PROPS);

  /*
   * Blocks
   */
  // TODO: moving to natura
  public static final ItemObject<Block> stoneLadder = BLOCKS.register("stone_ladder", () -> new LadderBlock(builder(Material.SUPPORTED, NO_TOOL, BlockSoundGroup.STONE).strength(0.1F).nonOpaque()) {}, HIDDEN_BLOCK_ITEM);
  // TODO: moving to natura
  public static final ItemObject<Block> punji = BLOCKS.register("punji", () -> new PunjiBlock(builder(Material.PLANT, NO_TOOL, BlockSoundGroup.GRASS).strength(3.0F).nonOpaque()), HIDDEN_BLOCK_ITEM);
  // torch
  // TODO: moving to natura
  private static final AbstractBlock.Settings STONE_TORCH = builder(Material.SUPPORTED, NO_TOOL, BlockSoundGroup.STONE).noCollision().strength(0.0F).luminance(s -> 14);
  public static final WallTorchBlock wallStoneTorch = (WallTorchBlock) BLOCKS.registerNoItem("wall_stone_torch", () -> new WallTorchBlock(STONE_TORCH, ParticleTypes.FLAME) {});
  public static final ItemObject<Block> stoneTorch = BLOCKS.register("stone_torch",
                                                                               () -> new TorchBlock(STONE_TORCH, ParticleTypes.FLAME) {},
                                                                               (block) -> new WallStandingBlockItem(block, wallStoneTorch, getHiddenProps()));
  // rails
  // TODO: moving to tinkers' mechworks
  private static final AbstractBlock.Settings WOODEN_RAIL = builder(Material.SUPPORTED, NO_TOOL, BlockSoundGroup.WOOD).noCollision().strength(0.2F);
  public static final ItemObject<Block> woodenDropperRail = BLOCKS.register("wooden_dropper_rail", () -> new DropperRailBlock(WOODEN_RAIL), HIDDEN_BLOCK_ITEM);
  public static final ItemObject<Block> woodenRail = BLOCKS.register("wooden_rail", () -> new RailBlock(WOODEN_RAIL) {}, HIDDEN_BLOCK_ITEM);

  /*
   * Items
   */
  // TODO: moving to natura
  public static final ItemObject<Item> stoneStick = ITEMS.register("stone_stick", getHiddenProps());
  public static final ItemObject<PiggyBackPackItem> piggyBackpack = ITEMS.register("piggy_backpack", () -> new PiggyBackPackItem(new Item.Settings().group(TinkerGadgets.TAB_GADGETS).maxCount(16)));
  public static final ItemEnumObject<FrameType,FancyItemFrameItem> itemFrame = ITEMS.registerEnum(FrameType.values(), "item_frame", (type) -> new FancyItemFrameItem(((world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, type.getId()))));
  // slime tools
  private static final Item.Settings SLING_PROPS = new Item.Settings().group(TAB_GADGETS).maxCount(1).maxDamage(250);
  public static final ItemEnumObject<SlimeType, BaseSlimeSlingItem> slimeSling = new ItemEnumObject.Builder<SlimeType, BaseSlimeSlingItem>(SlimeType.class)
    .put(SlimeType.EARTH, ITEMS.register("earth_slime_sling", () -> new EarthSlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.SKY, ITEMS.register("sky_slime_sling", () -> new SkySlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.ICHOR, ITEMS.register("ichor_slime_sling", () -> new IchorSlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.ENDER, ITEMS.register("ender_slime_sling", () -> new EnderSlimeSlingItem(SLING_PROPS)))
    .build();
  public static final ItemEnumObject<SlimeType,SlimeBootsItem> slimeBoots = ITEMS.registerEnum(SlimeType.values(), "slime_boots", (type) -> new SlimeBootsItem(type, UNSTACKABLE_PROPS));
  // throwballs
  public static final ItemObject<GlowBallItem> glowBall = ITEMS.register("glow_ball", GlowBallItem::new);
  public static final ItemObject<EflnBallItem> efln = ITEMS.register("efln_ball", EflnBallItem::new);

  // foods
  public static final ItemEnumObject<SlimeType,EdibleItem> slimeDrop = ITEMS.registerEnum(SlimeType.values(), "slime_drop", (type) -> new EdibleItem(type.getSlimeDropFood(type), TAB_GADGETS));
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
  private static final Item.Settings SPAGET_PROPS = new Item.Settings().maxCount(1);
  public static final ItemObject<Item> hardSpaghetti = ITEMS.register("hard_spaghetti", SPAGET_PROPS);
  public static final ItemObject<Item> soggySpaghetti = ITEMS.register("soggy_spaghetti", SPAGET_PROPS);
  public static final ItemObject<Item> coldSpaghetti = ITEMS.register("cold_spaghetti", SPAGET_PROPS);

  // Shurikens
  private static final Item.Settings THROWABLE_PROPS = new Item.Settings().maxCount(16).group(TAB_GADGETS);
  public static final ItemObject<ShurikenItem> quartzShuriken = ITEMS.register("quartz_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, QuartzShurikenEntity::new));
  public static final ItemObject<ShurikenItem> flintShuriken = ITEMS.register("flint_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, FlintShurikenEntity::new));

  /*
   * Entities
   */
  public static final EntityType<FancyItemFrameEntity> itemFrameEntity = ENTITIES.register("fancy_item_frame", () -> {
    return EntityType.Builder.<FancyItemFrameEntity>create(
      FancyItemFrameEntity::new, SpawnGroup.MISC)
      .setDimensions(0.5F, 0.5F)
      .maxTrackingRange(160);
//      .setUpdateInterval(Integer.MAX_VALUE)
//      .setCustomClientFactory((spawnEntity, world) -> new FancyItemFrameEntity(TinkerGadgets.itemFrameEntity.get(), world))
//      .setShouldReceiveVelocityUpdates(false);
  });
  public static final EntityType<GlowballEntity> glowBallEntity = ENTITIES.register("glow_ball", () -> {
    return EntityType.Builder.<GlowballEntity>create(GlowballEntity::new, SpawnGroup.MISC)
      .setDimensions(0.25F, 0.25F);
//      .setTrackingRange(64)
//      .setUpdateInterval(10)
//      .setCustomClientFactory((spawnEntity, world) -> new GlowballEntity(TinkerGadgets.glowBallEntity.get(), world))
//      .setShouldReceiveVelocityUpdates(true);
  });
  public static final EntityType<EflnBallEntity> eflnEntity = ENTITIES.register("efln_ball", () -> {
    return EntityType.Builder.<EflnBallEntity>create(EflnBallEntity::new, SpawnGroup.MISC)
      .setDimensions(0.25F, 0.25F);
//      .setTrackingRange(64)
//      .setUpdateInterval(10)
//      .setCustomClientFactory((spawnEntity, world) -> new EflnBallEntity(TinkerGadgets.eflnEntity.get(), world))
//      .setShouldReceiveVelocityUpdates(true);
  });
  public static final EntityType<QuartzShurikenEntity> quartzShurikenEntity = ENTITIES.register("quartz_shuriken", () -> {
    return EntityType.Builder.<QuartzShurikenEntity>create(QuartzShurikenEntity::new, SpawnGroup.MISC)
      .setDimensions(0.25F, 0.25F);
//      .setTrackingRange(64)
//      .setUpdateInterval(10)
//      .setCustomClientFactory((spawnEntity, world) -> new QuartzShurikenEntity(TinkerGadgets.quartzShurikenEntity.get(), world))
//      .setShouldReceiveVelocityUpdates(true);
  });
  public static final EntityType<FlintShurikenEntity> flintShurikenEntity = ENTITIES.register("flint_shuriken", () -> {
    return EntityType.Builder.<FlintShurikenEntity>create(FlintShurikenEntity::new, SpawnGroup.MISC)
      .setDimensions(0.25F, 0.25F);

//      .setUpdateInterval(10)
//      .setCustomClientFactory((spawnEntity, world) -> new FlintShurikenEntity(TinkerGadgets.flintShurikenEntity.get(), world))
//      .setShouldReceiveVelocityUpdates(true);
  });

  /**
   * Potions
   */
  public static final CarryPotionEffect carryEffect = Registry.register(Registry.STATUS_EFFECT, new Identifier(TConstruct.modID,"carry"), new CarryPotionEffect());

  @Override
  public void onInitialize() {
  }
}
