package slimeknights.tconstruct.library.client.modifiers;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modifiers.slotless.TrimModifier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model adding trim overlays to an item */
public enum TrimModifierModel implements IBakedModifierModel {
  INSTANCE;

  /** Cache texture for each item to save registry lookups */
  @SuppressWarnings("unchecked")
  private static final Map<String,Material>[] TEXTURE_CACHE = new Map[4];
  static {
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      TEXTURE_CACHE[type.ordinal()] = new HashMap<>();
    }
  }

  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    // if we are loading the model, then we are reloading resources
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      TEXTURE_CACHE[type.ordinal()].clear();
    }
    return INSTANCE;
  };

  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    return tool.getPersistentData().getString(TrimModifier.TRIM_MATERIAL);
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    if (!isLarge) {
      String materialId = tool.getPersistentData().getString(TrimModifier.TRIM_MATERIAL);
      if (!materialId.isEmpty() && tool.getItem() instanceof ArmorItem armor) {
        Map<String,Material> cache = TEXTURE_CACHE[armor.getType().ordinal()];
        Material texture = cache.get(materialId);
        if (texture == null) {
          ResourceLocation path = new ResourceLocation("trims/items/" + armor.getType().getName() + "_trim");
          Level level = Minecraft.getInstance().level;
          if (level != null) {
            TrimMaterial material = level.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL).get(ResourceLocation.tryParse(materialId));
            if (material != null) {
              path = path.withSuffix("_" + material.assetName());
            }
          }
          texture = new Material(InventoryMenu.BLOCK_ATLAS, path);
          cache.put(materialId, texture);
        }

        // at this point guaranteed to have a texture, add the quads
        quadConsumer.accept(MantleItemLayerModel.getQuadsForSprite(-1, -1, spriteGetter.apply(texture), transforms, 0, pixels));
      }
    }
  }
}
