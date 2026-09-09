package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

/**
 * Subtype interpreter for material items, considers variants in ingredient context but ignores them in recipe context.
 */
public enum ToolPartSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
  INSTANCE;

  @Override
  public String apply(ItemStack stack, UidContext context) {
    MaterialVariantId materialId = IMaterialItem.getMaterialFromStack(stack);
    if (materialId.equals(MaterialId.UNKNOWN)) {
      return IIngredientSubtypeInterpreter.NONE;
    }
    if (context == UidContext.Ingredient) {
      return materialId.toString();
    }
    return materialId.getId().toString();
  }
}
