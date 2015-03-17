package tconstruct.test;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import tconstruct.library.tinkering.Material;

public class SimpleColoredTexture extends AbstractColoredTexture {
  private final Material material;

  public SimpleColoredTexture(Material material, TextureAtlasSprite baseTexture, String spriteName) {
    super(baseTexture, spriteName);
    this.material = material;
  }

  public SimpleColoredTexture(Material material, String baseTextureLocation, String extra, String spriteName) {
    super(baseTextureLocation, extra, spriteName);
    this.material = material;
  }

  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    int a = alpha(pixel);
    if(a == 0)
      return pixel;

    int brightness = getPerceptualBrightness(pixel);
    int c = material.colorMid;
    if(brightness < 128)
      c = material.colorLow;
    else if(brightness > 192)
      c = material.colorHigh;


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
