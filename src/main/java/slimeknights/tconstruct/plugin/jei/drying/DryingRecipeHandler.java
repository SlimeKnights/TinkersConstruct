package slimeknights.tconstruct.plugin.jei.drying;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import slimeknights.tconstruct.library.DryingRecipe;

public class DryingRecipeHandler implements IRecipeWrapperFactory<DryingRecipe> {
  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull DryingRecipe recipe) {
    return new DryingRecipeWrapper(recipe);
  }
}
