package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import slimeknights.tconstruct.library.client.RenderUtil;

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
        return RenderUtil.compose(50, 50, 50, 255);
      }
      else {
        return 0;
      }
    }

    return pixel;
  }
}
