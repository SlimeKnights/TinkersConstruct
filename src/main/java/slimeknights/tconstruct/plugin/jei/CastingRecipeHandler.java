package slimeknights.tconstruct.plugin.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CastingRecipeHandler implements IRecipeHandler<CastingRecipeWrapper> {

  @Nonnull
  @Override
  public Class<CastingRecipeWrapper> getRecipeClass() {
    return CastingRecipeWrapper.class;
  }

  @Nonnull
  @Override
  @Deprecated
  public String getRecipeCategoryUid() {
    return CastingRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public String getRecipeCategoryUid(@Nonnull CastingRecipeWrapper recipe) {
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
           (!recipe.hasCast() || (!recipe.cast.isEmpty() && recipe.cast.get(0) != null)) &&
           (recipe.output != null && !recipe.output.isEmpty() && recipe.output.get(0) != null);
  }
}
