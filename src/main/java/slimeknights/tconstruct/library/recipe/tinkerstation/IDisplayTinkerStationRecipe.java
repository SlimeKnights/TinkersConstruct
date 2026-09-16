package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.display.RecipeSlot;
import slimeknights.tconstruct.library.recipe.display.RecipeSlots;

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

  /**
   * Gets an ingredients to display in JEI.
   * @param  slot        Slot index to display
   * @param  focus       Current focus
   * @param focusOutput  If true, the focus is from an output. If false, its from an input or tool.
   * @return  Display item list, or {@link List#of()} if an invalid index.
   */
  default List<ItemStack> getDisplayItems(int slot, ItemStack focus, boolean focusOutput) {
    return getDisplayItems(slot);
  }

  /** Gets the result tool before applying this recipe. */
  List<ItemStack> getToolWithoutModifier();

  /**
   * Gets the result tool before applying this recipe.
   * This method notably allows making the input change with respect to a tool on the output focus.
   * @param  focus       Current focus
   * @param focusOutput  If true, the focus is from an output. If false, its from an input or tool.
   * @return  Tools without the recipe.
   */
  default List<ItemStack> getToolWithoutModifier(ItemStack focus, boolean focusOutput) {
    return getToolWithoutModifier();
  }

  /** Gets the result tool after applying this recipe. */
  List<ItemStack> getToolWithModifier();

  /**
   * Gets the result tool before applying this recipe.
   * This method notably makes the tool able to return a list with respond to a focus instead of just a single tool.
   * IF the goal is to make the focus tool the input and a direct change to it the output, see {@link #onFocused(ItemStack)}, which also supports returning empty for an error state.
   * @param  focus       Current focus
   * @param focusOutput  If true, the focus is from an output. If false, its from an input or tool.
   * @return  Tools with the recipe.
   */
  default List<ItemStack> getToolWithModifier(ItemStack focus, boolean focusOutput) {
    return getToolWithModifier();
  }

  /** Checks if the passed item is a valid tool for this recipe. Used to determine whether to call {@link #onFocused} on input focus. */
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

  /** Gets the maximum stack size for tools computed via {@link #onFocused(ItemStack)}. The minimum between this and the stack's max stack size will be used. */
  default int getMaxToolSize() {
    return ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE;
  }

  /** Gets the maximum stack size for the given stack. */
  default int getMaxToolSize(ItemStack stack) {
    return Math.min(stack.getMaxStackSize(), getMaxToolSize());
  }

  /**
   * Creates a result for this recipe given the passed focus.
   * If you wish to return a list instead of a single tool, or wish to make the input respond to an output focus,
   * see {@link #getToolWithoutModifier(ItemStack, boolean)} and {@link #getToolWithModifier(ItemStack, boolean)}.
   * @param  focus  Input tool focus. Called if it passes {@link #isTool(ItemStack)}.
   * @return {@link RecipeResult#pass()} to use the default {@link #getToolWithoutModifier()} and {@link #getToolWithModifier()}, filtered by the focus.
   *         {@link RecipeResult#success(Object)} to override the result with the returned stack. Stack size will be handled automatically using {@link #getMaxToolSize()}.
   *         {@link RecipeResult#failure(Component)} to return an error indicating why this recipe cannot apply.
   *         If anything other than {@link RecipeResult#pass()} is returned, the input tool will be set to the focus.
   */
  default RecipeResult<ItemStack> onFocused(ItemStack focus) {
    return RecipeResult.pass();
  }

  /** If true, the displayed ingredients are dynamically updated whenever the view changes in {@link #onDisplayUpdate(RecipeSlot, RecipeSlots, RecipeSlot)} */
  default boolean isSlotsDynamic() {
    return false;
  }

  /**
   * Called when the display updates in JEI to allow a recipe to dynamically change the displayed values.
   * Only called if {@link #isSlotsDynamic()} is true.
   *
   * @param tool   Tool input, representing the tool without this modification.
   * @param inputs List of inputs used to modify the tool.
   * @param output Tool output, representing the tool with this modification.
   */
  default void onDisplayUpdate(RecipeSlot<ItemStack> tool, RecipeSlots<ItemStack> inputs, RecipeSlot<ItemStack> output) {}
}
