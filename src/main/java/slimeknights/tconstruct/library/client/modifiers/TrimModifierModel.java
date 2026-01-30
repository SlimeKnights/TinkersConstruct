package slimeknights.tconstruct.library.client.modifiers;

import com.mojang.math.Transformation;
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
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.cosmetic.TrimModule;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model adding trim overlays to an item */
public enum TrimModifierModel implements IBakedModifierModel {
  INSTANCE;

  private record TrimTexture(@Nullable TextureAtlasSprite sprite, int color) {
    public static final TrimTexture EMPTY = new TrimTexture(null, -1);
  }

  /** Cache texture for each item to save registry lookups */
  @SuppressWarnings("unchecked")
  private static final Map<String,TrimTexture>[] TEXTURE_CACHE = new Map[4];
  public static final ResourceLocation[] TRIM_TEXTURES = new ResourceLocation[4];
  static {
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      TEXTURE_CACHE[type.ordinal()] = new HashMap<>();
      TRIM_TEXTURES[type.ordinal()] = new ResourceLocation("trims/items/" + type.getName() + "_trim");
    }
  }

  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    // if we are loading the model, then we are reloading resources
    // TODO: clear cache on datapack reload
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      TEXTURE_CACHE[type.ordinal()].clear();
    }
    return INSTANCE;
  };

  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    return tool.getPersistentData().getString(TrimModule.materialKey(modifier.getId()));
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    if (!isLarge) {
      String materialId = tool.getPersistentData().getString(TrimModule.materialKey(modifier.getId()));
      if (!materialId.isEmpty() && tool.getItem() instanceof ArmorItem armor) {
        Map<String,TrimTexture> cache = TEXTURE_CACHE[armor.getType().ordinal()];
        // start with the cache, saves us having to lookup the material again
        TrimTexture texture = cache.get(materialId);
        if (texture == null) {
          texture = TrimTexture.EMPTY;
          Level level = Minecraft.getInstance().level;
          if (level != null) {
            // find the material, if missing we use the base texture
            TrimMaterial material = level.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL).get(ResourceLocation.tryParse(materialId));
            if (material != null) {
              // base location is based on the armor type
              ResourceLocation root = TRIM_TEXTURES[armor.getType().ordinal()];
              // specific location based on the material
              ResourceLocation path = root.withSuffix("_" + material.assetName());

              // ensure the material sprite exists, if not we will tint the base sprite
              TextureAtlasSprite sprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, path));
              int color = -1;
              if (MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
                // if the sprite doesn't exist, will tint the base sprite, assuming we have a component color
                // helps for mods that don't properly provide all sprites
                sprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, root));
                TextColor textColor = material.description().getStyle().getColor();
                if (textColor != null) {
                  color = textColor.getValue() | 0xFF000000;
                }
                TConstruct.LOG.error("Missing material specific texture {}, defaulting to tinting base texture #{}", path, ColorLoadable.NO_ALPHA.getString(color));
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
}
