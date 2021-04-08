package slimeknights.tconstruct.gadgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
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
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.client.RenderShuriken;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
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
      ModelLoader.addSpecialModel(new ModelIdentifier(new Identifier(TConstruct.modID, frameType.asString() + "_frame_empty"), "inventory"));
      ModelLoader.addSpecialModel(new ModelIdentifier(new Identifier(TConstruct.modID, frameType.asString() + "_frame_map"), "inventory"));
    }
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    MinecraftClient mc = MinecraftClient.getInstance();

    RenderLayers.setRenderLayer(TinkerGadgets.stoneLadder.get(), RenderLayer.getCutout());
    RenderLayers.setRenderLayer(TinkerGadgets.stoneTorch.get(), RenderLayer.getCutout());
    RenderLayers.setRenderLayer(TinkerGadgets.wallStoneTorch.get(), RenderLayer.getCutout());

    RenderLayers.setRenderLayer(TinkerGadgets.woodenRail.get(), RenderLayer.getCutout());
    RenderLayers.setRenderLayer(TinkerGadgets.woodenDropperRail.get(), RenderLayer.getCutout());

    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.itemFrameEntity.get(), (manager) -> new FancyItemFrameRenderer(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.glowBallEntity.get(), (manager) -> new FlyingItemEntityRenderer<>(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.eflnEntity.get(), (manager) -> new FlyingItemEntityRenderer<>(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.quartzShurikenEntity.get(), (manager) -> new RenderShuriken(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.flintShurikenEntity.get(), (manager) -> new RenderShuriken(manager, mc.getItemRenderer()));
  }
}
