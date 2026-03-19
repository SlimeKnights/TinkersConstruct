package slimeknights.tconstruct.library.client.modifiers.model;

import com.mojang.math.Transformation;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.mapping.SimpleRecordLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.cosmetic.TrimModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/** Model for rendering the trim modifier on items. */
@SuppressWarnings("removal")
public interface TrimModifierModel extends ModifierModel {
  /** Cached texture for a material */
  record TrimTexture(@Nullable TextureAtlasSprite sprite, int color) {
    public static final TrimTexture EMPTY = new TrimTexture(null, -1);
  }

  @Nullable
  @Override
  default Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    String key = tool.getPersistentData().getString(TrimModule.materialKey(modifier.getId()));
    if (key.isEmpty()) {
      return null;
    }
    return key;
  }

  /** Gets the cache for the given tool */
  @Nullable
  Map<String, TrimTexture> getCache(boolean isLarge);

  /** Gets the root texture for the given tool */
  ResourceLocation getRoot(boolean isLarge);

  /** If true, missing textures warn. If false, missing textures log to debug. */
  boolean warnOnMissingTexture();

  @Override
  default void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    // material must be set
    String materialId = tool.getPersistentData().getString(TrimModule.materialKey(modifier.getId()));
    if (!materialId.isEmpty()) {
      // cache must exist. May not in deprecated model for non-empty, or in regular for large tools
      Map<String, TrimTexture> cache = getCache(isLarge);
      if (cache != null) {
        // start with the cache, saves us having to look up the material and sprite again
        TrimTexture texture = cache.get(materialId);
        if (texture == null) {
          texture = TrimTexture.EMPTY;
          Level level = Minecraft.getInstance().level;
          if (level != null) {
            // find the material, if missing we use the base texture
            TrimMaterial material = level.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL).get(ResourceLocation.tryParse(materialId));
            if (material != null) {
              // base location is based on the armor type
              ResourceLocation root = getRoot(isLarge);
              // specific location based on the material
              ResourceLocation path = root.withSuffix("_" + material.assetName());

              // ensure the material sprite exists, if not we will tint the base sprite
              TextureAtlasSprite sprite = spriteGetter.apply(ModifierModel.blockAtlas(path));
              int color = -1;
              if (MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
                // if the sprite doesn't exist, will tint the base sprite, assuming we have a component color
                // helps for mods that don't properly provide all sprites
                sprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, root));
                TextColor textColor = material.description().getStyle().getColor();
                if (textColor != null) {
                  color = textColor.getValue() | 0xFF000000;
                }
                TConstruct.LOG.log(warnOnMissingTexture() ? org.apache.logging.log4j.Level.WARN : org.apache.logging.log4j.Level.DEBUG,
                  "Missing material specific texture {}, defaulting to tinting base texture #{}", path, ColorLoadable.NO_ALPHA.getString(color));
              }
              texture = new TrimTexture(sprite, color);
            }
            // cache the texture, has the effect of caching empty if the material was missing
            cache.put(materialId, texture);
          }
        }
        // no texture here mean the material is unknown, otherwise add it
        if (texture.sprite != null) {
          quadConsumer.accept(MantleItemLayerModel.getQuadsForSprite(texture.color, -1, texture.sprite, transforms, 0, pixels));
        }
      }
    }
  }

  enum Armor implements TrimModifierModel {
    HELMET(ArmorItem.Type.HELMET),
    CHESTPLATE(ArmorItem.Type.CHESTPLATE),
    LEGGINGS(ArmorItem.Type.LEGGINGS),
    BOOTS(ArmorItem.Type.BOOTS);

    public static final RecordLoadable<Armor> LOADER = new SimpleRecordLoadable<>(new EnumLoadable<>(Armor.class), "slot", null, false);

    @Getter
    private final ResourceLocation root;
    private final Map<String, TrimTexture> cache;
    Armor(ArmorItem.Type type) {
      root = new ResourceLocation("trims/items/" + type.getName() + "_trim");
      cache = new HashMap<>();
    }

    @Override
    public RecordLoadable<Armor> getLoader() {
      return LOADER;
    }

    @Override
    public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
      spriteGetter.apply(ModifierModel.blockAtlas(root));
      // clear the cache as this is the best spot called post reload to reset it
      cache.clear();
    }

    @Nullable
    @Override
    public Map<String, TrimTexture> getCache(boolean isLarge) {
      return isLarge ? null : cache;
    }

    @Override
    public ResourceLocation getRoot(boolean isLarge) {
      return root;
    }

    @Override
    public boolean warnOnMissingTexture() {
      return true;
    }

    /** @apiNote callback for the legacy implementation of trim models */
    @Internal
    public void clearCache() {
      cache.clear();
    }
  }

  /** Trim model using a unique texture */
  class Custom implements TrimModifierModel {
    public static final RecordLoadable<Custom> LOADER = RecordLoadable.create(
      Loadables.RESOURCE_LOCATION.requiredField("root", m -> m.smallRoot),
      Loadables.RESOURCE_LOCATION.nullableField("root_large", m -> m.largeCache != null ? m.largeRoot : null),
      Custom::new);

    /** Base texture for this model */
    @Nonnull
    private final ResourceLocation smallRoot, largeRoot;
    /** Cache of textures for each material. */
    private final Map<String, TrimTexture> smallCache, largeCache ;

    /** Creates a model using the given custom texture and a unique cache. */
    public Custom(ResourceLocation smallRoot, @Nullable ResourceLocation largeRoot) {
      this.smallRoot = smallRoot;
      this.smallCache = new HashMap<>();
      // if we have a large root, create a cache for it
      if (largeRoot != null) {
        this.largeRoot = largeRoot;
        this.largeCache  = new HashMap<>();
      } else {
        // if we lack a large root, null means unsupported
        this.largeRoot = smallRoot;
        this.largeCache = null;
      }
    }

    @Override
    public RecordLoadable<? extends Custom> getLoader() {
      return LOADER;
    }

    @Nullable
    @Override
    public Map<String, TrimTexture> getCache(boolean isLarge) {
      return isLarge ? largeCache : smallCache;
    }

    @Override
    public ResourceLocation getRoot(boolean isLarge) {
      return isLarge ? largeRoot : smallRoot;
    }

    @Override
    public boolean warnOnMissingTexture() {
      return false;
    }

    @Override
    public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
      spriteGetter.apply(ModifierModel.blockAtlas(smallRoot));
      if (largeCache != null) {
        spriteGetter.apply(ModifierModel.blockAtlas(largeRoot));
      }
    }
  }
}
