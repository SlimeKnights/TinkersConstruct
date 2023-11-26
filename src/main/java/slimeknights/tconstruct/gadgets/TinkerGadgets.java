package slimeknights.tconstruct.gadgets;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierCreativeTab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.gadgets.block.FoodCakeBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.capability.PiggybackCapability;
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
import slimeknights.tconstruct.gadgets.item.slimesling.BaseSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.EarthSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.EnderSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.IchorSlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.slimesling.SkySlimeSlingItem;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.function.Function;

/**
 * Contains any special tools unrelated to the base tools
 */
@SuppressWarnings("unused")
public final class TinkerGadgets extends TinkerModule {
  /** Tab for all special tools added by the mod */
  public static final CreativeModeTab TAB_GADGETS = new SupplierCreativeTab(TConstruct.MOD_ID, "gadgets", () -> new ItemStack(TinkerGadgets.slimeSling.get(SlimeType.EARTH)));
  static final Logger log = Util.getLogger("tinker_gadgets");

  /*
   * Block base properties
   */
  private static final Item.Properties GADGET_PROPS = new Item.Properties().tab(TAB_GADGETS);
  private static final Item.Properties UNSTACKABLE_PROPS = new Item.Properties().tab(TAB_GADGETS).stacksTo(1);
  private static final Function<Block,? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, GADGET_PROPS);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GADGET_PROPS);
  private static final Function<Block,? extends BlockItem> UNSTACKABLE_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, UNSTACKABLE_PROPS);

  /*
   * Blocks
   */
  public static final ItemObject<PunjiBlock> punji = BLOCKS.register("punji", () -> new PunjiBlock(builder(Material.PLANT, SoundType.GRASS).strength(3.0F).speedFactor(0.4F).noOcclusion()), TOOLTIP_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<PiggyBackPackItem> piggyBackpack = ITEMS.register("piggy_backpack", () -> new PiggyBackPackItem(new Properties().tab(TinkerGadgets.TAB_GADGETS).stacksTo(16)));
  public static final EnumObject<FrameType,FancyItemFrameItem> itemFrame = ITEMS.registerEnum(FrameType.values(), "item_frame", (type) -> new FancyItemFrameItem(GADGET_PROPS, (world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, type)));
  // slings - TODO: remove in 1.19, replaced by slimestaffs & flinging/springing/bonking/warping
  private static final Item.Properties SLING_PROPS = new Item.Properties().tab(TAB_GADGETS).stacksTo(1).durability(250);
  public static final EnumObject<SlimeType, BaseSlimeSlingItem> slimeSling = new EnumObject.Builder<SlimeType, BaseSlimeSlingItem>(SlimeType.class)
    .put(SlimeType.EARTH, ITEMS.register("earth_slime_sling", () -> new EarthSlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.SKY, ITEMS.register("sky_slime_sling", () -> new SkySlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.ICHOR, ITEMS.register("ichor_slime_sling", () -> new IchorSlimeSlingItem(SLING_PROPS)))
    .put(SlimeType.ENDER, ITEMS.register("ender_slime_sling", () -> new EnderSlimeSlingItem(SLING_PROPS)))
    .build();
  // throwballs
  public static final ItemObject<GlowBallItem> glowBall = ITEMS.register("glow_ball", GlowBallItem::new);
  public static final ItemObject<EflnBallItem> efln = ITEMS.register("efln_ball", EflnBallItem::new);

  // foods
  private static final BlockBehaviour.Properties CAKE = builder(Material.CAKE, SoundType.WOOL).strength(0.5F);
  public static final EnumObject<SlimeType,FoodCakeBlock> cake = BLOCKS.registerEnum(SlimeType.LIQUID, "cake", type -> new FoodCakeBlock(CAKE, TinkerFood.getCake(type)), UNSTACKABLE_BLOCK_ITEM);
  public static final ItemObject<FoodCakeBlock> magmaCake = BLOCKS.register("magma_cake", () -> new FoodCakeBlock(CAKE, TinkerFood.MAGMA_CAKE), UNSTACKABLE_BLOCK_ITEM);

  // Shurikens
  private static final Item.Properties THROWABLE_PROPS = new Item.Properties().stacksTo(16).tab(TAB_GADGETS);
  public static final ItemObject<ShurikenItem> quartzShuriken = ITEMS.register("quartz_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, QuartzShurikenEntity::new));
  public static final ItemObject<ShurikenItem> flintShuriken = ITEMS.register("flint_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, FlintShurikenEntity::new));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<FancyItemFrameEntity>> itemFrameEntity = ENTITIES.register("fancy_item_frame", () ->
    EntityType.Builder.<FancyItemFrameEntity>of(
      FancyItemFrameEntity::new, MobCategory.MISC)
      .sized(0.5F, 0.5F)
      .setTrackingRange(10)
      .setUpdateInterval(Integer.MAX_VALUE)
      .setCustomClientFactory((spawnEntity, world) -> new FancyItemFrameEntity(TinkerGadgets.itemFrameEntity.get(), world))
      .setShouldReceiveVelocityUpdates(false)
  );
  public static final RegistryObject<EntityType<GlowballEntity>> glowBallEntity = ENTITIES.register("glow_ball", () ->
    EntityType.Builder.<GlowballEntity>of(GlowballEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new GlowballEntity(TinkerGadgets.glowBallEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
  );
  public static final RegistryObject<EntityType<EflnBallEntity>> eflnEntity = ENTITIES.register("efln_ball", () ->
    EntityType.Builder.<EflnBallEntity>of(EflnBallEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new EflnBallEntity(TinkerGadgets.eflnEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
  );
  public static final RegistryObject<EntityType<QuartzShurikenEntity>> quartzShurikenEntity = ENTITIES.register("quartz_shuriken", () ->
    EntityType.Builder.<QuartzShurikenEntity>of(QuartzShurikenEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new QuartzShurikenEntity(TinkerGadgets.quartzShurikenEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
  );
  public static final RegistryObject<EntityType<FlintShurikenEntity>> flintShurikenEntity = ENTITIES.register("flint_shuriken", () ->
    EntityType.Builder.<FlintShurikenEntity>of(FlintShurikenEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new FlintShurikenEntity(TinkerGadgets.flintShurikenEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
  );

  /*
   * Potions
   */
  public static final RegistryObject<CarryPotionEffect> carryEffect = MOB_EFFECTS.register("carry", CarryPotionEffect::new);

  /*
   * Events
   */
  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    PiggybackCapability.register();
    event.enqueueWork(() -> {
      cake.forEach(block -> ComposterBlock.add(1.0f, block));
      ComposterBlock.add(1.0f, magmaCake.get());
    });
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      datagenerator.addProvider(new GadgetRecipeProvider(datagenerator));
    }
  }
}
