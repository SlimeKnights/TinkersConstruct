package slimeknights.tconstruct.plugin.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class SmeltingRecipeHandler implements IRecipeHandler<MeltingRecipe> {

  @Nonnull
  @Override
  public Class<MeltingRecipe> getRecipeClass() {
    return MeltingRecipe.class;
  }

  @Nonnull
  @Override
  @Deprecated
  public String getRecipeCategoryUid() {
    return SmeltingRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public String getRecipeCategoryUid(@Nonnull MeltingRecipe recipe) {
    return SmeltingRecipeCategory.CATEGORY;
  }

  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull MeltingRecipe recipe) {
    return new SmeltingRecipeWrapper(recipe);
  }

  @Override
  public boolean isRecipeValid(@Nonnull MeltingRecipe recipe) {
    return recipe.output != null &&
           recipe.input != null &&
           recipe.input.getInputs() != null &&
           recipe.input.getInputs().size() > 0;
  }
}
