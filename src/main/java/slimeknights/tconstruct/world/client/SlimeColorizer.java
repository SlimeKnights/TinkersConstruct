package slimeknights.tconstruct.world.client;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

public class SlimeColorizer implements IResourceManagerReloadListener {
  private static final ResourceLocation LOC_SLIME_PNG = Util.getResource("textures/colormap/slimegrasscolor.png");
  //private static final ResourceLocation LOC_SLIME_PNG = Util.getResource("textures/colormap/orangegrasscolor.png");

  private static int[] colorBuffer = new int[65536];

  /**
   * Gets foliage color from temperature and humidity. Args: temperature, humidity
   */
  public static int getColor(double temperature, double humidity)
  {
    //humidity *= temperature;
    //int i = (int)((1.0D - temperature) * 255.0D);
    //int j = (int)((1.0D - humidity) * 255.0D);
    //return colorBuffer[j << 8 | i];

    return colorBuffer[(int)(temperature*255d) << 8 | (int)(humidity*255d)];
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {
    try {
      colorBuffer = TextureUtil.readImageData(resourceManager, LOC_SLIME_PNG);
    } catch(IOException e) {
      TConstruct.log.error(e);
    }
  }
}
