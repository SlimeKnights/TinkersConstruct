package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.recipe.display.FilteredItemRecipe;

import java.util.function.Predicate;

import static slimeknights.tconstruct.library.recipe.display.FilteredRecipe.matchesList;

/** Recipes that show in JEI for changing tools in ways other than adding modifiers, such as part swapping or tool damaging. */
public interface IDisplayToolModification extends IDisplayTinkerStationRecipe, FilteredItemRecipe {
  /** Gets the title for display in JEI */
  Component getTitle();

  /** Gets the tooltip to display in JEI */
  Component getTooltip();

  /** If true, the tool is simply modified by the recipe. If false, it is an input and output for crafting. */
  default boolean isToolCatalyst() {
    return false;
  }


  /* Filtered */

  @Override
  default boolean matchesItem(Predicate<ItemStack> focus, boolean output) {
    // only output is tools without modifier, unless it's a catalyst
    if (output) {
      return !isToolCatalyst() && matchesList(focus, getToolWithModifier());
    }
    // check all inputs
    for (int i = 0; i < getInputCount(); i++) {
      if (matchesList(focus, getDisplayItems(i))) {
        return true;
      }
    }
    // check tools, without is always input while with is input if catalyst
    return matchesList(focus, getToolWithoutModifier()) || isToolCatalyst() && matchesList(focus, getToolWithModifier());
  }
}
