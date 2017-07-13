package slimeknights.tconstruct.plugin.jei.alloy;

import java.util.ArrayList;
import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class AlloyRecipeChecker {
  public static List<AlloyRecipe> getAlloyRecipes() {
    List<AlloyRecipe> recipes = new ArrayList<>();

    for(AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
      if(recipe.getFluids() != null && recipe.getFluids().size() > 0 && recipe.getResult() != null && recipe.getResult().amount > 0) {
        recipes.add(recipe);
      }
    }

    return recipes;
  }
}
