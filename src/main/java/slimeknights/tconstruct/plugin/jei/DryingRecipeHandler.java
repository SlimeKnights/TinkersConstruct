package slimeknights.tconstruct.plugin.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.DryingRecipe;

public class DryingRecipeHandler implements IRecipeHandler<DryingRecipe> {

  @Nonnull
  @Override
  public Class<DryingRecipe> getRecipeClass() {
    return DryingRecipe.class;
  }

  @Nonnull
  @Override
  @Deprecated
  public String getRecipeCategoryUid() {
    return DryingRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public String getRecipeCategoryUid(@Nonnull DryingRecipe recipe) {
    return DryingRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull DryingRecipe recipe) {
    return new DryingRecipeWrapper(recipe);
  }

  @Override
  public boolean isRecipeValid(@Nonnull DryingRecipe recipe) {
    return recipe.output != null &&
           recipe.input != null &&
           recipe.input.getInputs() != null &&
           recipe.input.getInputs().size() > 0;
  }
}
