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

  /** @deprecated Use {@link #getQuads(IModifierToolStack, ModifierEntry, Function, TransformationMatrix, boolean, int)} */
  @Deprecated
  ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry modifier, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge);

  /**
   * Gets quads for the given model. Its a good idea to cache these quads. When doing so, you can assume the transformation matrix will be the same for a given state of isLarge
   * @param tool             Tool instance for modifier sensitive models
   * @param modifier         Modifier being rendered
   * @param spriteGetter     Function to fetch sprites
   * @param transforms       Transforms
   * @param isLarge          If true, use the large sprites and quads
   * @param startTintIndex   First tint index that can be used for this model. Use with {@link #getTintIndexes()} and {@link #getTint(IModifierToolStack, ModifierEntry, int)}, if neither is used this index will not work
   * @return  List of baked quads
   */
  default ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry modifier, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge, int startTintIndex) {
    return getQuads(tool, modifier, spriteGetter, transforms, isLarge);
  }

  /**
   * Gets the number of tint indexes used by this model
   * @return  Number of tint indexes used by this model
   */
  default int getTintIndexes() {
    return 0;
  }

  /**
   *
   * @param tool   Tool stack instance
   * @param entry  Modifier entry representing the relevant modifier
   * @param index  Localized tint index for this modifier, starting from 0. Only considers tint indexes this model handles as per {@link #getTintIndexes()}
   * @return  Color for this quad
   */
  default int getTint(IModifierToolStack tool, ModifierEntry entry, int index) {
    return -1;
  }
}
