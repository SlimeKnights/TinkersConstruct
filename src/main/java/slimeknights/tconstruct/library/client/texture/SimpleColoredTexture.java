package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SimpleColoredTexture extends AbstractColoredTexture {

  protected final int colorLow, colorMid, colorHigh;

  public SimpleColoredTexture(int colorLow, int colorMid, int colorHigh, TextureAtlasSprite baseTexture,
                              String spriteName) {
    super(baseTexture, spriteName);
    this.colorLow = colorLow;
    this.colorMid = colorMid;
    this.colorHigh = colorHigh;
  }

  int minBrightness;
  int maxBrightness;

  float weight = 0.60f; // 60-40 split between main color and high/low color

  @Override
  protected void processData(int[][] data) {
    int max = 0;
    int min = 255;
    // setup brigthness data
    for(int x = 0; x < width; ++x) {
      for(int y = 0; y < height; ++y) {
        int c = data[0][y * width + x];
        if(alpha(c) == 0)
          continue;
        int b = getPerceptualBrightness(c);
        if(b < min)
          min = b;
        if(b > max)
          max = b;
      }
    }

    // calculate the actual limits where we change color
    int d = max-min;
    d /= 2;
    minBrightness = Math.max(min+1, min + (int)(d * 0.4f));
    maxBrightness = Math.min(max-1, max - (int)(d * 0.3f));

    super.processData(data);
  }

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

    r = mult(r, red(pixel)) & 0xff;
    g = mult(g, blue(pixel)) & 0xff;
    b = mult(b, green(pixel)) & 0xff;

    // put it back together
    return compose(r, g, b, a);
  }
}
