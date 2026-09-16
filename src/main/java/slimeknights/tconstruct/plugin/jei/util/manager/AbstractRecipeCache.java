package slimeknights.tconstruct.plugin.jei.util.manager;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Common logic for a recipe cache.
 * Functionally, needs the ability to compute a cached list of matching recipes for a given ingredient subtype,
 * and to filter that list with response to non-subtype ingredient data.
 * Designed for usage inside {@link mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin}.
 * @param <T>  Recipe class.
 * @param <I>  Ingredient type.
 */
@RequiredArgsConstructor
public abstract class AbstractRecipeCache<T,I> {
  protected final IIngredientHelper<I> helper;
  protected final List<? extends T> recipes;
  private final Map<Object, List<T>> cache = new HashMap<>();
  private final Function<Object, List<T>> computeRecipes = this::computeRecipes;

  /**
   * Checks if the given recipe matches the predicate. Used for cache building.
   * @param recipe     Recipe instance to test.
   * @param predicate  Predicate to test each ingredient.
   * @return  True if the recipe matches.
   */
  protected abstract boolean matches(T recipe, Predicate<I> predicate);

  /** Logic to compute the recipe list for the given cache key. */
  protected List<T> computeRecipes(Object cacheKey) {
    Predicate<I> predicate = stack -> cacheKey.equals(helper.getUid(stack, UidContext.Recipe));
    List<T> filtered = new ArrayList<>(recipes.size());
    for (T recipe : recipes) {
      if (matches(recipe, predicate)) {
        filtered.add(recipe);
      }
    }
    return List.copyOf(filtered);
  }

  /**
   * Finds and caches all recipes matching the focus.
   * @param focus  Focus
   * @return  List of matching recipes
   * @see #filterRecipes(I)
   */
  public List<T> matchingRecipes(I focus) {
    return cache.computeIfAbsent(helper.getUid(focus, UidContext.Recipe), computeRecipes);
  }

  /**
   * Filters the cached recipes to only those matching the focus.
   * @param focus  Focus
   * @return  List of matching recipes
   * @see #matchingRecipes(I)
   */
  public List<T> filterRecipes(I focus) {
    return matchingRecipes(focus);
  }
}
