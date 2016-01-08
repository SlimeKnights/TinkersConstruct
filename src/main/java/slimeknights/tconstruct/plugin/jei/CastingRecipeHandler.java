package slimeknights.tconstruct.plugin.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;

public class CastingRecipeHandler implements IRecipeHandler<CastingRecipeWrapper> {

  @Nonnull
  @Override
  public Class<CastingRecipeWrapper> getRecipeClass() {
    return CastingRecipeWrapper.class;
  }

  @Nonnull
  @Override
  public String getRecipeCategoryUid() {
    return CastingRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull CastingRecipeWrapper recipe) {
    return recipe;
  }

  @Override
  public boolean isRecipeValid(@Nonnull CastingRecipeWrapper recipe) {
    return !recipe.inputFluid.isEmpty() &&
           recipe.inputFluid.get(0) != null &&
           (recipe.cast.isEmpty() || recipe.cast.get(0) != null) &&
           (recipe.output != null && !recipe.output.isEmpty() && recipe.output.get(0) != null);
  }
}
