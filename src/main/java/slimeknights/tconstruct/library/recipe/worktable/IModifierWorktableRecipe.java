package slimeknights.tconstruct.library.recipe.worktable;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Main interface for all recipes in the Modifier Worktable
 */
public interface IModifierWorktableRecipe extends ICommonRecipe<ITinkerableContainer> {
  @Override
  default RecipeType<?> getType() {
    return TinkerRecipeTypes.MODIFIER_WORKTABLE.get();
  }

  /** If true, this recipe matches the given inputs, does not consider the modifier button yet */
  @Override
  boolean matches(ITinkerableContainer inv, Level world);

  /** Gets the title for display in JEI and in the info panel */
  Component getTitle();

  /**
   * Gets the description of this recipe, or display an error if this recipe matches but currently has no modifiers
   * @param inv  Recipe inventory, null when fetching in JEI
   */
  Component getDescription(@Nullable ITinkerableContainer inv);

  /**
   * Gets a list of modifier buttons for the given input. May be empty.
   * It will not be automatically sorted, you must ensure the order is consistent on both client and server.
   * @param inv  Recipe inventory, null when fetching in JEI
   */
  List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv);

  /**
   * Gets the tool stack result for this recipe.
   * @param inv             Inventory instance
   * @param modifier        Modifier that was interacted with
   * @return Tool stack result. Can be the same instance as previousResult or a new instance.
   *         Should never share NBT with {@link ITinkerableContainer#getTinkerable()}, needs to be a copy.
   */
  RecipeResult<LazyToolStack> getResult(ITinkerableContainer inv, ModifierEntry modifier);

  default int shrinkToolSlotBy(LazyToolStack result) {
    return result.getSize();
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result    Result from {@link #getResult(ITinkerableContainer, ModifierEntry)}
   * @param inv       Inventory instance to modify inputs
   * @param isServer  If true, this is on the serverside. Use to handle randomness, {@link IMutableTinkerStationContainer#giveItem(ItemStack)} should handle being called serverside only
   */
  default void updateInputs(LazyToolStack result, ITinkerableContainer.Mutable inv, ModifierEntry selected, boolean isServer) {
    // shrink all stacks by 1
    for (int index = 0; index < inv.getInputCount(); index++) {
      inv.shrinkInput(index, 1);
    }
  }

  /** Gets input tool options, need not be rendered with the modifiers, simply be valid tools */
  List<ItemStack> getInputTools();

  /**
   * Gets an ingredients to display in JEI.
   * @param  slot  Slot index to display
   * @return  Display item list
   */
  List<ItemStack> getDisplayItems(int slot);

  /** Gets the number of inputs for this recipe */
  int getInputCount();

  /** If true, the recipe modifier is an output */
  default boolean isModifierOutput() {
    return false;
  }


  /** Deprecated methods to ignore */

  @Override
  @Deprecated
  default ItemStack getResultItem(RegistryAccess access) {
    return ItemStack.EMPTY;
  }

  @Deprecated
  @Override
  default ItemStack assemble(ITinkerableContainer inv, RegistryAccess access) {
    return ItemStack.EMPTY;
  }
}
