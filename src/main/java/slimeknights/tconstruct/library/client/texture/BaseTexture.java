package slimeknights.tconstruct.library.client.texture;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;

import java.util.Collection;

public class BaseTexture extends AbstractColoredTexture {

  public BaseTexture(String baseTextureLocation, String spriteName) {
    super(baseTextureLocation, spriteName);
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return ImmutableList.of();
  }

  @Override
  protected int colorPixel(int pixel, int mipmap, int pxCoord) {
    return pixel;
  }
}
