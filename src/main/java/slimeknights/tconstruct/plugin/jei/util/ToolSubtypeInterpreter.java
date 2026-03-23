package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import java.util.List;

/**
 * Subtype interpreter for tools, treats the tool as unique in ingredient list, generic in recipes
 */
public enum ToolSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
  /** Materials are always considered */
  ALWAYS,
  /** Materials are only considered in the ingredients view */
  INGREDIENT,
  /** Materials are only considered in the ingredients view or the first material. */
  FIRST;

  @Override
  public String apply(ItemStack itemStack, UidContext context) {
    boolean isIngredient = context == UidContext.Ingredient;
    if (isIngredient || this != INGREDIENT) {
      List<MaterialVariantId> materialList = MaterialIdNBT.from(itemStack).getMaterials();
      if (!materialList.isEmpty()) {
        // if in first mode and looking at a recipe, only use the first material
        MaterialId first = materialList.get(0).getId();
        if (!isIngredient && this == FIRST) {
          return first.toString();
        }
        // append first entry without a comma
        StringBuilder builder = new StringBuilder();
        builder.append(first);
        for (int i = 1; i < materialList.size(); i++) {
          builder.append(',');
          builder.append(materialList.get(i).getId());
        }
        return builder.toString();
      }
    }
    return NONE;
  }
}
