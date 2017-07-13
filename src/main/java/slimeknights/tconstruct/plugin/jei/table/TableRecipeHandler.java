package slimeknights.tconstruct.plugin.jei.table;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import slimeknights.tconstruct.tools.common.TableRecipeFactory.TableRecipe;

public class TableRecipeHandler implements IRecipeWrapperFactory<TableRecipe> {
  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull TableRecipe recipe) {
    return new TableRecipeWrapper(recipe);
  }
}
