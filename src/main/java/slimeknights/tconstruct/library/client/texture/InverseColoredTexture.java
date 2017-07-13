package slimeknights.tconstruct.library.client.texture;

import net.minecraft.util.ResourceLocation;

import slimeknights.tconstruct.library.client.RenderUtil;

public class InverseColoredTexture extends SimpleColoredTexture {

  public InverseColoredTexture(int colorLow, int colorMid, int colorHigh, ResourceLocation baseTexture, String spriteName) {
    super(colorLow, colorMid, colorHigh, baseTexture, spriteName);
  }

  @Override
  protected int colorPixel(int pixel, int pxCoord) {
    int a = RenderUtil.alpha(pixel);
    if(a == 0) {
      return pixel;
    }

    int brightness = getPerceptualBrightness(pixel);
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

    r = ~mult(r, brightness) & 0xff;
    g = ~mult(g, brightness) & 0xff;
    b = ~mult(b, brightness) & 0xff;

    // put it back together
    return RenderUtil.compose(r, g, b, a);
  }
}
