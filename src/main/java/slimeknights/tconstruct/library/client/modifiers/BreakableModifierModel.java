package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.ItemLayerModel;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Modifier model for a modifier that changes its texture when broken
 */
public class BreakableModifierModel implements IBakedModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    RenderMaterial normalSmall = smallGetter.apply("");
    RenderMaterial brokenSmall = smallGetter.apply("_broken");
    RenderMaterial normalLarge = smallGetter.apply("");
    RenderMaterial brokenLarge = smallGetter.apply("_broken");
    // we need both to exist for this to work
    if (normalSmall != null || brokenSmall != null || normalLarge != null || brokenLarge != null) {
      return new BreakableModifierModel(normalSmall, brokenSmall, normalLarge, brokenLarge);
    }
    return null;
  };


  /** Textures for this model */
  private final RenderMaterial[] sprites;
  /* Caches of the small quad list */
  @SuppressWarnings("unchecked")
  private final ImmutableList<BakedQuad>[] quadCache = new ImmutableList[4];
  public BreakableModifierModel(@Nullable RenderMaterial normalSmall, @Nullable RenderMaterial brokenSmall, @Nullable RenderMaterial normalLarge, @Nullable RenderMaterial brokenLarge) {
    this.sprites = new RenderMaterial[] {normalSmall, brokenSmall, normalLarge, brokenLarge};
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry entry, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge) {
    // first get the cache index
    int index = (isLarge ? 2 : 0) | (tool.isBroken() ? 1 : 0);
    // if not cached, build
    if (quadCache[index] == null) {
      if (sprites[index] == null) {
        quadCache[index] = ImmutableList.of();
      } else {
        quadCache[index] = ItemLayerModel.getQuadsForSprite(-1, spriteGetter.apply(sprites[index]), transforms);
      }
    }
    return quadCache[index];
  }
}
