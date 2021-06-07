package slimeknights.tconstruct.tools.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.TransformationMatrix;
import slimeknights.tconstruct.library.client.modifiers.IUnbakedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Modifier model that turns invisible when out of overslime
 */
public class OverslimeModifierModel extends NormalModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    RenderMaterial smallTexture = smallGetter.apply("");
    RenderMaterial largeTexture = largeGetter.apply("");
    if (smallTexture != null || largeTexture != null) {
      return new OverslimeModifierModel(smallTexture, largeTexture);
    }
    return null;
  };

  public OverslimeModifierModel(@Nullable RenderMaterial smallTexture, @Nullable RenderMaterial largeTexture) {
    super(smallTexture, largeTexture);
  }

  @Nullable
  @Override
  public Object getCacheKey(IModifierToolStack tool, ModifierEntry entry) {
    Modifier modifier = entry.getModifier();
    if (modifier instanceof OverslimeModifier && ((OverslimeModifier) modifier).getOverslime(tool) == 0) {
      return null;
    }
    return super.getCacheKey(tool, entry);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry entry, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge) {
    Modifier modifier = entry.getModifier();
    if (modifier instanceof OverslimeModifier && ((OverslimeModifier) modifier).getOverslime(tool) == 0) {
      return ImmutableList.of();
    }
    return super.getQuads(tool, entry, spriteGetter, transforms, isLarge);
  }
}
