package slimeknights.tconstruct.plugin.jei.material;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.recipe.material.ShapedMaterialsRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

/** Logic to show {@link ShapedMaterialsRecipe} in JEI */
public class ShapedMaterialsExtension extends MaterialsCraftingExtension<ShapedMaterialsRecipe> {
  private ShapedMaterialsExtension(ShapedMaterialsRecipe recipe) {
    super(recipe);
  }

  /** {@return Instance of the shaped extension, or null if the recipe is invalid for display} */
  @Nullable
  public static ShapedMaterialsExtension create(ShapedMaterialsRecipe recipe) {
    for (Ingredient ingredient : recipe.getParts()) {
      if (ingredient.getItems().length == 0) {
        return null;
      }
    }
    return new ShapedMaterialsExtension(recipe);
  }

  @Override
  protected int[] getMaterialSlots(ShapedMaterialsRecipe recipe, Ingredient firstPart) {
    List<Ingredient> inputs = recipe.getIngredients();
    return IntStream.range(0, inputs.size()).filter(i -> inputs.get(i) == firstPart).toArray();
  }

  @Override
  public int getWidth() {
    return recipe.getWidth();
  }

  @Override
  public int getHeight() {
    return recipe.getHeight();
  }
}
