package slimeknights.tconstruct.library.client.materials;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Determines the type of texture used for rendering a specific material
 */
@RequiredArgsConstructor
public class MaterialRenderInfo {
  /** ID of this render info */
  @Getter
  private final MaterialId identifier;
  private final ResourceLocation texture;
  private final String[] fallbacks;
  /* color used to tint this model as an item colors handler */
  @Getter
  private final int vertexColor;

  /**
   * Tries to get a sprite for the given texture
   * @param base           Base texture
   * @param suffix         Sprite suffix
   * @param spriteGetter   Logic to get the sprite
   * @return  Sprite if valid, null if missing
   */
  @Nullable
  private TextureAtlasSprite tryTexture(RenderMaterial base, String suffix, Function<RenderMaterial,TextureAtlasSprite> spriteGetter) {
    TextureAtlasSprite sprite = spriteGetter.apply(getMaterial(base.getTextureLocation(), suffix));
    if (!MissingTextureSprite.getLocation().equals(sprite.getName())) {
      return sprite;
    }
    return null;
  }

  /**
   * Gets the texture for this render material
   * @param base          Base texture
   * @param spriteGetter  Logic to get a sprite
   * @return  Pair of the sprite, and a boolean indicating whether the sprite should be tinted
   */
  public TintedSprite getSprite(RenderMaterial base, Function<RenderMaterial,TextureAtlasSprite> spriteGetter) {
    TextureAtlasSprite sprite = tryTexture(base, getSuffix(texture), spriteGetter);
    if (sprite != null) {
      return TintedSprite.of(sprite, false);
    }
    for (String fallback : fallbacks) {
      sprite = tryTexture(base, fallback, spriteGetter);
      if (sprite != null) {
        return TintedSprite.of(sprite, true);
      }
    }
    return TintedSprite.of(spriteGetter.apply(base), true);
  }

  /**
   * Gets all dependencies for this render info
   * @param textures  Texture consumer
   * @param base      Base texture, will be used to generate texture names
   */
  public void getTextureDependencies(Consumer<RenderMaterial> textures, RenderMaterial base) {
    textures.accept(getMaterial(base.getTextureLocation(), getSuffix(texture)));
    for (String fallback : fallbacks) {
      textures.accept(getMaterial(base.getTextureLocation(), fallback));
    }
  }

  /**
   * Converts a material ID into a sprite suffix
   * @param material  Material ID
   * @return  Sprite name
   */
  private static String getSuffix(ResourceLocation material) {
    // namespace will only be minecraft for a texture override, so this lets you select to always use an untinted base texture as the materials texture
    if ("minecraft".equals(material.getNamespace())) {
      return material.getPath();
    }
    return material.getNamespace() + "_" + material.getPath();
  }

  /**
   * Gets a material for the given resource locations
   * @param texture   Texture path
   * @param suffix    Material or fallback suffix name
   * @return  Material instance
   */
  private static RenderMaterial getMaterial(ResourceLocation texture, String suffix) {
    return ModelLoaderRegistry.blockMaterial(new ResourceLocation(texture.getNamespace(), texture.getPath() + "_" + suffix));
  }

  @Data(staticConstructor = "of")
  public static class TintedSprite {
    private final TextureAtlasSprite sprite;
    private final boolean isTinted;
  }
}
