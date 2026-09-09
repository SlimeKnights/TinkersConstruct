package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/** Common interface between {@link IDisplayToolModification} and {@link slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe} */
public interface IDisplayTinkerStationRecipe {
  /** Return from {@link #linkToOutput()} to indicate no slots are linked. Will attempt to link the inputs instead. */
  int[] NO_LINKS = new int[0];

  /** Gets the ID of this recipe. If this is a generated display recipe, uses the parent recipe ID */
  @Nullable
  ResourceLocation getRecipeId();

  /**
   * Gets the variant for this recipe. Mutually exclusive with level for recipe display.
   * @return  Variant text for the modifier, or null if not a variant.
   */
  @Nullable
  default Component getVariant() {
    return null;
  }

  /** Gets the number of inputs for this recipe */
  int getInputCount();

  /**
   * Gets an ingredients to display in JEI.
   * @param  slot  Slot index to display
   * @return  Display item list, or {@link List#of()} if an invalid index.
   */
  List<ItemStack> getDisplayItems(int slot);

  /** Gets the result tool before applying this recipe. */
  List<ItemStack> getToolWithoutModifier();

  /** Gets the result tool after applying this recipe. */
  List<ItemStack> getToolWithModifier();

  /** List of input indices to link to the output in JEI. */
  default int[] linkToOutput() {
    return NO_LINKS;
  }
}
