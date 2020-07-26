package slimeknights.tconstruct.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.gadgets.block.DropperRailBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;
import slimeknights.tconstruct.gadgets.item.EflnBallItem;
import slimeknights.tconstruct.gadgets.item.FancyItemFrameItem;
import slimeknights.tconstruct.gadgets.item.GlowBallItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem.CarryPotionEffect;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.SlimeSlingItem;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.piggyback.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.utils.SupplierItemGroup;
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.shared.block.SlimeBlock.SlimeType;

import java.util.function.Function;

/**
 * Contains any special tools unrelated to the base tools
 */
@SuppressWarnings("unused")
public final class TinkerGadgets extends TinkerModule {
  /** Tab for all special tools added by the mod */
  public static final ItemGroup TAB_GADGETS = new SupplierItemGroup(TConstruct.modID, "gadgets", () -> new ItemStack(TinkerGadgets.slimeSling.get(SlimeType.GREEN)));
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
  public static final BlockItemObject<LadderBlock> stoneLadder = BLOCKS.register("stone_ladder", () -> new LadderBlock(builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.STONE).hardnessAndResistance(0.1F).notSolid()) {}, DEFAULT_BLOCK_ITEM);
  public static final BlockItemObject<PunjiBlock> punji = BLOCKS.register("punji", () -> new PunjiBlock(builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(3.0F)), TOOLTIP_BLOCK_ITEM);
  // torch
  private static final Block.Properties STONE_TORCH = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.STONE).doesNotBlockMovement().hardnessAndResistance(0.0F).lightValue(14);
  public static final RegistryObject<WallTorchBlock> wallStoneTorch = BLOCKS.registerNoItem("wall_stone_torch", () -> new WallTorchBlock(STONE_TORCH) {});
  public static final BlockItemObject<TorchBlock> stoneTorch = BLOCKS.register("stone_torch",
                                                                               () -> new TorchBlock(STONE_TORCH) {},
                                                                               (block) -> new WallOrFloorItem(block, wallStoneTorch.get(), GADGET_PROPS));
  // rails
  private static final Block.Properties WOODEN_RAIL = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.WOOD).doesNotBlockMovement().hardnessAndResistance(0.2F);
  public static final BlockItemObject<DropperRailBlock> woodenDropperRail = BLOCKS.register("wooden_dropper_rail", () -> new DropperRailBlock(WOODEN_RAIL), TOOLTIP_BLOCK_ITEM);
  public static final BlockItemObject<RailBlock> woodenRail = BLOCKS.register("wooden_rail", () -> new RailBlock(WOODEN_RAIL) {}, DEFAULT_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> stoneStick = ITEMS.register("stone_stick", GADGET_PROPS);
  public static final ItemObject<PiggyBackPackItem> piggyBackpack = ITEMS.register("piggy_backpack", PiggyBackPackItem::new);
  public static final EnumObject<FrameType,FancyItemFrameItem> itemFrame = ITEMS.registerEnum(FrameType.values(), "item_frame", (type) -> new FancyItemFrameItem(((world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, type.getId()))));
  // slime tools
  public static final EnumObject<SlimeType,SlimeSlingItem> slimeSling = ITEMS.registerEnum(SlimeType.values(), "slime_sling", (type) -> new SlimeSlingItem(UNSTACKABLE_PROPS));
  public static final EnumObject<SlimeType,SlimeBootsItem> slimeBoots = ITEMS.registerEnum(SlimeType.values(), "slime_boots", (type) -> new SlimeBootsItem(type, UNSTACKABLE_PROPS));
  // throwballs
  public static final ItemObject<GlowBallItem> glowBall = ITEMS.register("glow_ball", GlowBallItem::new);
  public static final ItemObject<EflnBallItem> efln = ITEMS.register("efln_ball", EflnBallItem::new);

  // foods
  public static final EnumObject<SlimeType,EdibleItem> slimeDrop = ITEMS.registerEnum(SlimeType.values(), "slime_drop", (type) -> new EdibleItem(type.getSlimeDropFood(type), TAB_GADGETS));
  // jerkies
  public static final ItemObject<EdibleItem> monsterJerky = ITEMS.register("monster_jerky", () -> new EdibleItem(TinkerFood.MONSTER_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> beefJerky = ITEMS.register("beef_jerky", () -> new EdibleItem(TinkerFood.BEEF_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> chickenJerky = ITEMS.register("chicken_jerky", () -> new EdibleItem(TinkerFood.CHICKEN_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> porkJerky = ITEMS.register("pork_jerky", () -> new EdibleItem(TinkerFood.PORK_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> muttonJerky = ITEMS.register("mutton_jerky", () -> new EdibleItem(TinkerFood.MUTTON_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> rabbitJerky = ITEMS.register("rabbit_jerky", () -> new EdibleItem(TinkerFood.RABBIT_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> fishJerky = ITEMS.register("fish_jerky", () -> new EdibleItem(TinkerFood.FISH_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> salmonJerky = ITEMS.register("salmon_jerky", () -> new EdibleItem(TinkerFood.SALMON_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> clownfishJerky = ITEMS.register("clownfish_jerky", () -> new EdibleItem(TinkerFood.CLOWNFISH_JERKY, TAB_GADGETS));
  public static final ItemObject<EdibleItem> pufferfishJerky = ITEMS.register("pufferfish_jerky", () -> new EdibleItem(TinkerFood.PUFFERFISH_JERKY, TAB_GADGETS));
  // Spicy Memes
  private static final Item.Properties SPAGET_PROPS = new Item.Properties().maxStackSize(1);
  public static final ItemObject<Item> hardSpaghetti = ITEMS.register("hard_spaghetti", SPAGET_PROPS);
  public static final ItemObject<Item> soggySpaghetti = ITEMS.register("soggy_spaghetti", SPAGET_PROPS);
  public static final ItemObject<Item> coldSpaghetti = ITEMS.register("cold_spaghetti", SPAGET_PROPS);

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
}
