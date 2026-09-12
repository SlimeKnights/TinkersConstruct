package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.RecipeSlot;
import slimeknights.tconstruct.library.recipe.RecipeSlots;

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

  /** Checks if the passed item is a valid tool for this recipe */
  default boolean isTool(ItemStack check) {
    Item item = check.getItem();
    for (ItemStack stack : getToolWithoutModifier()) {
      if (stack.is(item)) {
        return true;
      }
    }
    return false;
  }

  /** List of input indices to link to the output in JEI. */
  default int[] linkToOutput() {
    return NO_LINKS;
  }


  /* Dynamic ingredients */

  /**
   * Creates a result for this recipe given the passed focus.
   * @param  focus  Input tool focus. Called if it passes {@link #isTool(ItemStack)}.
   * @return {@link RecipeResult#pass()} to use the default {@link #getToolWithoutModifier()} and {@link #getToolWithModifier()}, filtered by the focus.
   *         {@link RecipeResult#success(Object)} to override the result with the returned stack.
   *         {@link RecipeResult#failure(Component)} to return an error indicating why this recipe cannot apply.
   *         If anything other than {@link RecipeResult#pass()} is returned, the input tool will be set to the focus.
   */
  default RecipeResult<ItemStack> onFocused(ItemStack focus) {
    return RecipeResult.pass();
  }

  /** If true, the displayed ingredients are dynamically updated whenever the view changes in {@link #onDisplayUpdate(RecipeSlot, RecipeSlots, RecipeSlot, ItemStack, boolean)} */
  default boolean isSlotsDynamic() {
    return false;
  }

  /**
   * Called when the display updates in JEI to allow a recipe to dynamically change the displayed values.
   * Only called if {@link #isSlotsDynamic()} is true.
   * @param tool         Tool input, representing the tool without this modification.
   * @param inputs       List of inputs used to modify the tool.
   * @param output       Tool output, representing the tool with this modification.
   * @param focus        Current focus. Will be empty if no focus.
   * @param focusOutput  If true, the focus is the output. If false, it is an input.
   */
  default void onDisplayUpdate(RecipeSlot<ItemStack> tool, RecipeSlots<ItemStack> inputs, RecipeSlot<ItemStack> output, ItemStack focus, boolean focusOutput) {}
}
