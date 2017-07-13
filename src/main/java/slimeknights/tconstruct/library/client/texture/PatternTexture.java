package slimeknights.tconstruct.library.client.texture;

import net.minecraft.util.ResourceLocation;

import slimeknights.tconstruct.library.client.RenderUtil;

public class PatternTexture extends TextureColoredTexture {

  public PatternTexture(ResourceLocation partTexture, ResourceLocation patternTexture, String spriteName) {
    super(partTexture, patternTexture, spriteName);
  }

  @Override
  protected int colorPixel(int pixel, int pxCoord) {
    if(RenderUtil.alpha(pixel) == 0) {
      return pixel;
    }

    if(width > addTextureWidth) {
      // scale coordinates to match the other texture
      int texX = (int) ((float) getX(pxCoord) * scale);
      int texY = (int) ((float) getY(pxCoord) * scale);
      pxCoord = texY * addTextureWidth + texX;
    }

    // we only want the inner 3/4 of the texture
    int x = pxCoord % addTextureWidth;
    int y = pxCoord / addTextureHeight;

    int x2 = x - offsetX;
    int y2 = y - offsetY;

    if(x2 >= addTextureWidth || x2 < 0 || y2 >= addTextureHeight || y2 < 0) {
      // offset moved it out of the picture, equals transparent
      return pixel;
    }

    if(x < addTextureWidth / 8 || x > addTextureWidth - addTextureWidth / 8 || y < addTextureHeight / 8 || y > addTextureHeight - addTextureHeight / 8) {
      return pixel;
    }

    int c = textureData[coord2(x2, y2)];

    int a = RenderUtil.alpha(c);

    float mult;
    if(a < 64) {
      return pixel;
    }

    boolean edge = false;
    if(x > 0) {
      a = RenderUtil.alpha(textureData[coord2(x - 1, y)]);
      if(a < 64) {
        edge = true;
      }
    }
    if(y < height - 1) {
      a = RenderUtil.alpha(textureData[coord2(x, y + 1)]);
      if(a < 64) {
        edge = true;
      }
    }
    if(x < width - 1) {
      a = RenderUtil.alpha(textureData[coord2(x + 1, y)]);
      if(a < 64) {
        edge = true;
      }
    }
    if(y > 0) {
      a = RenderUtil.alpha(textureData[coord2(x, y - 1)]);
      if(a < 64) {
        edge = true;
      }
    }

    mult = 0.5f;
    if(edge) {
      mult = 0.6f;
    }


    int r = (int) ((float) RenderUtil.red(pixel) * mult);
    int g = (int) ((float) RenderUtil.green(pixel) * mult);
    int b = (int) ((float) RenderUtil.blue(pixel) * mult);

    if(r > 255) {
      r = 255;
    }
    if(g > 255) {
      g = 255;
    }
    if(b > 255) {
      b = 255;
    }

    // otherwise darken color for pattern imprint
    return RenderUtil.compose(r, g, b, 255);
  }
}
