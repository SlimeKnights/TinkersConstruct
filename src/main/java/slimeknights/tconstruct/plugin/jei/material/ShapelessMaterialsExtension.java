package slimeknights.tconstruct.plugin.jei.material;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.tconstruct.library.recipe.material.ShapelessMaterialsRecipe;

import java.util.List;

/** Logic to show {@link ShapelessMaterialsRecipe} in JEI */
public class ShapelessMaterialsExtension extends MaterialsCraftingExtension<ShapelessMaterialsRecipe> {
  private final int gridSize;
  ShapelessMaterialsExtension(ShapelessMaterialsRecipe recipe) {
    super(recipe);
    gridSize = getShapelessSize(recipe.getIngredients().size());
  }

  /** Gets the material slots for the given recipe */
  @Override
  protected int[] getMaterialSlots(ShapelessMaterialsRecipe recipe, Ingredient part) {
    List<Ingredient> ingredients = recipe.getIngredients();
    // map the given part to the index it will show in the real grid
    return new int[] { MantleJEIConstants.getCraftingIndex(ingredients.indexOf(part), gridSize, gridSize) };
  }
}
