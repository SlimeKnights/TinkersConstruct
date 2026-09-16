package slimeknights.tconstruct.plugin.jei.util;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.display.FilteredFluidRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/** Cache for searching for recipes from a list of {@link FilteredFluidRecipe} */
@RequiredArgsConstructor
public class FluidRecipeCache<T extends FilteredFluidRecipe> {
  private final IIngredientHelper<FluidStack> helper;
  private final List<? extends T> recipes;
  private final Map<Object, List<T>> cache = new HashMap<>();
  private final boolean output;

  /**
   * Finds and caches all recipes matching the focus.
   * @param focus  Focus
   * @return  List of matching recipes
   * @see #filterRecipes(FluidStack)
   */
  public List<T> matchingRecipes(FluidStack focus) {
    // first, check the cache for the recipe list
    Object cacheKey = helper.getUid(focus, UidContext.Recipe);
    List<T> cached = cache.get(cacheKey);
    if (cached != null) {
      return cached;
    }

    Predicate<FluidStack> predicate = stack -> cacheKey.equals(helper.getUid(stack, UidContext.Recipe));
    List<T> filtered = new ArrayList<>(recipes.size());
    for (T recipe : recipes) {
      if (recipe.matchesFluid(predicate, output)) {
        filtered.add(recipe);
      }
    }
    filtered = List.copyOf(filtered);
    cache.put(cacheKey, filtered);
    return filtered;
  }

  /**
   * Filters the cached recipes to only those matching the focus.
   * @param focus  Focus
   * @return  List of matching recipes
   * @see #matchingRecipes(FluidStack)
   */
  public List<T> filterRecipes(FluidStack focus) {
    List<T> recipes = matchingRecipes(focus);
    List<T> filtered = new ArrayList<>(recipes.size());
    for (T recipe : recipes) {
      if (recipe.isVisibleFromFluid(focus, output)) {
        filtered.add(recipe);
      }
    }
    return filtered;
  }
}
