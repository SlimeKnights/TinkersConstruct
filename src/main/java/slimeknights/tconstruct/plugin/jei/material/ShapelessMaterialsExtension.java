package slimeknights.tconstruct.plugin.jei.material;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.recipe.material.ShapelessMaterialsRecipe;

/** Logic to show {@link ShapelessMaterialsRecipe} in JEI. */
public class ShapelessMaterialsExtension extends MaterialsCraftingExtension<ShapelessMaterialsRecipe> {
  private ShapelessMaterialsExtension(ShapelessMaterialsRecipe recipe) {
    super(recipe);
  }

  @Override
  protected int[] getMaterialSlots(ShapelessMaterialsRecipe recipe, Ingredient firstPart) {
    return new int[] {0};
  }
}
