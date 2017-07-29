package slimeknights.tconstruct.plugin.jei.smelting;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class SmeltingRecipeHandler implements IRecipeWrapperFactory<MeltingRecipe> {
  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull MeltingRecipe recipe) {
    return new SmeltingRecipeWrapper(recipe);
  }
}
