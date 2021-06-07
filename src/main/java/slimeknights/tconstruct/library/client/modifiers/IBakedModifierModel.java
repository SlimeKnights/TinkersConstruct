package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.TransformationMatrix;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Modifier model for a particular tool. One copy of a class with this interface exists per modifier per tool
 */
public interface IBakedModifierModel {
  /**
   * Gets the key to use for caching results from this modifier. Should uniquely represent this tool state for the given modifier
   * For most models, this can be just the modifier itself
   * @param tool      Tool
   * @param modifier  Modifier instance
   * @return  Cache key for the given data, or null to not cache anything
   */
  @Nullable
  default Object getCacheKey(IModifierToolStack tool, ModifierEntry modifier) {
    return modifier.getModifier();
  }

  /**
   * Gets quads for the given model. Its a good idea to cache these quads. When doing so, you can assume the transformation matrix will be the same for a given state of isLarge
   * @param spriteGetter  Function to convert render materials into sprites
   * @param transforms    Transforms to apply
   * @return  List of baked quads
   */
  ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry modifier, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge);
}
