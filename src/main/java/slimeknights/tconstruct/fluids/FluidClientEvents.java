package slimeknights.tconstruct.fluids;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;

@EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class FluidClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderLayers.setRenderLayer(TinkerFluids.skySlime.getStill(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerFluids.skySlime.getFlowing(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerFluids.enderSlime.getStill(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerFluids.enderSlime.getFlowing(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerFluids.liquidSoul.getStill(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerFluids.liquidSoul.getFlowing(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerFluids.moltenSoulsteel.getStill(), RenderLayer.getTranslucent());
    RenderLayers.setRenderLayer(TinkerFluids.moltenSoulsteel.getFlowing(), RenderLayer.getTranslucent());
  }
}
