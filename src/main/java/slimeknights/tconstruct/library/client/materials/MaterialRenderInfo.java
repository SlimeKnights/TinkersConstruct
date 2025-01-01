package slimeknights.tconstruct.library.client.materials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Determines the type of texture used for rendering a specific material
 */
@RequiredArgsConstructor
public class MaterialRenderInfo {
  /** ID of this render info */
  @Getter
  private final MaterialVariantId identifier;
  @Nullable @Getter
  private final ResourceLocation texture;
  @Getter
  private final String[] fallbacks;
  /** color used to tint quads of this texture when the fallback is used */
  @Getter
  private final int vertexColor;
  /** Extra light to add to the material, allows some materials to appear to glow slightly */
  @Getter
  private final int luminosity; // TODO: rename to emissivity

  /**
   * Tries to get a sprite for the given texture
   * @param base              Base texture
   * @param suffix            Sprite suffix
   * @param spriteGetter      Logic to get the sprite
   * @return  Sprite if valid, null if missing
   */
  @Nullable
  private TextureAtlasSprite trySprite(Material base, String suffix, Function<Material,TextureAtlasSprite> spriteGetter) {
    Material materialTexture = getMaterial(base.texture(), suffix);
    TextureAtlasSprite sprite = spriteGetter.apply(materialTexture);
    if (!MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
      return sprite;
    }
    return null;
  }

  /**
   * Gets the texture for this render material
   * @param base               Base texture
   * @param spriteGetter       Logic to get a sprite
   * @return  Pair of the sprite, and a boolean indicating whether the sprite should be tinted
   */
  public TintedSprite getSprite(Material base, Function<Material,TextureAtlasSprite> spriteGetter) {
    TextureAtlasSprite sprite;
    if (texture != null) {
      sprite = trySprite(base, getSuffix(texture), spriteGetter);
      if (sprite != null) {
        return new TintedSprite(sprite, -1, getLuminosity());
      }
    }
    for (String fallback : fallbacks) {
      sprite = trySprite(base, fallback, spriteGetter);
      if (sprite != null) {
        return new TintedSprite(sprite, vertexColor, getLuminosity());
      }
    }
    return new TintedSprite(spriteGetter.apply(base), vertexColor, getLuminosity());
  }

  /**
   * Converts a material ID into a sprite suffix
   * @param material  Material ID
   * @return  Sprite name
   */
  public static String getSuffix(ResourceLocation material) {
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
  private static Material getMaterial(ResourceLocation texture, String suffix) {
    return new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(texture.getNamespace(), texture.getPath() + "_" + suffix));
  }

  /**
   * Data class for a sprite that may be tinted
   */
  public record TintedSprite(TextureAtlasSprite sprite, int color, int emissivity) {}
}
