package tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.awt.image.DirectColorModel;

/**
 * All Logic by RWTema
 * This is the texture generation algorithm that is used in Extra Utilities for unstable parts.
 */
public class ExtraUtilityTexture extends AbstractColoredTexture {

  boolean[] trans;
  boolean[] edge;

  public ExtraUtilityTexture(TextureAtlasSprite baseTexture,
                             String spriteName) {
    super(baseTexture, spriteName);
  }

  public ExtraUtilityTexture(String baseTextureLocation, String extra, String spriteName) {
    super(baseTextureLocation, extra, spriteName);
  }

  @Override
  protected void processData(int[][] data) {
    // preprocess
    DirectColorModel color = new DirectColorModel(32, 16711680, '\uff00', 255, -16777216);

    edge = new boolean[width * height];
    trans = new boolean[width * height];

    int y;
    int c;
    for(int x = 0; x < width; ++x) {
      for(y = 0; y < height; ++y) {
        if(x == 0 || y == 0 || x == width - 1 || y == height - 1) {
          edge[coord(x, y)] = true;
        }

        c = data[0][coord(x, y)];
        if(c == 0 || color.getAlpha(c) < 64) {
          trans[coord(x, y)] = true;
          if(x > 0) {
            edge[coord(x - 1, y)] = true;
          }

          if(y > 0) {
            edge[coord(x, y - 1)] = true;
          }

          if(x < width - 1) {
            edge[coord(x + 1, y)] = true;
          }

          if(y < height - 1) {
            edge[coord(x, y + 1)] = true;
          }
        }
      }
    }

    super.processData(data);
  }

  private int coord(int x, int y) {
    return y * width + x;
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    if(!trans[pxCoord]) {
      int lum;
      if(edge[pxCoord]) {
        short alpha = 255;
        int x = pxCoord % width;
        int y = pxCoord / width;
        lum = 256 + (x * 16 / width + y * 16 / height - 16) * 6;
        if(lum >= 256) {
          lum = 255 - (lum - 256);
        }

        int col = alpha << 24 | lum << 16 | lum << 8 | lum;
        return col;
      }
      else {
        return 0;
      }
    }

    return pixel;
  }
}
