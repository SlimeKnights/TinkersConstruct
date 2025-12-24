package slimeknights.tconstruct.gadgets;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.gadgets.client.FancyItemFrameRenderer;
import slimeknights.tconstruct.gadgets.entity.shuriken.ShurikenEntityBase;
import slimeknights.tconstruct.tools.client.material.ThrownShurikenRenderer;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.MOD_ID, value=Dist.CLIENT, bus=Bus.MOD)
public class GadgetClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void registerModels(RegisterAdditional event) {
    FancyItemFrameRenderer.LOCATIONS_MODEL.values().forEach(event::register);
    FancyItemFrameRenderer.LOCATIONS_MODEL_MAP.values().forEach(event::register);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(TinkerGadgets.itemFrameEntity.get(), FancyItemFrameRenderer::new);
    EntityRendererProvider<ThrowableItemProjectile> throwable = ThrownItemRenderer::new;
    event.registerEntityRenderer(TinkerGadgets.glowBallEntity.get(), throwable);
    event.registerEntityRenderer(TinkerGadgets.eflnEntity.get(), throwable);
    EntityRendererProvider<ShurikenEntityBase> shuriken = ThrownShurikenRenderer::new;
    event.registerEntityRenderer(TinkerGadgets.quartzShurikenEntity.get(), shuriken);
    event.registerEntityRenderer(TinkerGadgets.flintShurikenEntity.get(), shuriken);
  }
}
