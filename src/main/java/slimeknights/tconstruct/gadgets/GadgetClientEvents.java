package slimeknights.tconstruct.gadgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    FancyItemFrameRenderer.LOCATIONS_MODEL.forEach((type, loc) -> ForgeModelBakery.addSpecialModel(loc));
    FancyItemFrameRenderer.LOCATIONS_MODEL_MAP.forEach((type, loc) -> ForgeModelBakery.addSpecialModel(loc));
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    Minecraft mc = Minecraft.getInstance();

    event.enqueueWork(() -> {
      EntityRenderers.register(TinkerGadgets.itemFrameEntity.get(), FancyItemFrameRenderer::new);
      EntityRenderers.register(TinkerGadgets.glowBallEntity.get(), ThrownItemRenderer::new);
      EntityRenderers.register(TinkerGadgets.eflnEntity.get(), ThrownItemRenderer::new);
      EntityRenderers.register(TinkerGadgets.quartzShurikenEntity.get(), RenderShuriken::new);
      EntityRenderers.register(TinkerGadgets.flintShurikenEntity.get(), RenderShuriken::new);
    });
  }
}
