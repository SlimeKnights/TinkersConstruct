package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.Function;

/** Modifier model that copies dye from a key and is breakable */
public class BreakableDyedModifierModel implements IBakedModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    Material smallTexture = smallGetter.apply("");
    Material brokenSmall = smallGetter.apply("_broken");
    Material largeTexture = largeGetter.apply("");
    Material brokenLarge = largeGetter.apply("_broken");
    if (smallTexture != null || brokenSmall != null || largeTexture != null || brokenLarge != null) {
      return new BreakableDyedModifierModel(smallTexture, brokenSmall, largeTexture, brokenLarge);
    }
    return null;
  };

  /** Textures to show */
  private final Material[] textures;

  public BreakableDyedModifierModel(@Nullable Material normalSmall, @Nullable Material brokenSmall, @Nullable Material normalLarge, @Nullable Material brokenLarge) {
    this.textures = new Material[]{normalSmall, brokenSmall, normalLarge, brokenLarge};
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    ModifierId modifier = entry.getId();
    IModDataView data = tool.getPersistentData();
    int color = -1;
    if (data.contains(modifier, Tag.TAG_INT)) {
      color = data.getInt(modifier);
    }
    return new CacheKey(modifier, color);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, @Nullable ItemLayerPixels pixels) {
    Material texture = textures[(isLarge ? 2 : 0) | (tool.isBroken() ? 1 : 0)];
    if (texture != null) {
      IModDataView data = tool.getPersistentData();
      ResourceLocation key = modifier.getId();
      if (data.contains(key, Tag.TAG_INT)) {
        return MantleItemLayerModel.getQuadsForSprite(0xFF000000 | data.getInt(key), -1, spriteGetter.apply(texture), transforms, 0, pixels);
      }
    }
    return ImmutableList.of();
  }

  /** Data class to cache a colored texture */
  private record CacheKey(ModifierId modifier, int color) {}
}
