package slimeknights.tconstruct.library.client.texture;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;

public class MetalTextureTexture extends MetalColoredTexture {

  private final ResourceLocation addTextureLocation;
  protected TextureColoredTexture texture2;

  public MetalTextureTexture(ResourceLocation addTextureLocation, ResourceLocation baseTexture, String spriteName, int baseColor, float shinyness, float brightness, float hueshift) {
    super(baseTexture, spriteName, baseColor, shinyness, brightness, hueshift);
    this.addTextureLocation = addTextureLocation;
    texture2 = new TextureColoredTexture(addTextureLocation, baseTexture, spriteName);
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
    // at first do the metal texture
    texture2.load(manager, location, textureGetter);
    return super.load(manager, location, textureGetter);
  }

  @Override
  protected void processData(int[] data) {
    int[] textureData = texture2.getFrameTextureData(0)[0];
    for(int i = 0; i < data.length && i < textureData.length; i++) {
      data[i] = textureData[i];
    }
    super.processData(data);
  }
}
