package slimeknights.tconstruct.gadgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
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
import slimeknights.tconstruct.gadgets.client.RenderShuriken;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.MOD_ID, value=Dist.CLIENT, bus=Bus.MOD)
public class GadgetClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void registerModels(ModelRegistryEvent event) {
    FancyItemFrameRenderer.LOCATIONS_MODEL.forEach((type, loc) -> ModelLoader.addSpecialModel(loc));
    FancyItemFrameRenderer.LOCATIONS_MODEL_MAP.forEach((type, loc) -> ModelLoader.addSpecialModel(loc));
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    Minecraft mc = Minecraft.getInstance();

    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.itemFrameEntity.get(), (manager) -> new FancyItemFrameRenderer(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.glowBallEntity.get(), (manager) -> new SpriteRenderer<>(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.eflnEntity.get(), (manager) -> new SpriteRenderer<>(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.quartzShurikenEntity.get(), (manager) -> new RenderShuriken(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.flintShurikenEntity.get(), (manager) -> new RenderShuriken(manager, mc.getItemRenderer()));
  }
}
