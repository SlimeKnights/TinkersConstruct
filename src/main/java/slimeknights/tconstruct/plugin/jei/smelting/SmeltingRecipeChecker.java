package slimeknights.tconstruct.plugin.jei.smelting;

import java.util.ArrayList;
import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class SmeltingRecipeChecker {
  public static List<MeltingRecipe> getSmeltingRecipes() {
    List<MeltingRecipe> recipes = new ArrayList<>();

    for(MeltingRecipe recipe : TinkerRegistry.getAllMeltingRecipies()) {
      if(recipe.output != null && recipe.input != null && recipe.input.getInputs() != null && recipe.input.getInputs().size() > 0) {
        recipes.add(recipe);
      }
    }

    return recipes;
  }
}
