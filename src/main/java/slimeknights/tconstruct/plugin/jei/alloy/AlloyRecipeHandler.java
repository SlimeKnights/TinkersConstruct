package slimeknights.tconstruct.plugin.jei.alloy;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class AlloyRecipeHandler implements IRecipeWrapperFactory<AlloyRecipe> {
  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull AlloyRecipe recipe) {
    return new AlloyRecipeWrapper(recipe);
  }
}
