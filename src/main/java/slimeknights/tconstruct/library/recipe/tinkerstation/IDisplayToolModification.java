package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.network.chat.Component;

/** Recipes that show in JEI for changing tools in ways other than adding modifiers, such as part swapping or tool damaging. */
public interface IDisplayToolModification extends IDisplayTinkerStationRecipe {
  /** Gets the title for display in JEI */
  Component getTitle();

  /** Gets the tooltip to display in JEI */
  Component getTooltip();

  /** If true, the tool is simply modified by the recipe. If false, it is an input and output for crafting. */
  default boolean isToolCatalyst() {
    return false;
  }
}
