package tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class GuiOutlineTexture extends ExtraUtilityTexture {

  public GuiOutlineTexture(String baseTextureLocation, String spriteName) {
    super(baseTextureLocation, spriteName);
  }

  public GuiOutlineTexture(TextureAtlasSprite baseTexture, String spriteName) {
    super(baseTexture, spriteName);
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    if(!trans[pxCoord]) {
      if(edge[pxCoord]) {
        return compose(128,128,128, 255);
      }
      else {
        return 0;
      }
    }

    return pixel;
  }
}
