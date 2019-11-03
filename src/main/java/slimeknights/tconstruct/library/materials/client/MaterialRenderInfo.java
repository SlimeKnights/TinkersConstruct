package slimeknights.tconstruct.library.materials.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

/**
 * Determines the type of texture used for rendering a specific material
 */
public interface MaterialRenderInfo {

  TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location);

  boolean isStitched();

  boolean useVertexColoring();

  int getVertexColor();

  // this actually would require its own thing, but we put it here for simplicity
  String getTextureSuffix();

  MaterialRenderInfo setTextureSuffix(String suffix);

  // todo: copy material render info implementations over

}
