package slimeknights.tconstruct.fluids;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
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
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.skySlime.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.skySlime.getFlowing(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.enderSlime.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.enderSlime.getFlowing(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.liquidSoul.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.liquidSoul.getFlowing(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.moltenSoulsteel.getStill(), RenderLayer.getTranslucent());
    BlockRenderLayerMapImpl.INSTANCE.putFluid(TinkerFluids.moltenSoulsteel.getFlowing(), RenderLayer.getTranslucent());
  }
}
