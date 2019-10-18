package slimeknights.tconstruct.gadgets;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.potion.Effect;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
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
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.GadgetBlocks;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.ItemRegistryAdapter;
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
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeBlock;

@Pulse(id = TinkerPulseIds.TINKER_GADGETS_PULSE_ID, description = "All the fun toys")
@ObjectHolder(TConstruct.modID)
public class TinkerGadgets extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_GADGETS_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> GadgetClientProxy::new, () -> ServerProxy::new);

  public static EntityType<FancyItemFrameEntity> fancy_item_frame;
  public static EntityType<GlowballEntity> throwable_glow_ball;
  public static EntityType<EflnBallEntity> throwable_efln_ball;

  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new StoneLadderBlock(), "stone_ladder");

    registry.register(new StoneTorchBlock(), "stone_torch");
    registry.register(new WallStoneTorchBlock(), "wall_stone_torch");

    registry.register(new PunjiBlock(), "punji");

    registry.register(new WoodenRailBlock(), "wooden_rail");
    registry.register(new WoodenDropperRailBlock(), "wooden_dropper_rail");

    registry.register(new DriedClayBlock(), "dried_clay");
    registry.register(new DriedClayBlock(), "dried_clay_bricks");

    registry.register(new DriedClaySlabBlock(), "dried_clay_slab");
    registry.register(new DriedClaySlabBlock(), "dried_clay_bricks_slab");

    registry.register(new StairsBaseBlock(DecorativeBlocks.dried_clay), "dried_clay_stairs");
    registry.register(new StairsBaseBlock(DecorativeBlocks.dried_clay_bricks), "dried_clay_bricks_stairs");
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabGadgets);
    registry.registerBlockItem(GadgetBlocks.stone_ladder);

    registry.registerBlockItem(new WallOrFloorItem(GadgetBlocks.stone_torch, GadgetBlocks.wall_stone_torch, (new Item.Properties()).group(TinkerRegistry.tabGadgets)));

    registry.registerBlockItem(GadgetBlocks.punji);

    registry.registerBlockItem(GadgetBlocks.wooden_rail);
    registry.registerBlockItem(GadgetBlocks.wooden_dropper_rail);

    registry.registerBlockItem(DecorativeBlocks.dried_clay);
    registry.registerBlockItem(DecorativeBlocks.dried_clay_bricks);

    registry.registerBlockItem(DecorativeBlocks.dried_clay_slab);
    registry.registerBlockItem(DecorativeBlocks.dried_clay_bricks_slab);

    registry.registerBlockItem(DecorativeBlocks.dried_clay_stairs);
    registry.registerBlockItem(DecorativeBlocks.dried_clay_bricks_stairs);

    registry.register(new Item((new Item.Properties()).group(TinkerRegistry.tabGadgets)), "stone_stick");

    for (SlimeBlock.SlimeType type : SlimeBlock.SlimeType.VISIBLE_COLORS) {
      registry.register(new SlimeSlingItem(), "slime_sling_" + type.getName());
      registry.register(new SlimeBootsItem(type), "slime_boots_" + type.getName());
    }

    registry.register(new PiggyBackPackItem(), "piggy_backpack");

    for (FrameType frameType : FrameType.values()) {
      registry.register(new FancyItemFrameItem((world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, frameType.getId())), frameType.getName() + "_item_frame");
    }

    registry.register(new GlowBallItem(), "glow_ball");
    registry.register(new EflnBallItem(), "efln_ball");

    registry.register(new SpaghettiItem(), "hard_spaghetti");
    registry.register(new SpaghettiItem(), "soggy_spaghetti");
    registry.register(new SpaghettiItem(), "cold_spaghetti");
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
    MinecraftForge.EVENT_BUS.register(new GadgetEvents());
    proxy.postInit();

    TinkerRegistry.tabGadgets.setDisplayIcon(new ItemStack(GadgetItems.slime_sling_green));
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
