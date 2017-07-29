package slimeknights.tconstruct.library.client.texture;

import net.minecraft.util.ResourceLocation;

import slimeknights.tconstruct.library.client.RenderUtil;

public class SimpleColoredTexture extends AbstractColoredTexture {


  protected final int colorLow;
  protected final int colorMid;
  protected final int colorHigh;
  protected int minBrightness;
  protected int maxBrightness;
  protected int brightnessData[];

  public SimpleColoredTexture(int colorLow, int colorMid, int colorHigh, ResourceLocation baseTexture,
                              String spriteName) {
    super(baseTexture, spriteName);
    this.colorLow = colorLow;
    this.colorMid = colorMid;
    this.colorHigh = colorHigh;
  }

  @Override
  protected void preProcess(int[] data) {
    // setup brigthness data
    int max = 0;
    int min = 255;
    brightnessData = new int[data.length];
    for(int i = 0; i < data.length; i++) {
      int pixel = data[i];
      if(RenderUtil.alpha(pixel) == 0) {
        continue;
      }
      int brightness = getPerceptualBrightness(pixel);
      if(brightness < min) {
        min = brightness;
      }
      if(brightness > max) {
        max = brightness;
      }
      brightnessData[i] = brightness;
    }

    // calculate the actual limits where we change color
    int brightnessDiff = max - min;
    brightnessDiff /= 2;
    minBrightness = Math.max(min + 1, min + (int) (brightnessDiff * 0.4f));
    maxBrightness = Math.min(max - 1, max - (int) (brightnessDiff * 0.3f));
  }

  @Override
  protected void postProcess(int[] data) {
    // delete memory that we don't need anymore. We only cached it for faster loading anyway
    brightnessData = null;
  }

  @Override
  protected int colorPixel(int pixel, int pxCoord) {
    int a = RenderUtil.alpha(pixel);
    if(a == 0) {
      return pixel;
    }

    int brightness = brightnessData[pxCoord];
    int c = colorMid;
    if(brightness < minBrightness) {
      c = colorLow;
    }
    else if(brightness > maxBrightness) {
      c = colorHigh;
    }

    // multiply in the color
    int r = RenderUtil.red(c);
    int b = RenderUtil.blue(c);
    int g = RenderUtil.green(c);

    r = mult(r, RenderUtil.red(pixel)) & 0xff;
    g = mult(g, RenderUtil.blue(pixel)) & 0xff;
    b = mult(b, RenderUtil.green(pixel)) & 0xff;

    // put it back together
    return RenderUtil.compose(r, g, b, a);
  }
}
