package slimeknights.tconstruct.plugin.jei.partbuilder;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Allows getting a list of items for display for a given material
 */
public class MaterialItemList {
  private static List<MaterialRecipe> RECIPE_LIST = Collections.emptyList();

  /** Material recipes */
  private static final Map<MaterialId,List<ItemStack>> ITEM_LISTS = new HashMap<>();

  /**
   * Sets the list of recipes
   * @param recipes  Recipes
   */
  public static void setRecipes(List<MaterialRecipe> recipes) {
    RECIPE_LIST = recipes.stream().filter(r -> r.getMaterial() != IMaterial.UNKNOWN).collect(Collectors.toList());
    ITEM_LISTS.clear();
  }

  /**
   * Gets a list of items
   * @param material  Materials
   * @return  List of items
   */
  public static List<ItemStack> getItems(MaterialId material) {
    List<ItemStack> list = ITEM_LISTS.get(material);
    if (list == null) {
      ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
      for (MaterialRecipe recipe : RECIPE_LIST) {
        if (material.equals(recipe.getMaterialId())) {
          builder.addAll(recipe.getDisplayItems());
        }
      }
      list = builder.build();
      ITEM_LISTS.put(material, list);
    }
    return list;
  }
}
