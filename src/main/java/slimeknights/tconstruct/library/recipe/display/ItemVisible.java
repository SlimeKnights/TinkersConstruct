package slimeknights.tconstruct.library.recipe.display;

import net.minecraft.world.item.ItemStack;

/** Interface to help with builders for {@link FilteredItemRecipe} */
@FunctionalInterface
public interface ItemVisible {
  /**
   * Checks if the recipe is currently visible given the item's non-subtype NBT.
   * @param focus  Currently focused stack
   * @param output If true, the focus is an output. If false, the focus is an input.
   * @return true if the recipe should be shown.
   */
  boolean isVisibleFromItem(ItemStack focus, boolean output);
}
