package slimeknights.tconstruct.library.client.texture;

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

    float mult = 1.0f;
    if(a < 64) {
      return pixel;
    }

    boolean edge = false;
    if(x > 0) {
      a = alpha(textureData[mipmap][coord(x - 1, y)]);
      if(a < 64) {
        edge = true;
      }
    }
    if(y < height-1) {
      a = alpha(textureData[mipmap][coord(x, y + 1)]);
      if(a < 64) {
        edge = true;
      }
    }
    if(x < width-1) {
      a = alpha(textureData[mipmap][coord(x + 1, y)]);
      if(a < 64) {
        edge = true;
      }
    }
    if(y > 0) {
      a = alpha(textureData[mipmap][coord(x, y - 1)]);
      if(a < 64) {
        edge = true;
      }
    }

    mult = 0.5f;
    if(edge)
      mult = 0.6f;


    int r = (int)((float)red(pixel) * mult);
    int g = (int)((float)green(pixel) * mult);
    int b = (int)((float)blue(pixel) * mult);

    if(r > 255)
      r = 255;
    if(g > 255)
      g = 255;
    if(b > 255)
      b = 255;

    // otherwise darken color for pattern imprint
    return compose(r, g, b, 255);
  }
}
