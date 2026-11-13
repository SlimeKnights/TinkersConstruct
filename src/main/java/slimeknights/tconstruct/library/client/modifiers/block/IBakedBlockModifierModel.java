package slimeknights.tconstruct.library.client.modifiers.block;

import com.mojang.math.Transformation;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.modifiers.block.model.BlockModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import javax.annotation.Nullable;

import java.util.function.Function;

/**
 * Modifier model for a particular tool. One copy of a class with this interface exists per modifier per tool.
 * @see BlockModifierModel
 */
public interface IBakedBlockModifierModel {
  /**
   * Gets the key to use for caching results from this modifier. Should uniquely represent this tool state for the given modifier
   * For most models, this can be just the modifier itself
   * @param tool      Tool
   * @param modifier  Modifier instance
   * @return  Cache key for the given data, or null to not cache anything
   */
  @Nullable
  default Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    return modifier == ModifierEntry.EMPTY ? null : modifier.getId();
  }

  /**
   * Gets quads for the given model. These quads should not be cached as that will make them inconsistent with {@link ItemLayerPixels}.
   * @param tool             Tool instance for modifier sensitive models
   * @param modifier         Modifier instance for modifier being rendered
   * @param context          Baking context for this model
   * @param spriteGetter     Function to fetch sprites
   * @param transforms       Transforms
   * @param builder          Builder to add quads to
   */
  void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material,TextureAtlasSprite> spriteGetter, IQuadTransformer transforms, SimpleBakedModel.Builder builder);
}
