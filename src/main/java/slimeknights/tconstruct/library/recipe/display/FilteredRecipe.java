package slimeknights.tconstruct.library.recipe.display;

import java.util.List;
import java.util.function.Predicate;

/**
 * Common interface for all filtered recipe interfaces.
 * Filtered recipes work using JEI's recipe manager plugin system, which allows dynamically deciding which recipes to return.
 * We filter any recipes where {@link #isFiltered()} is true to be handled by a custom manager, leaving false to the standard recipe manager.
 * <br>
 * To implement this on a new recipe type, it is necessary to reimplement the ingredient lookup caches for ALL ingredient types, including custom types. See the relevant utilities to help.
 * @see FilteredItemRecipe
 * @see FilteredFluidRecipe
 * @see slimeknights.tconstruct.plugin.jei.util.manager.AbstractRecipeCache
 */
public interface FilteredRecipe {
  /** If true, this recipe is handled via a custom recipe manager in JEI to allow hiding based on focus NBT. */
  default boolean isFiltered() {
    return !showUnfocused();
  }

  /** If true (default), this recipe will show without a focus. If false, it only shows when focused. */
  default boolean showUnfocused() {
    return true;
  }

  /** Gets a list of recipes that are always shown in JEI. */
  static <T extends FilteredRecipe> List<T> unfiltered(List<T> recipes) {
    List<T> filtered = recipes.stream().filter(r -> !r.isFiltered()).toList();
    // save memory: return the same list if the size is the same
    return filtered.size() < recipes.size() ? filtered : recipes;
  }

  /** Gets a list of recipes that support dynamic filtering. */
  static <T extends FilteredRecipe> List<T> filtered(List<T> recipes) {
    List<T> filtered = recipes.stream().filter(FilteredRecipe::isFiltered).toList();
    // save memory: return the same list if the size is the same
    return filtered.size() < recipes.size() ? filtered : recipes;
  }

  /** Gets a list of always visible recipes from the list of filterable recipes. */
  static <T extends FilteredRecipe> List<T> alwaysVisible(List<T> recipes) {
    List<T> filtered = recipes.stream().filter(FilteredRecipe::showUnfocused).toList();
    // save memory: return the same list if the size is the same
    return filtered.size() < recipes.size() ? filtered : recipes;
  }

  /** Checks if the given predicate matches any entry in the given list. */
  static <T> boolean matchesList(Predicate<T> focus, Iterable<T> list) {
    for (T entry : list) {
      if (focus.test(entry)) {
        return true;
      }
    }
    return false;
  }

  /** Checks if the given predicate matches any entry in the given list. */
  static <T> boolean matchesArray(Predicate<T> focus, T[] array) {
    for (T entry : array) {
      if (focus.test(entry)) {
        return true;
      }
    }
    return false;
  }
}
