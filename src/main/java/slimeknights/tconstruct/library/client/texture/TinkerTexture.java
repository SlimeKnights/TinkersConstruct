package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class TinkerTexture extends TextureAtlasSprite {

  public static TextureAtlasSprite loadManually(String sprite) {
    return new TinkerTexture(sprite);
  }

  protected TinkerTexture(String spriteName) {
    super(spriteName);
  }

  protected ResourceLocation getResourceLocation(ResourceLocation resourceLocation) {
    return new ResourceLocation(resourceLocation.getResourceDomain(), String.format("%s/%s%s", "textures", resourceLocation.getResourcePath(), ".png"));
  }
}
