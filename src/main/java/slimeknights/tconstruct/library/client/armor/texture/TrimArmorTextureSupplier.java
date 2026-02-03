package slimeknights.tconstruct.library.client.armor.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.cosmetic.TrimModule;

import java.util.HashMap;
import java.util.Map;

/** Handles fetching textures for armor trims */
public record TrimArmorTextureSupplier(ModifierId modifier, ResourceLocation patternKey, ResourceLocation materialKey) implements ArmorTextureSupplier {
  /** Default instant using the tinkers modifier */
  public static TrimArmorTextureSupplier INSTANCE = new TrimArmorTextureSupplier(TinkerModifiers.trim.getId());
  public static final RecordLoadable<TrimArmorTextureSupplier> LOADER = RecordLoadable.create(ModifierId.PARSER.defaultField("modifier", TinkerModifiers.trim.getId(), TrimArmorTextureSupplier::modifier), TrimArmorTextureSupplier::new);

  /* Caches */
  private static final Map<String,ArmorTexture> ARMOR_CACHE = new HashMap<>();
  private static final Map<String,ArmorTexture> LEGGING_CACHE = new HashMap<>();
  /** Listener to clear caches associated with trim textures */
  public static final ResourceManagerReloadListener CACHE_INVALIDATOR = manager -> {
    ARMOR_CACHE.clear();
    LEGGING_CACHE.clear();
    TrimArmorTexture.armorTrimAtlas = null;
  };

  /** @apiNote use {@link #TrimArmorTextureSupplier(ModifierId)} */
  @Internal
  public TrimArmorTextureSupplier {}

  public TrimArmorTextureSupplier(ModifierId modifier) {
    this(modifier, TrimModule.patternKey(modifier), TrimModule.materialKey(modifier));
  }

  @Override
  public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType, RegistryAccess access) {
    if (textureType != TextureType.WINGS) {
      String patternId = ModifierUtil.getPersistentString(stack, patternKey);
      String materialId = ModifierUtil.getPersistentString(stack, materialKey);
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
          texture = TrimArmorTexture.create(patternAsset.withPath("trims/models/armor/" + patternAsset.getPath() + (textureType == TextureType.LEGGINGS ? "_leggings" : "")), material);
        }
        cache.put(key, texture);
        return texture;
      }
    }
    return ArmorTexture.EMPTY;
  }

  @Override
  public RecordLoadable<TrimArmorTextureSupplier> getLoader() {
    return LOADER;
  }

  /** Implementation of an armor texture for armor trims */
  @RequiredArgsConstructor
  public static class TrimArmorTexture implements ArmorTexture {
    private static TextureAtlas armorTrimAtlas = null;
    private final TextureAtlasSprite trimSprite;

    /** Gets the texture atlas for trim */
    private static TextureAtlas getTrimAtlas() {
      if (armorTrimAtlas == null) {
        armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
      }
      return armorTrimAtlas;
    }

    /** Creates the trim texture for the given root texture and material */
    private static ArmorTexture create(ResourceLocation root, TrimMaterial material) {
      // start by trying and finding the material specific sprite
      ResourceLocation withMaterial = root.withSuffix('_' + material.assetName());
      TextureAtlasSprite sprite = getTrimAtlas().getSprite(withMaterial);
      if (!MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
        return new TrimArmorTexture(sprite);
      }
      // failed to find the unique sprite, go for tinting the base
      int color = -1;
      TextColor textColor = material.description().getStyle().getColor();
      if (textColor != null) {
        color = textColor.getValue() | 0xFF000000;
      }
      TConstruct.LOG.error("Missing material specific texture {}, defaulting to tinting base texture #{}", withMaterial, ColorLoadable.NO_ALPHA.getString(color));
      return new TintedArmorTexture(root.withPath("textures/" + root.getPath() + ".png"), color);
    }

    @Override
    public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint) {
      // ignoring glint as odds are very low trim texture is the first one
      VertexConsumer buffer = trimSprite.wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet()));
      model.renderToBuffer(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
  }
}
