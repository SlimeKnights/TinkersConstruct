package slimeknights.tconstruct.plugin.jei.util.manager;

import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.display.FilteredFluidRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Cache for searching for recipes from a list of {@link FilteredFluidRecipe}. */
public class FluidRecipeCache<T extends FilteredFluidRecipe> extends AbstractRecipeCache<T, FluidStack> {
  private final boolean output;
  public FluidRecipeCache(IIngredientHelper<FluidStack> helper, List<? extends T> recipes, boolean output) {
    super(helper, recipes);
    this.output = output;
  }

  @Override
  protected boolean matches(T recipe, Predicate<FluidStack> predicate) {
    return recipe.matchesFluid(predicate, this.output);
  }

  @Override
  public List<T> filterRecipes(FluidStack focus) {
    List<T> recipes = matchingRecipes(focus);
    if (recipes.isEmpty()) {
      return List.of();
    }
    List<T> filtered = new ArrayList<>(recipes.size());
    for (T recipe : recipes) {
      if (recipe.isVisibleFromFluid(focus, output)) {
        filtered.add(recipe);
      }
    }
    return filtered;
  }
}
