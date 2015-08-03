package tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class PatternTexture extends TextureColoredTexture {

  public PatternTexture(String partTexture, TextureAtlasSprite patternTexture, String spriteName) {
    super(partTexture, patternTexture, spriteName);
  }


  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    if(alpha(pixel) == 0) {
      return pixel;
    }
    // textureData == toolpart
    if(textureData == null) {
      loadData();
    }

    // we only want the inner 3/4 of the texture
    int x = getX(pxCoord);
    int y = getY(pxCoord);

    if(x < width/8 || x > width - width/8 || y < height/8 || y > height - height/8)
      return pixel;

    int c = textureData[mipmap][pxCoord];

    int a = alpha(c);
    // if not fully opaque, return pattern texture
    if(a < 255) {
      return pixel;
    }

    int r = red(pixel) / 2;
    int g = green(pixel) / 2;
    int b = blue(pixel) / 2;

    // otherwise darken color for pattern imprint
    return compose(r, g, b, a);
  }
}
