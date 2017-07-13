package slimeknights.tconstruct.plugin.jei.casting;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class CastingRecipeHandler implements IRecipeWrapperFactory<CastingRecipeWrapper> {
  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull CastingRecipeWrapper recipe) {
    return recipe;
  }
}
