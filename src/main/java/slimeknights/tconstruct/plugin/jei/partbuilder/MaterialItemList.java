package slimeknights.tconstruct.plugin.jei.partbuilder;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows getting a list of items for display for a given material
 */
public class MaterialItemList {
  /** Material recipes */
  private static final Map<MaterialId,List<ItemStack>> ITEM_LISTS = new HashMap<>();

  /**
   * @apiNote Called internally by the plugin, no need for addons to call.
   */
  @SuppressWarnings("unused")
  @Internal
  public static void setRecipes(List<MaterialRecipe> recipes) {
    ITEM_LISTS.clear();
  }

  /**
   * Gets a list of items
   * @param variant  Material to show.
   * @return  List of items
   * @deprecated use {@link MaterialRecipeCache#getItems(MaterialVariantId)}. Note a small behavior difference as {@code MaterialRecipeCache} does not combine all variants into a root material.
   */
  @Deprecated(forRemoval = true)
  public static List<ItemStack> getItems(MaterialVariantId variant) {
    // if its a variant, we can just use the new lookup
    if (!variant.getVariant().isEmpty()) {
      return MaterialRecipeCache.getItems(variant);
    }
    // if its a base material; the new lookup won't show all children, but this method did
    MaterialId material = variant.getId();
    List<ItemStack> list = ITEM_LISTS.get(material);
    if (list == null) {
      list = MaterialRecipeCache.getVariants(material.getId()).stream().flatMap(v -> MaterialRecipeCache.getItems(v).stream()).toList();
      ITEM_LISTS.put(material, list);
    }
    return list;
  }
}
