package slimeknights.tconstruct.gadgets;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.gadgets.block.FoodCakeBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.capability.PiggybackCapability;
import slimeknights.tconstruct.gadgets.data.GadgetRecipeProvider;
import slimeknights.tconstruct.gadgets.entity.EFLNEntity;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.FlintShurikenEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.QuartzShurikenEntity;
import slimeknights.tconstruct.gadgets.item.EFLNItem;
import slimeknights.tconstruct.gadgets.item.FancyItemFrameItem;
import slimeknights.tconstruct.gadgets.item.GlowBallItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem.CarryPotionEffect;
import slimeknights.tconstruct.gadgets.item.ShurikenItem;
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.world.block.FoliageType;

/**
 * Contains any special tools unrelated to the base tools.
 * TODO: consider merging this into commons, the distinction of what is a gadget is getting pretty narrow.
 */
@SuppressWarnings("unused")
public final class TinkerGadgets extends TinkerModule {
  /* Block base properties */

  /*
   * Blocks
   */
  public static final ItemObject<PunjiBlock> punji = BLOCKS.register("punji", () -> new PunjiBlock(builder(MapColor.PLANT, SoundType.GRASS).strength(3.0F).speedFactor(0.4F).noOcclusion().pushReaction(PushReaction.DESTROY)), TOOLTIP_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<PiggyBackPackItem> piggyBackpack = ITEMS.register("piggy_backpack", () -> new PiggyBackPackItem(new Properties().stacksTo(16)));
  public static final EnumObject<FrameType,FancyItemFrameItem> itemFrame = ITEMS.registerEnum(FrameType.values(), "item_frame", (type) -> new FancyItemFrameItem(ITEM_PROPS, (world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, type)));

  // throwballs
  public static final ItemObject<GlowBallItem> glowBall;
  public static final ItemObject<EFLNItem> efln;
  public static final ItemObject<ShurikenItem> quartzShuriken, flintShuriken;
  static {
    Item.Properties THROWABLE_PROPS = new Item.Properties().stacksTo(16);
    glowBall = ITEMS.register("glow_ball", () -> new GlowBallItem(THROWABLE_PROPS));
    efln = ITEMS.register("efln_ball", () -> new EFLNItem(THROWABLE_PROPS));
    quartzShuriken = ITEMS.register("quartz_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, QuartzShurikenEntity::new));
    flintShuriken = ITEMS.register("flint_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, FlintShurikenEntity::new));

  }

  // foods
  public static final EnumObject<FoliageType,FoodCakeBlock> cake;
  public static final ItemObject<FoodCakeBlock> magmaCake;
  static {
    BlockBehaviour.Properties CAKE = builder(SoundType.WOOL).forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY);
    cake = BLOCKS.registerEnum(FoliageType.values(), "cake", type -> new FoodCakeBlock(CAKE, TinkerFood.getCake(type)), UNSTACKABLE_BLOCK_ITEM);
    magmaCake = BLOCKS.register("magma_cake", () -> new FoodCakeBlock(CAKE, TinkerFood.MAGMA_CAKE), UNSTACKABLE_BLOCK_ITEM);
  }

  // Shurikens

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
  public static final RegistryObject<EntityType<EFLNEntity>> eflnEntity = ENTITIES.register("efln_ball", () ->
    EntityType.Builder.<EFLNEntity>of(EFLNEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new EFLNEntity(TinkerGadgets.eflnEntity.get(), world))
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
    DataGenerator generator = event.getGenerator();
    generator.addProvider(event.includeServer(), new GadgetRecipeProvider(generator.getPackOutput()));
  }

  /** Adds all relevant items to the creative tab, called by general tab */
  public static void addTabItems(ItemDisplayParameters itemDisplayParameters, Output output) {
    output.accept(punji);
    accept(output, itemFrame);
    output.accept(glowBall);
    output.accept(efln);
    output.accept(quartzShuriken);
    output.accept(flintShuriken);
    output.accept(piggyBackpack);
    accept(output, cake);
    output.accept(magmaCake);
  }
}
