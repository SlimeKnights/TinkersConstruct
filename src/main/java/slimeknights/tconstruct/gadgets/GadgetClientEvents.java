package slimeknights.tconstruct.gadgets;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.gadgets.client.FancyItemFrameRenderer;
import slimeknights.tconstruct.gadgets.client.RenderShuriken;

@SuppressWarnings("unused")
public class GadgetClientEvents extends ClientEventBase {
//  @SubscribeEvent
//  static void registerModels(ModelRegistryEvent event) {
//    // TODO: reinstate when Forge fixes itself
//    //StateContainer<Block, BlockState> dummyContainer = new StateContainer.Builder<Block, BlockState>(Blocks.AIR).add(BooleanProperty.create("map")).create(BlockState::new);
//    //for (FrameType frameType : FrameType.values()) {
//    //  ResourceLocation fancyFrame = new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame");
//    //  for (BlockState state : dummyContainer.getValidStates()) {
//    //    ModelLoader.addSpecialModel(BlockModelShapes.getModelLocation(fancyFrame, state));
//    //  }
//    //}
//
//    for (FrameType frameType : FrameType.values()) {
//      ModelLoader.addSpecialModel(new ModelIdentifier(new Identifier(TConstruct.modID, frameType.asString() + "_frame_empty"), "inventory"));
//      ModelLoader.addSpecialModel(new ModelIdentifier(new Identifier(TConstruct.modID, frameType.asString() + "_frame_map"), "inventory"));
//    }
//  }

  @Override
  public void onInitializeClient() {
    MinecraftClient mc = MinecraftClient.getInstance();

    BlockRenderLayerMap.INSTANCE.putBlock(TinkerGadgets.stoneLadder.get(), RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerGadgets.stoneTorch.get(), RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerGadgets.wallStoneTorch, RenderLayer.getCutout());

    BlockRenderLayerMap.INSTANCE.putBlock(TinkerGadgets.woodenRail.get(), RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerGadgets.woodenDropperRail.get(), RenderLayer.getCutout());

    EntityRendererRegistry.INSTANCE.register(TinkerGadgets.itemFrameEntity, (dispatcher, context) -> new FancyItemFrameRenderer(dispatcher, mc.getItemRenderer()));
    EntityRendererRegistry.INSTANCE.register(TinkerGadgets.glowBallEntity, (dispatcher, context) ->  new FlyingItemEntityRenderer<>(dispatcher, mc.getItemRenderer()));
    EntityRendererRegistry.INSTANCE.register(TinkerGadgets.eflnEntity, (dispatcher, context) ->  new FlyingItemEntityRenderer<>(dispatcher, mc.getItemRenderer()));
    EntityRendererRegistry.INSTANCE.register(TinkerGadgets.quartzShurikenEntity, (dispatcher, context) ->  new RenderShuriken(dispatcher, mc.getItemRenderer()));
    EntityRendererRegistry.INSTANCE.register(TinkerGadgets.flintShurikenEntity, (dispatcher, context) ->  new RenderShuriken(dispatcher, mc.getItemRenderer()));
  }
}
