package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class InverseColoredTexture extends SimpleColoredTexture {

  public InverseColoredTexture(int colorLow, int colorMid, int colorHigh, TextureAtlasSprite baseTexture, String spriteName) {
    super(colorLow, colorMid, colorHigh, baseTexture, spriteName);
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    int a = alpha(pixel);
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
    int r = red(c);
    int b = blue(c);
    int g = green(c);

    r = ~mult(r, brightness) & 0xff;
    g = ~mult(g, brightness) & 0xff;
    b = ~mult(b, brightness) & 0xff;

    // put it back together
    return compose(r, g, b, a);
  }
}
