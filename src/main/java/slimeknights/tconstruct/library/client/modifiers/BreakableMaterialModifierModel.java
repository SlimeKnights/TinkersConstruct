package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import org.apache.commons.lang3.mutable.MutableObject;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.Function;

/** Model for a modifier that has variants based on a material and the tool's broken state */
public class BreakableMaterialModifierModel implements IBakedModifierModel {
  /** Fetches relevant material textures after checking if the texture exists */
  @Nullable
  private static RenderMaterial stitchMaterialTextures(Function<String,RenderMaterial> textureGetter, String name) {
    RenderMaterial baseTexture = textureGetter.apply(name);
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
    RenderMaterial smallTexture = stitchMaterialTextures(smallGetter, "");
    RenderMaterial brokenSmall = stitchMaterialTextures(smallGetter, "_broken");
    RenderMaterial largeTexture = stitchMaterialTextures(largeGetter, "");
    RenderMaterial brokenLarge = stitchMaterialTextures(largeGetter, "_broken");
    if (smallTexture != null || brokenSmall != null || largeTexture != null || brokenLarge != null) {
      return new BreakableMaterialModifierModel(smallTexture, brokenSmall, largeTexture, brokenLarge);
    }
    return null;
  };

  /** Textures to show */
  private final RenderMaterial[] textures;

  public BreakableMaterialModifierModel(@Nullable RenderMaterial normalSmall, @Nullable RenderMaterial brokenSmall, @Nullable RenderMaterial normalLarge, @Nullable RenderMaterial brokenLarge) {
    this.textures = new RenderMaterial[]{normalSmall, brokenSmall, normalLarge, brokenLarge};
  }

  @Nullable
  @Override
  public Object getCacheKey(IModifierToolStack tool, ModifierEntry entry) {
    Modifier modifier = entry.getModifier();
    return new CacheKey(modifier, tool.getPersistentData().getString(modifier.getId()));
  }

  @Nullable
  private static MaterialId getMaterial(IModifierToolStack tool, Modifier modifier) {
    String material = tool.getPersistentData().getString(modifier.getId());
    if (!material.isEmpty()) {
      return MaterialId.tryCreate(material);
    }
    return null;
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry modifier, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge) {
    return getQuads(tool, modifier, spriteGetter, transforms, isLarge, -1, null);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry modifier, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge, int startTintIndex, @Nullable ItemLayerPixels pixels) {
    RenderMaterial texture = textures[(isLarge ? 2 : 0) | (tool.isBroken() ? 1 : 0)];
    if (texture != null) {
      MutableObject<ImmutableList<BakedQuad>> mutable = new MutableObject<>();
      MaterialModel.getPartQuads(mutable::setValue, texture, spriteGetter, transforms, -1, getMaterial(tool, modifier.getModifier()), pixels);
      return mutable.getValue();
    }
    return ImmutableList.of();
  }

  /** Data class to cache a mateirla texture */
  @Data
  private static class CacheKey {
    private final Modifier modifier;
    private final String material;
  }
}
