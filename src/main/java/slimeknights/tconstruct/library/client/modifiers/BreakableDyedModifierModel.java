package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.Function;

/** Modifier model that copies dye from a key and is breakable */
public class BreakableDyedModifierModel implements IBakedModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    RenderMaterial smallTexture = smallGetter.apply("");
    RenderMaterial brokenSmall = smallGetter.apply("_broken");
    RenderMaterial largeTexture = largeGetter.apply("");
    RenderMaterial brokenLarge = largeGetter.apply("_broken");
    if (smallTexture != null || brokenSmall != null || largeTexture != null || brokenLarge != null) {
      return new BreakableDyedModifierModel(smallTexture, brokenSmall, largeTexture, brokenLarge);
    }
    return null;
  };

  /** Textures to show */
  private final RenderMaterial[] textures;

  public BreakableDyedModifierModel(@Nullable RenderMaterial normalSmall, @Nullable RenderMaterial brokenSmall, @Nullable RenderMaterial normalLarge, @Nullable RenderMaterial brokenLarge) {
    this.textures = new RenderMaterial[]{normalSmall, brokenSmall, normalLarge, brokenLarge};
  }

  @Nullable
  @Override
  public Object getCacheKey(IModifierToolStack tool, ModifierEntry entry) {
    Modifier modifier = entry.getModifier();
    ResourceLocation key = modifier.getId();
    IModDataReadOnly data = tool.getPersistentData();
    int color = -1;
    if (data.contains(key, NBT.TAG_INT)) {
      color = data.getInt(modifier.getId());
    }
    return new CacheKey(modifier, color);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry modifier, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge) {
    return getQuads(tool, modifier, spriteGetter, transforms, isLarge, -1, null);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry modifier, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge, int startTintIndex, @Nullable ItemLayerPixels pixels) {
    RenderMaterial texture = textures[(isLarge ? 2 : 0) | (tool.isBroken() ? 1 : 0)];
    if (texture != null) {
      IModDataReadOnly data = tool.getPersistentData();
      ResourceLocation key = modifier.getModifier().getId();
      if (data.contains(key, NBT.TAG_INT)) {
        return MantleItemLayerModel.getQuadsForSprite(0xFF000000 | data.getInt(key), -1, spriteGetter.apply(texture), transforms, 0, pixels);
      }
    }
    return ImmutableList.of();
  }

  /** Data class to cache a colored texture */
  @Data
  private static class CacheKey {
    private final Modifier modifier;
    private final int color;
  }
}
