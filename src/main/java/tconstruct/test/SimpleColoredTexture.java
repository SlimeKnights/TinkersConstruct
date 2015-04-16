package tconstruct.test;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import tconstruct.library.tinkering.Material;

public class SimpleColoredTexture extends AbstractColoredTexture {
  private final int colorLow, colorMid, colorHigh;

  public SimpleColoredTexture(int colorLow, int colorMid, int colorHigh, TextureAtlasSprite baseTexture, String spriteName) {
    super(baseTexture, spriteName);
    this.colorLow = colorLow;
    this.colorMid = colorMid;
    this.colorHigh = colorHigh;
  }

  public SimpleColoredTexture(int colorLow, int colorMid, int colorHigh, String baseTextureLocation, String extra, String spriteName) {
    super(baseTextureLocation, extra, spriteName);
    this.colorLow = colorLow;
    this.colorMid = colorMid;
    this.colorHigh = colorHigh;
  }

  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    int a = alpha(pixel);
    if(a == 0)
      return pixel;

    int brightness = getPerceptualBrightness(pixel);
    int c = colorMid;
    if(brightness < 128)
      c = colorLow;
    else if(brightness > 192)
      c = colorHigh;


    // multiply in the color
    int r = red(c);
    int b = blue(c);
    int g = green(c);

    r = mult(r,brightness) & 0xff;
    g = mult(g,brightness) & 0xff;
    b = mult(b,brightness) & 0xff;

    // put it back together
    return compose(r,g,b,a);
  }
}
