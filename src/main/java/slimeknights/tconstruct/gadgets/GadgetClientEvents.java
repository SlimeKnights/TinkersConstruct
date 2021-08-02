package slimeknights.tconstruct.gadgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.gadgets.client.FancyItemFrameRenderer;
import slimeknights.tconstruct.gadgets.client.HeadWithOverlayModel;
import slimeknights.tconstruct.gadgets.client.RenderShuriken;
import slimeknights.tconstruct.gadgets.entity.FrameType;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.MOD_ID, value=Dist.CLIENT, bus=Bus.MOD)
public class GadgetClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void registerModels(ModelRegistryEvent event) {
    // TODO: reinstate when Forge fixes itself
    //StateContainer<Block, BlockState> dummyContainer = new StateContainer.Builder<Block, BlockState>(Blocks.AIR).add(BooleanProperty.create("map")).create(BlockState::new);
    //for (FrameType frameType : FrameType.values()) {
    //  ResourceLocation fancyFrame = new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame");
    //  for (BlockState state : dummyContainer.getValidStates()) {
    //    ModelLoader.addSpecialModel(BlockModelShapes.getModelLocation(fancyFrame, state));
    //  }
    //}

    for (FrameType frameType : FrameType.values()) {
      ModelLoader.addSpecialModel(new ModelResourceLocation(TConstruct.getResource(frameType.getString() + "_frame_empty"), "inventory"));
      ModelLoader.addSpecialModel(new ModelResourceLocation(TConstruct.getResource(frameType.getString() + "_frame_map"), "inventory"));
    }
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    Minecraft mc = Minecraft.getInstance();

    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.itemFrameEntity.get(), (manager) -> new FancyItemFrameRenderer(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.glowBallEntity.get(), (manager) -> new SpriteRenderer<>(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.eflnEntity.get(), (manager) -> new SpriteRenderer<>(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.quartzShurikenEntity.get(), (manager) -> new RenderShuriken(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.flintShurikenEntity.get(), (manager) -> new RenderShuriken(manager, mc.getItemRenderer()));

    // skull rendering
    GenericHeadModel normalHead = new GenericHeadModel(0, 0, 64, 32);
    GenericHeadModel tinkersOverlayHead = new HeadWithOverlayModel(0, 0, 0, 16, 32, 32);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.BLAZE, normalHead);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.ENDERMAN, new GenericHeadModel(0, 0, 32, 16));
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.STRAY, tinkersOverlayHead);
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.BLAZE, new ResourceLocation("textures/entity/blaze.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.ENDERMAN, TConstruct.getResource("textures/entity/skull/enderman.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.STRAY, TConstruct.getResource("textures/entity/skull/stray.png"));
    // zombies
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.HUSK, new GenericHeadModel(0, 0, 64, 64));
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.DROWNED, tinkersOverlayHead);
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.HUSK, new ResourceLocation("textures/entity/zombie/husk.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.DROWNED, TConstruct.getResource("textures/entity/skull/drowned.png"));
    // spider
    GenericHeadModel spiderHead = new GenericHeadModel(32, 4, 64, 32);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.SPIDER, spiderHead);
    SkullTileEntityRenderer.MODELS.put(TinkerHeadType.CAVE_SPIDER, spiderHead);
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.SPIDER, new ResourceLocation("textures/entity/spider/spider.png"));
    SkullTileEntityRenderer.SKINS.put(TinkerHeadType.CAVE_SPIDER, new ResourceLocation("textures/entity/spider/cave_spider.png"));
  }
}
