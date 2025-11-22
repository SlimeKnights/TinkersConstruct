package slimeknights.tconstruct.library.client.materials;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.data.listener.ISafeManagerReloadListener;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MaterialTooltipCache {
  /** Map of the key for each material variant */
  private static final Map<MaterialVariantId,String> KEY_CACHE = new HashMap<>();
  /** Map of the color for each material variant */
  private static final Map<MaterialVariantId,TextColor> COLOR_CACHE = new HashMap<>();
  /** Map of the key for each material variant */
  private static final Map<MaterialVariantId,Component> DISPLAY_NAME_CACHE = new HashMap<>();
  /** Map of the key for each material variant */
  private static final Map<MaterialVariantId,Component> COLORED_DISPLAY_NAME_CACHE = new HashMap<>();

  /** Clears all resource pack driven caches */
  private static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> {
    COLOR_CACHE.clear();
    DISPLAY_NAME_CACHE.clear();
    COLORED_DISPLAY_NAME_CACHE.clear();
  };

  private MaterialTooltipCache() {}

  /** Called during the event to initialize the cache invalidators */
  public static void init(RegisterClientReloadListenersEvent manager)  {
    manager.registerReloadListener(RELOAD_LISTENER);
  }

  /** Logic to convert a material ID to a string */
  private static final Function<MaterialVariantId,String> KEY_GETTER = id -> Util.makeTranslationKey("material", id.getLocation('.'));

  /** Gets the key name for a given material variant, prevents need to create a lot of extra strings */
  public static String getKey(MaterialVariantId variantId) {
    return KEY_CACHE.computeIfAbsent(variantId, KEY_GETTER);
  }

  /** Gets the display name for a component */
  private static final Function<MaterialVariantId,MutableComponent> DISPLAY_NAME_GETTER = id -> {
    if (id.hasVariant()) {
      String variantKey = getKey(id);
      // TODO 1.21: ditch can translate and assume variants must always translate
      if (Util.canTranslate(variantKey)) {
        return Component.translatable(variantKey);
      }
    }
    return Component.translatable(getKey(id.getId()));
  };

  /** Gets the key name for a given material variant, prevents need to create a lot of extra strings */
  public static Component getDisplayName(MaterialVariantId variantId) {
    return DISPLAY_NAME_CACHE.computeIfAbsent(variantId, DISPLAY_NAME_GETTER);
  }

  /** Getter for colors given a material variant ID */
  public static final Function<MaterialVariantId,TextColor> COLOR_GETTER = id -> {
    if (id.hasVariant()) {
      String variantKey = getKey(id);
      TextColor color = ResourceColorManager.getOrNull(variantKey);
      if (color != null) {
        return color;
      }
    }
    return ResourceColorManager.getTextColor(getKey(id.getId()));
  };

  /** Gets the color for a given material ID */
  public static TextColor getColor(MaterialVariantId id) {
    return COLOR_CACHE.computeIfAbsent(id, COLOR_GETTER);
  }

  /** Gets the display name for a component */
  private static final Function<MaterialVariantId,Component> COLORED_DISPLAY_NAME_GETTER = id -> {
    TextColor color = getColor(id);
    if ((color.getValue() & 0xFFFFFF) != 0xFFFFFF) {
      return DISPLAY_NAME_GETTER.apply(id).withStyle(style -> style.withColor(color));
    }
    // no color? Use the already cached white component
    return getDisplayName(id);
  };

  /** Gets the key name for a given material variant, prevents need to create a lot of extra strings */
  public static Component getColoredDisplayName(MaterialVariantId variantId) {
    return COLORED_DISPLAY_NAME_CACHE.computeIfAbsent(variantId, COLORED_DISPLAY_NAME_GETTER);
  }
}
