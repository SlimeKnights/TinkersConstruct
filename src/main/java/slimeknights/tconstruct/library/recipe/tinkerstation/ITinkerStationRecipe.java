package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;

/**
 * Main interface for all recipes in the Tinker Station
 */
public interface ITinkerStationRecipe extends ICommonRecipe<ITinkerStationInventory> {
  /* Recipe data */

  @Override
  default RecipeType<?> getType() {
    return RecipeTypes.TINKER_STATION;
  }

  /** If true, this recipe matches the given inputs, ignoring current tool state */
  @Override
  boolean matches(ITinkerStationInventory inv, Level world);

  /**
   * Gets the recipe result. Return {@link ItemStack#EMPTY) to represent {@link ValidatedResult#PASS}, or a non-empty stack to represent success.
   * For more complex recipes, override {@link #getValidatedResult(ITinkerStationInventory)} instead.
   *
   * Do not call this method directly, but it is okay to override it.
   * @return  Recipe result, may be empty.
   */
  @Override
  default ItemStack assemble(ITinkerStationInventory inv) {
    return getResultItem().copy();
  }

  /**
   * Gets the recipe result, or an object containing an error message if the recipe matches but cannot be applied.
   * @return Validated result
   */
  default ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    ItemStack result = assemble(inv);
    if (result.isEmpty()) {
      return ValidatedResult.PASS;
    }
    return ValidatedResult.success(result);
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #getCraftingResult(ITinkerStationInventory)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   * @param isServer  If true, this is on the serverside. Use to handle randomness, {@link IMutableTinkerStationInventory#giveItem(ItemStack)} should handle being called serverside only
   */
  default void updateInputs(ItemStack result, IMutableTinkerStationInventory inv, boolean isServer) {
    // shrink all stacks by 1
    for (int index = 0; index < inv.getInputCount(); index++) {
      inv.shrinkInput(index, 1);
    }
  }


  /* Deprecated */

  /** @deprecated use {@link #updateInputs(ItemStack, IMutableTinkerStationInventory, boolean)} */
  @Override
  @Deprecated
  default NonNullList<ItemStack> getRemainingItems(ITinkerStationInventory inv) {
    return NonNullList.of(ItemStack.EMPTY);
  }
}
