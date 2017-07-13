package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class MetalTextureTexture extends MetalColoredTexture {

  protected TextureColoredTexture texture2;

  public MetalTextureTexture(ResourceLocation addTextureLocation, ResourceLocation baseTexture, String spriteName, int baseColor, float shinyness, float brightness, float hueshift) {
    super(baseTexture, spriteName, baseColor, shinyness, brightness, hueshift);
    texture2 = new TextureColoredTexture(addTextureLocation, baseTexture, spriteName);
  }

  @Override
  public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    // at first do the metal texture
    texture2.load(manager, location, textureGetter);
    return super.load(manager, location, textureGetter);
  }

  @Override
  protected void processData(int[] data) {
    super.processData(texture2.getFrameTextureData(0)[0]);
  }
}
