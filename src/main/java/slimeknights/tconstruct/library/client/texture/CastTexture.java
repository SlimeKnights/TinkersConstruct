package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import slimeknights.tconstruct.library.client.RenderUtil;

public class CastTexture extends TextureColoredTexture {

  public CastTexture(String addTextureLocation, TextureAtlasSprite baseTexture, String spriteName) {
    super(addTextureLocation, baseTexture, spriteName);
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    if(RenderUtil.alpha(pixel) == 0) {
      return pixel;
    }
    // textureData == toolpart
    if(textureData == null) {
      loadData();
    }

    // we only want the inner 3/4 of the texture
    int x = getX(pxCoord);
    int y = getY(pxCoord);
/*
    if(x < width/8 || x > width - width/8 || y < height/8 || y > height - height/8)
      return pixel;
*/
    int c = textureData[mipmap][pxCoord];

    int a = RenderUtil.alpha(c);

    // we want to have the row above darker and the rew below brighter
    // to achieve that, we check if the above or below is transparent in the tool texture
    float mult = 1.0f;
    if(a < 64) {
      /*
      if(y < height-1 && alpha(textureData[mipmap][coord(x, y + 1)]) > 64) {
        mult = 0.5f;
      }
      else if(y > 0 &&  alpha(textureData[mipmap][coord(x, y - 1)]) > 64) {
        mult = 2.0f;
      }
      else {
        return pixel;
      }
      */
      return pixel;
    }

    // we want to have the row above darker and the rew below brighter
    // to achieve that, we check if the above or below is transparent in the tool texture
    // dark takes precedence, so we check for bright first so we can overwrite it
    int count = 0;
    boolean edge = false;
    if(x > 0) {
      a = RenderUtil.alpha(textureData[mipmap][coord(x - 1, y)]);
      if(a < 64) {
        //mult = 1.2f;
        count++;
        edge = true;
      }
    }
    if(y < height-1) {
      a = RenderUtil.alpha(textureData[mipmap][coord(x, y + 1)]);
      if(a < 64) {
        //mult = 1.2f;
        count++;
        edge = true;
      }
    }
    if(x < width-1) {
      a = RenderUtil.alpha(textureData[mipmap][coord(x + 1, y)]);
      if(a < 64) {
        //mult = 0.8f;
        count++;
        edge = true;
      }
    }
    if(y > 0) {
      a = RenderUtil.alpha(textureData[mipmap][coord(x, y - 1)]);
      if(a < 64) {
        //mult = 0.8f;
        count-=3;
        edge = true;
      }
    }

    // inner always are invisible
    if(!edge || count == 0)
      return 0;

    //mult = 1f + 0.1f*count;

    if(count < 0)
      mult = 0.8f;
    else if(count > 0)
      mult = 1.1f;

    int r = (int)((float) RenderUtil.red(pixel) * mult);
    int g = (int)((float) RenderUtil.green(pixel) * mult);
    int b = (int)((float) RenderUtil.blue(pixel) * mult);

    if(r > 255)
      r = 255;
    if(g > 255)
      g = 255;
    if(b > 255)
      b = 255;

    // otherwise darken color for pattern imprint
    return RenderUtil.compose(r, g, b, 255);
  }
}
