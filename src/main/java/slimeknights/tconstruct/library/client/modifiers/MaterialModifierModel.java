package slimeknights.tconstruct.library.client.modifiers;

import com.mojang.math.Transformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.SimpleModifierModel;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Model for a modifier that has variants based on a material
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.modifiers.modules}
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class MaterialModifierModel implements SimpleModifierModel {
  public static final RecordLoadable<MaterialModifierModel> LOADER = SimpleModifierModel.loader(MaterialModifierModel::new);
  /** Fetches relevant material textures after checking if the texture exists */
  @Nullable
  private static Material stitchMaterialTextures(Function<String,Material> textureGetter) {
    Material baseTexture = textureGetter.apply("");
    if (baseTexture != null) {
      for (MaterialRenderInfo info : MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos()) {
        ResourceLocation texture = info.texture();
        if (texture != null) {
          textureGetter.apply("_" + MaterialRenderInfo.getSuffix(texture));
        }
        for (String fallback : info.fallbacks()) {
          textureGetter.apply("_" + fallback);
        }
      }
    }
    return baseTexture;
  }

  /** @deprecated legacy system, use {@link #LOADER} */
  @Deprecated
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    Material smallTexture = stitchMaterialTextures(smallGetter);
    Material largeTexture = stitchMaterialTextures(largeGetter);
    if (smallTexture != null || largeTexture != null) {
      return new MaterialModifierModel(smallTexture, largeTexture);
    }
    return null;
  };

  @Nullable
  private final Material small;
  @Nullable
  private final Material large;

  @Override
  public RecordLoadable<? extends ModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    SimpleModifierModel.super.validate(spriteGetter);
    if (Config.CLIENT.logMissingMaterialTextures.get()) {
      for (MaterialRenderInfo info : MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos()) {
        if (small != null) info.getSprite(small, spriteGetter);
        if (large != null) info.getSprite(large, spriteGetter);
      }
    }
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    ModifierId modifier = entry.getId();
    return new CacheKey(modifier, tool.getPersistentData().getString(modifier));
  }

  @Nullable
  private static MaterialVariantId getMaterial(IToolStackView tool, Modifier modifier) {
    String material = tool.getPersistentData().getString(modifier.getId());
    if (!material.isEmpty()) {
      return MaterialVariantId.tryParse(material);
    }
    return null;
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    Material texture = isLarge ? large : small;
    if (texture != null) {
      MaterialVariantId material = getMaterial(tool, modifier.getModifier());
      if (material != null) {
        quadConsumer.accept(MaterialModel.getQuadsForMaterial(spriteGetter, texture, material, -1, transforms, pixels));
      }
    }
  }

  /** Data class to cache a mateirla texture */
  private record CacheKey(ModifierId modifier, String material) {}
}
