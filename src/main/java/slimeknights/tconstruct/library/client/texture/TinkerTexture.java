package slimeknights.tconstruct.library.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class TinkerTexture extends TextureAtlasSprite {

  public static TextureAtlasSprite loadManually(ResourceLocation sprite) {
    return new TinkerTexture(sprite.toString());
  }

  protected TinkerTexture(String spriteName) {
    super(spriteName);
  }
}
