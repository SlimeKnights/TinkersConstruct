package slimeknights.tconstruct.library.client.armor.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.modifiers.slotless.TrimModifier;

import java.util.HashMap;
import java.util.Map;

/** Handles fetching textures for armor trims */
public enum TrimArmorTextureSupplier implements ArmorTextureSupplier {
  INSTANCE;

  private final SingletonLoader<TrimArmorTextureSupplier> LOADER = new SingletonLoader<>(this);
  private static final Map<String,ArmorTexture> ARMOR_CACHE = new HashMap<>();
  private static final Map<String,ArmorTexture> LEGGING_CACHE = new HashMap<>();
  /** Listener to clear caches associated with trim textures */
  public static final ResourceManagerReloadListener CACHE_INVALIDATOR = manager -> {
    ARMOR_CACHE.clear();
    LEGGING_CACHE.clear();
    TrimArmorTexture.armorTrimAtlas = null;
  };

  @Override
  public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType, RegistryAccess access) {
    if (textureType != TextureType.WINGS) {
      String patternId = ModifierUtil.getPersistentString(stack, TrimModifier.TRIM_PATTERN);
      String materialId = ModifierUtil.getPersistentString(stack, TrimModifier.TRIM_MATERIAL);
      if (!patternId.isEmpty() && !materialId.isEmpty()) {
        String key = patternId + '#' + materialId;
        Map<String,ArmorTexture> cache = textureType == TextureType.LEGGINGS ? LEGGING_CACHE : ARMOR_CACHE;
        ArmorTexture texture = cache.get(key);
        if (texture != null) {
          return texture;
        }
        TrimPattern pattern = access.registryOrThrow(Registries.TRIM_PATTERN).get(ResourceLocation.tryParse(patternId));
        TrimMaterial material = access.registryOrThrow(Registries.TRIM_MATERIAL).get(ResourceLocation.tryParse(materialId));
        texture = ArmorTexture.EMPTY;
        if (pattern != null && material != null) {
          ResourceLocation patternAsset = pattern.assetId();
          texture = new TrimArmorTexture(patternAsset.withPath("trims/models/armor/" + patternAsset.getPath() + (textureType == TextureType.LEGGINGS ? "_leggings_" : "_") + material.assetName()));

        }
        cache.put(key, texture);
        return texture;
      }
    }
    return ArmorTexture.EMPTY;
  }

  @Override
  public SingletonLoader<TrimArmorTextureSupplier> getLoader() {
    return LOADER;
  }

  /** Implementation of an armor texture for armor trims */
  @RequiredArgsConstructor
  public static class TrimArmorTexture implements ArmorTexture {
    private static TextureAtlas armorTrimAtlas = null;
    private final ResourceLocation trimLocation;
    private TextureAtlasSprite trimSprite = null;

    /** Gets the texture atlas for trim */
    private static TextureAtlas getTrimAtlas() {
      if (armorTrimAtlas == null) {
        armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
      }
      return armorTrimAtlas;
    }

    /** Gets the trim texture sprite */
    private TextureAtlasSprite getSprite() {
      if (trimSprite == null) {
        trimSprite = getTrimAtlas().getSprite(trimLocation);
      }
      return trimSprite;
    }

    @Override
    public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint) {
      // ignoring glint as odds are very low trim texture is the first one
      VertexConsumer buffer = getSprite().wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet()));
      model.renderToBuffer(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
  }
}
