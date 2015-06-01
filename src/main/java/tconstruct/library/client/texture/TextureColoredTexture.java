package tconstruct.library.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class TextureColoredTexture extends AbstractColoredTexture {

  private final TextureAtlasSprite addTexture;
  private final String addTextureLocation;
  private int[][] textureData;

  public boolean stencil = false;

  public TextureColoredTexture(String addTextureLocation, TextureAtlasSprite baseTexture,
                               String spriteName) {
    super(baseTexture, spriteName);
    this.addTextureLocation = addTextureLocation;
    this.addTexture = null;
  }

  public TextureColoredTexture(String addTextureLocation, String baseTextureLocation, String extra, String spriteName) {
    super(baseTextureLocation, extra, spriteName);
    this.addTextureLocation = addTextureLocation;
    this.addTexture = null;
  }

  public TextureColoredTexture(TextureAtlasSprite addTexture, TextureAtlasSprite baseTexture,
                               String spriteName) {
    super(baseTexture, spriteName);
    this.addTextureLocation = addTexture.getIconName();
    this.addTexture = addTexture;
  }

  public TextureColoredTexture(TextureAtlasSprite addTexture, String baseTextureLocation, String extra,
                               String spriteName) {
    super(baseTextureLocation, extra, spriteName);
    this.addTextureLocation = addTexture.getIconName();
    this.addTexture = addTexture;
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    int a = alpha(pixel);
    if (a == 0) {
      return pixel;
    }

    if (textureData == null) {
      loadData();
    }

    int c = textureData[mipmap][pxCoord];

    // multiply in the color
    int r = red(c);
    int b = blue(c);
    int g = green(c);

    if (!stencil) {
      r = mult(mult(r, red(pixel)), red(pixel));
      g = mult(mult(g, green(pixel)), green(pixel));
      b = mult(mult(b, blue(pixel)), blue(pixel));
    }
    return compose(r, g, b, a);
  }

  private void loadData() {
    if (addTexture != null && addTexture.getFrameCount() > 0) {
      textureData = addTexture.getFrameTextureData(0);
    } else {
      textureData = backupLoadTexture(new ResourceLocation(addTextureLocation),
                                      Minecraft.getMinecraft().getResourceManager());
    }
  }
}
