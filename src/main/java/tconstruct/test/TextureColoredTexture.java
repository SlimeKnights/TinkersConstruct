package tconstruct.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class TextureColoredTexture extends AbstractColoredTexture {
  private final TextureAtlasSprite addTexture;
  private int[][] textureData;

  public TextureColoredTexture(TextureAtlasSprite addTexture, TextureAtlasSprite baseTexture, String spriteName) {
    super(baseTexture, spriteName);
    this.addTexture = addTexture;
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    int a = alpha(pixel);
    if(a == 0)
      return pixel;

    if(textureData == null) {
      if(addTexture.getFrameCount() > 0)
        textureData = addTexture.getFrameTextureData(0);
      else
        textureData = backupLoadTexture(new ResourceLocation(addTexture.getIconName()), Minecraft.getMinecraft().getResourceManager());
    }

    int c = textureData[mipmap][pxCoord];

    // multiply in the color
    int r = red(c);
    int b = blue(c);
    int g = green(c);

    r = mult(r, red(pixel)) & 0xff;
    g = mult(g, green(pixel)) & 0xff;
    b = mult(b, blue(pixel)) & 0xff;

    return compose(r,g,b,a);
  }
}
