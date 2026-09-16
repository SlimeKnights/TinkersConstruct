package slimeknights.tconstruct.library.recipe.display;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/** Interface to apply to display recipes that enable hiding with respect to specific item stacks */
public interface FilteredItemRecipe extends FilteredRecipe {
  /**
   * Checks if this recipe matches the item stack, causing it to be included in the cache.
   * Should produce equivalent results to JEI's {@code setRecipe} function.
   * Generally does not need to be overridden from the base display recipe.
   * @param focus  predicate matching any stacks that match the focus.
   * @param output If true, the focus is an output. If false, the focus is an input.
   * @return true if the recipe matches (ignoring non-subtype NBT).
   */
  boolean matchesItem(Predicate<ItemStack> focus, boolean output);

  /**
   * Checks if the recipe is currently visible given the item's non-subtype NBT.
   * Only used if {@link #isFiltered()} to be true.
   * @param focus  Currently focused stack
   * @param output If true, the focus is an output. If false, the focus is an input.
   * @return true if the recipe should be shown.
   */
  default boolean isVisibleFromItem(ItemStack focus, boolean output) {
    return true;
  }
}
