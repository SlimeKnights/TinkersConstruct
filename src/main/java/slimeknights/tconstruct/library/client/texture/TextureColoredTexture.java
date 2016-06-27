package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import slimeknights.tconstruct.library.client.RenderUtil;

public class TextureColoredTexture extends AbstractColoredTexture {

  protected TextureAtlasSprite addTexture;
  protected final String addTextureLocation;
  protected int[][] textureData;
  protected int textureW;
  protected int textureH;
  protected float scale;
  protected int offsetX = 0;
  protected int offsetY = 0;

  public boolean stencil = false;

  public TextureColoredTexture(String addTextureLocation, TextureAtlasSprite baseTexture,
                               String spriteName) {
    super(baseTexture, spriteName);
    this.addTextureLocation = addTextureLocation;
    this.addTexture = null;
  }

  public TextureColoredTexture(TextureAtlasSprite addTexture, TextureAtlasSprite baseTexture,
                               String spriteName) {
    super(baseTexture, spriteName);
    this.addTextureLocation = addTexture.getIconName();
    this.addTexture = addTexture;
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    int a = RenderUtil.alpha(pixel);
    if(a == 0) {
      return pixel;
    }

    if(textureData == null) {
      loadData();
    }


    int texCoord = pxCoord;
    if(width > textureW) {
      int texX = (pxCoord % width) % textureW;
      int texY = (pxCoord / height) % textureH;
      texCoord = texY * textureW + texX;
    }
    int c = textureData[mipmap][texCoord];

    // multiply in the color
    int r = RenderUtil.red(c);
    int b = RenderUtil.blue(c);
    int g = RenderUtil.green(c);

    if(!stencil) {
      r = mult(mult(r, RenderUtil.red(pixel)), RenderUtil.red(pixel));
      g = mult(mult(g, RenderUtil.green(pixel)), RenderUtil.green(pixel));
      b = mult(mult(b, RenderUtil.blue(pixel)), RenderUtil.blue(pixel));
    }
    return RenderUtil.compose(r, g, b, a);
  }

  protected void loadData() {
    if(addTexture == null || addTexture.getFrameCount() <= 0) {
      addTexture = backupLoadTexture(new ResourceLocation(addTextureLocation), Minecraft.getMinecraft().getResourceManager());
    }

    textureData = addTexture.getFrameTextureData(0);
    // we need to keep the sizes, otherwise the secondary texture might set our size to a different value
    // since it uses the same loading code as the main texture
    // read: 32x32 block textures with 16x16 tool textures = stuff goes boom
    textureW = addTexture.getIconWidth();
    textureH = addTexture.getIconHeight();
    scale = (float) textureH / (float) width;
  }

  public void setOffset(int x, int y) {
    offsetX = x;
    offsetY = y;
  }

  protected int coord2(int x, int y) {
    return y * textureW + x;
  }
}
