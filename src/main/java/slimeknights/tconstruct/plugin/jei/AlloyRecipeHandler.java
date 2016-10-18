package slimeknights.tconstruct.plugin.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class AlloyRecipeHandler implements IRecipeHandler<AlloyRecipe> {

  @Nonnull
  @Override
  public Class<AlloyRecipe> getRecipeClass() {
    return AlloyRecipe.class;
  }

  @Nonnull
  @Override
  @Deprecated
  public String getRecipeCategoryUid() {
    return AlloyRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public String getRecipeCategoryUid(@Nonnull AlloyRecipe recipe) {
    return AlloyRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull AlloyRecipe recipe) {
    return new AlloyRecipeWrapper(recipe);
  }

  @Override
  public boolean isRecipeValid(@Nonnull AlloyRecipe recipe) {
    return recipe.getFluids() != null
           && recipe.getFluids().size() > 0
           && recipe.getResult() != null
           && recipe.getResult().amount > 0;
  }
}
