package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import java.util.List;

/**
 * Subtype interpreter for tools, treats the tool as unique in ingredient list, generic in recipes
 */
public enum ToolSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
  ALWAYS, INGREDIENT;

  @Override
  public String apply(ItemStack itemStack, UidContext context) {
    if (this == ALWAYS || context == UidContext.Ingredient) {
      List<MaterialVariantId> materialList = MaterialIdNBT.from(itemStack).getMaterials();
      if (!materialList.isEmpty()) {
        // append first entry without a comma
        StringBuilder builder = new StringBuilder();
        builder.append(materialList.get(0).getId());
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
