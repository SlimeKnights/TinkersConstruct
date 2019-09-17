package slimeknights.tconstruct.fluids;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.common.ClientProxy;

public class FluidsClientProxy extends ClientProxy {

  @Override
  public void construct() {
    super.construct();
    //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTextures);
  }

  @SubscribeEvent
  public void registerTextures(TextureStitchEvent.Pre event) {
    if (event.getMap().getBasePath().equalsIgnoreCase("textures")) {
      event.addSprite(FluidIcons.FLUID_STILL);
      event.addSprite(FluidIcons.FLUID_FLOWING);
      event.addSprite(FluidIcons.MILK_FLUID_STILL);
      event.addSprite(FluidIcons.MILK_FLUID_FLOWING);
      event.addSprite(FluidIcons.STONE_FLUID_STILL);
      event.addSprite(FluidIcons.STONE_FLUID_FLOWING);
    }
  }

}
