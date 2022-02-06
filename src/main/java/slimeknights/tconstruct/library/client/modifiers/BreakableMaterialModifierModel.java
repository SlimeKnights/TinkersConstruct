package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.Function;

/** Model for a modifier that has variants based on a material and the tool's broken state */
public class BreakableMaterialModifierModel implements IBakedModifierModel {
  /** Fetches relevant material textures after checking if the texture exists */
  @Nullable
  private static Material stitchMaterialTextures(Function<String,Material> textureGetter, String name) {
    Material baseTexture = textureGetter.apply(name);
    if (baseTexture != null) {
      for (MaterialRenderInfo info : MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos()) {
        ResourceLocation texture = info.getTexture();
        if (texture != null) {
          textureGetter.apply(name + "_" + MaterialRenderInfo.getSuffix(texture));
        }
        for (String fallback : info.getFallbacks()) {
          textureGetter.apply(name + "_" + fallback);
        }
      }
    }
    return baseTexture;
  }

  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    Material smallTexture = stitchMaterialTextures(smallGetter, "");
    Material brokenSmall = stitchMaterialTextures(smallGetter, "_broken");
    Material largeTexture = stitchMaterialTextures(largeGetter, "");
    Material brokenLarge = stitchMaterialTextures(largeGetter, "_broken");
    if (smallTexture != null || brokenSmall != null || largeTexture != null || brokenLarge != null) {
      return new BreakableMaterialModifierModel(smallTexture, brokenSmall, largeTexture, brokenLarge);
    }
    return null;
  };

  /** Textures to show */
  private final Material[] textures;

  public BreakableMaterialModifierModel(@Nullable Material normalSmall, @Nullable Material brokenSmall, @Nullable Material normalLarge, @Nullable Material brokenLarge) {
    this.textures = new Material[]{normalSmall, brokenSmall, normalLarge, brokenLarge};
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    Modifier modifier = entry.getModifier();
    return new CacheKey(modifier, tool.getPersistentData().getString(modifier.getId()));
  }

  @Nullable
  private static MaterialId getMaterial(IToolStackView tool, Modifier modifier) {
    String material = tool.getPersistentData().getString(modifier.getId());
    if (!material.isEmpty()) {
      return MaterialId.tryParse(material);
    }
    return null;
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, @Nullable ItemLayerPixels pixels) {
    Material texture = textures[(isLarge ? 2 : 0) | (tool.isBroken() ? 1 : 0)];
    if (texture != null) {
      MutableObject<ImmutableList<BakedQuad>> mutable = new MutableObject<>();
      MaterialModel.getPartQuads(mutable::setValue, texture, spriteGetter, transforms, -1, getMaterial(tool, modifier.getModifier()), pixels);
      return mutable.getValue();
    }
    return ImmutableList.of();
  }

  /** Data class to cache a mateirla texture */
  private record CacheKey(Modifier modifier, String material) {}
}
