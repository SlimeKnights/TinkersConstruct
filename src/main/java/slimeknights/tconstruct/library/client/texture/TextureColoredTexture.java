package slimeknights.tconstruct.library.client.texture;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;

import slimeknights.tconstruct.library.client.RenderUtil;

public class TextureColoredTexture extends AbstractColoredTexture {

  protected final ResourceLocation addTextureLocation;
  protected TextureAtlasSprite addTexture;
  protected int[] textureData;
  protected int addTextureWidth;
  protected int addTextureHeight;
  protected float scale;
  protected int offsetX = 0;
  protected int offsetY = 0;

  public boolean stencil = false;

  public TextureColoredTexture(ResourceLocation addTextureLocation, ResourceLocation baseTexture, String spriteName) {
    super(baseTexture, spriteName);
    this.addTextureLocation = addTextureLocation;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return ImmutableList.<ResourceLocation>builder()
        .addAll(super.getDependencies())
        .add(addTextureLocation)
        .build();
  }

  @Override
  public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    addTexture = textureGetter.apply(addTextureLocation);

    super.load(manager, location, textureGetter);

    return false;
  }

  @Override
  protected void preProcess(int[] data) {
    textureData = addTexture.getFrameTextureData(0)[0];
    // we need to keep the sizes, otherwise the secondary texture might set our size to a different value
    // since it uses the same loading code as the main texture
    // read: 32x32 block textures with 16x16 tool textures = stuff goes boom
    addTextureWidth = addTexture.getIconWidth();
    addTextureHeight = addTexture.getIconHeight();
    scale = (float) addTextureHeight / (float) width;
  }

  @Override
  protected void postProcess(int[] data) {
    // not needed anymore, release memory
    textureData = null;
  }

  @Override
  protected int colorPixel(int pixel, int pxCoord) {
    int a = RenderUtil.alpha(pixel);
    if(a == 0) {
      return pixel;
    }

    int texCoord = pxCoord;
    if(width > addTextureWidth) {
      int texX = (pxCoord % width) % addTextureWidth;
      int texY = (pxCoord / height) % addTextureHeight;
      texCoord = texY * addTextureWidth + texX;
    }
    int c = textureData[texCoord];

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

  public void setOffset(int x, int y) {
    offsetX = x;
    offsetY = y;
  }

  protected int coord2(int x, int y) {
    return y * addTextureWidth + x;
  }
}
