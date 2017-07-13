package slimeknights.tconstruct.plugin.jei.drying;

import java.util.ArrayList;
import java.util.List;

import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;

public class DryingRecipeChecker {
  public static List<DryingRecipe> getDryingRecipes() {
    List<DryingRecipe> recipes = new ArrayList<>();

    for(DryingRecipe recipe : TinkerRegistry.getAllDryingRecipes()) {
      if(recipe.output != null && recipe.input != null && recipe.input.getInputs() != null && recipe.input.getInputs().size() > 0) {
        recipes.add(recipe);
      }
    }

    return recipes;
  }
}
