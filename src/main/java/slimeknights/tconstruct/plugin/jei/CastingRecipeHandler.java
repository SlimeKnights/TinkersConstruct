package slimeknights.tconstruct.plugin.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;

public class CastingRecipeHandler implements IRecipeHandler<CastingRecipe> {

  @Nonnull
  @Override
  public Class<CastingRecipe> getRecipeClass() {
    return CastingRecipe.class;
  }

  @Nonnull
  @Override
  public String getRecipeCategoryUid() {
    return CastingRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull CastingRecipe recipe) {
    return new CastingRecipeWrapper(recipe);
  }

  @Override
  public boolean isRecipeValid(@Nonnull CastingRecipe recipe) {
    return recipe.fluid != null &&
           recipe.fluid.amount > 0 &&
           recipe.output != null;
  }
}
