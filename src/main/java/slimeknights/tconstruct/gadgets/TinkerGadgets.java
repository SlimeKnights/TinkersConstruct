package slimeknights.tconstruct.gadgets;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.potion.Effect;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.block.StairsBaseBlock;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.gadgets.block.DriedClayBlock;
import slimeknights.tconstruct.gadgets.block.DriedClaySlabBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.block.StoneLadderBlock;
import slimeknights.tconstruct.gadgets.block.StoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WallStoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WoodenDropperRailBlock;
import slimeknights.tconstruct.gadgets.block.WoodenRailBlock;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;
import slimeknights.tconstruct.gadgets.item.EflnBallItem;
import slimeknights.tconstruct.gadgets.item.FancyItemFrameItem;
import slimeknights.tconstruct.gadgets.item.GlowBallItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.SlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.SpaghettiItem;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeBlock;

@Pulse(id = TinkerPulseIds.TINKER_GADGETS_PULSE_ID, description = "All the fun toys")
@ObjectHolder(TConstruct.modID)
public class TinkerGadgets extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_GADGETS_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> GadgetClientProxy::new, () -> ServerProxy::new);

  public static final StoneLadderBlock stone_ladder = null;

  public static final StoneTorchBlock stone_torch = null;
  public static final WallStoneTorchBlock wall_stone_torch = null;

  public static final PunjiBlock punji = null;

  public static final WoodenRailBlock wooden_rail = null;
  public static final WoodenDropperRailBlock wooden_dropper_rail = null;

  public static DriedClayBlock dried_clay;
  public static DriedClayBlock dried_clay_bricks;

  public static final DriedClaySlabBlock dried_clay_slab = null;
  public static final DriedClaySlabBlock dried_clay_bricks_slab = null;

  public static final StairsBaseBlock dried_clay_stairs = null;
  public static final StairsBaseBlock dried_clay_bricks_stairs = null;

  public static final SlimeSlingItem slime_sling_blue = null;
  public static final SlimeSlingItem slime_sling_purple = null;
  public static final SlimeSlingItem slime_sling_magma = null;
  public static final SlimeSlingItem slime_sling_green = null;
  public static final SlimeSlingItem slime_sling_blood = null;

  public static final SlimeBootsItem slime_boots_blue = null;
  public static final SlimeBootsItem slime_boots_purple = null;
  public static final SlimeBootsItem slime_boots_magma = null;
  public static final SlimeBootsItem slime_boots_green = null;
  public static final SlimeBootsItem slime_boots_blood = null;

  public static final PiggyBackPackItem piggy_backpack = null;

  public static final FancyItemFrameItem jewel_item_frame = null;
  public static final FancyItemFrameItem aluminum_brass_item_frame = null;
  public static final FancyItemFrameItem cobalt_item_frame = null;
  public static final FancyItemFrameItem ardite_item_frame = null;
  public static final FancyItemFrameItem manyullyn_item_frame = null;
  public static final FancyItemFrameItem gold_item_frame = null;
  public static final FancyItemFrameItem clear_item_frame = null;

  public static final GlowBallItem glow_ball = null;
  public static final EflnBallItem efln_ball = null;

  public static final SpaghettiItem hard_spaghetti = null;
  public static final SpaghettiItem soggy_spaghetti = null;
  public static final SpaghettiItem cold_spaghetti = null;

  public static EntityType<FancyItemFrameEntity> fancy_item_frame;
  public static EntityType<GlowballEntity> throwable_glow_ball;
  public static EntityType<EflnBallEntity> throwable_efln_ball;

  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    register(registry, new StoneLadderBlock(), "stone_ladder");

    register(registry, new StoneTorchBlock(), "stone_torch");
    register(registry, new WallStoneTorchBlock(), "wall_stone_torch");

    register(registry, new PunjiBlock(), "punji");

    register(registry, new WoodenRailBlock(), "wooden_rail");
    register(registry, new WoodenDropperRailBlock(), "wooden_dropper_rail");

    dried_clay = register(registry, new DriedClayBlock(), "dried_clay");
    dried_clay_bricks = register(registry, new DriedClayBlock(), "dried_clay_bricks");

    register(registry, new DriedClaySlabBlock(), "dried_clay_slab");
    register(registry, new DriedClaySlabBlock(), "dried_clay_bricks_slab");

    register(registry, new StairsBaseBlock(dried_clay), "dried_clay_stairs");
    register(registry, new StairsBaseBlock(dried_clay_bricks), "dried_clay_bricks_stairs");
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    CreativeTab tabGadgets = TinkerRegistry.tabGadgets;
    registerBlockItem(registry, stone_ladder, tabGadgets);

    registerBlockItem(registry, new WallOrFloorItem(stone_torch, wall_stone_torch, (new Item.Properties()).group(tabGadgets)));

    registerBlockItem(registry, punji, tabGadgets);

    registerBlockItem(registry, wooden_rail, tabGadgets);
    registerBlockItem(registry, wooden_dropper_rail, tabGadgets);

    registerBlockItem(registry, dried_clay, tabGadgets);
    registerBlockItem(registry, dried_clay_bricks, tabGadgets);

    registerBlockItem(registry, dried_clay_slab, tabGadgets);
    registerBlockItem(registry, dried_clay_bricks_slab, tabGadgets);

    registerBlockItem(registry, dried_clay_stairs, tabGadgets);
    registerBlockItem(registry, dried_clay_bricks_stairs, tabGadgets);

    register(registry, new Item((new Item.Properties()).group(tabGadgets)), "stone_stick");

    for (SlimeBlock.SlimeType type : SlimeBlock.SlimeType.VISIBLE_COLORS) {
      register(registry, new SlimeSlingItem(), "slime_sling_" + type.getName());
      register(registry, new SlimeBootsItem(type), "slime_boots_" + type.getName());
    }

    register(registry, new PiggyBackPackItem(), "piggy_backpack");

    for (FrameType frameType : FrameType.values()) {
      register(registry, new FancyItemFrameItem((world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, frameType.getId())), frameType.getName() + "_item_frame");
    }

    register(registry, new GlowBallItem(), "glow_ball");
    register(registry, new EflnBallItem(), "efln_ball");

    register(registry, new SpaghettiItem(), "hard_spaghetti");
    register(registry, new SpaghettiItem(), "soggy_spaghetti");
    register(registry, new SpaghettiItem(), "cold_spaghetti");
  }

  @SubscribeEvent
  public void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    IForgeRegistry<EntityType<?>> registry = event.getRegistry();

    fancy_item_frame = EntityType.Builder.<FancyItemFrameEntity>create(
            FancyItemFrameEntity::new, EntityClassification.MISC)
            .size(0.5F, 0.5F)
            .setTrackingRange(160)
            .setUpdateInterval(Integer.MAX_VALUE)
            .setCustomClientFactory((spawnEntity, world) -> new FancyItemFrameEntity(fancy_item_frame, world))
            .setShouldReceiveVelocityUpdates(false)
            .build("fancy_item_frame");

    registry.register(fancy_item_frame.setRegistryName("tconstruct:fancy_item_frame"));

    throwable_glow_ball = EntityType.Builder.<GlowballEntity>create(
            GlowballEntity::new, EntityClassification.MISC)
            .size(0.25F, 0.25F)
            .setTrackingRange(64)
            .setUpdateInterval(10)
            .setCustomClientFactory((spawnEntity, world) -> new GlowballEntity(throwable_glow_ball, world))
            .setShouldReceiveVelocityUpdates(true)
            .build("glow_ball");

    registry.register(throwable_glow_ball.setRegistryName("tconstruct:glow_ball"));

    throwable_efln_ball = EntityType.Builder.<EflnBallEntity>create(
            EflnBallEntity::new, EntityClassification.MISC)
            .size(0.25F, 0.25F)
            .setTrackingRange(64)
            .setUpdateInterval(10)
            .setCustomClientFactory((spawnEntity, world) -> new EflnBallEntity(throwable_efln_ball, world))
            .setShouldReceiveVelocityUpdates(true)
            .build("efln_ball");

    registry.register(throwable_efln_ball.setRegistryName("tconstruct:efln_ball"));
  }

  @SubscribeEvent
  public void registerPotions(RegistryEvent.Register<Effect> event) {
    IForgeRegistry<Effect> registry = event.getRegistry();

    registry.register(PiggyBackPackItem.CarryPotionEffect.INSTANCE);
  }

  @SubscribeEvent
  public void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();
  }

  @SubscribeEvent
  public void init(final InterModEnqueueEvent event) {
    proxy.init();
  }

  @SubscribeEvent
  public void postInit(final InterModProcessEvent event) {
    proxy.postInit();

    TinkerRegistry.tabGadgets.setDisplayIcon(new ItemStack(slime_sling_green));
  }

  @SubscribeEvent
  public void registerModels(final ModelRegistryEvent event) {
    proxy.registerModels();
  }

  @SubscribeEvent
  public void clientSetup(final FMLClientSetupEvent event) {
    proxy.clientSetup();
  }

}
