package slimeknights.tconstruct.fluids;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.client.render.RenderLayer;
import slimeknights.tconstruct.common.ClientEventBase;

public class FluidClientEvents extends ClientEventBase {

  @Override
  public void onInitializeClient() {
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
