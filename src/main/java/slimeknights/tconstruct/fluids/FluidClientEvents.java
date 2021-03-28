package slimeknights.tconstruct.fluids;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
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
    RenderTypeLookup.setRenderLayer(TinkerFluids.skySlime.getStill(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerFluids.skySlime.getFlowing(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerFluids.enderSlime.getStill(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerFluids.enderSlime.getFlowing(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerFluids.liquidSoul.getStill(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerFluids.liquidSoul.getFlowing(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerFluids.moltenSoulsteel.getStill(), RenderType.getTranslucent());
    RenderTypeLookup.setRenderLayer(TinkerFluids.moltenSoulsteel.getFlowing(), RenderType.getTranslucent());
  }
}
