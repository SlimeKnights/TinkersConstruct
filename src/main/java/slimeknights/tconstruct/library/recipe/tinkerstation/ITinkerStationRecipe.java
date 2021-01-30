package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.ValidationResult;

/**
 * Main interface for all recipes in the Tinker Station
 */
public interface ITinkerStationRecipe extends ICommonRecipe<ITinkerStationInventory> {
  /* Recipe data */

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.TINKER_STATION;
  }

  /** If true, this recipe matches the given inputs, ignoring current tool state */
  @Override
  boolean matches(ITinkerStationInventory inv, World world);

  /** Validates the recipe, returning success, or failure with a possible message */
  default ValidationResult validate(ITinkerStationInventory inv) {
    return ValidationResult.SUCCESS;
  }

  /**
   * Gets the recipe result, assumes matches is true and validate returned SUCCESS.
   * Returning {@link ItemStack#EMPTY} is equivalent to {@link #validate(ITinkerStationInventory)} returning {@link ValidationResult#PASS}
   * @return  Item stack result.
   */
  @Override
  default ItemStack getCraftingResult(ITinkerStationInventory inv) {
    return getRecipeOutput().copy();
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #getCraftingResult(ITinkerStationInventory)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  default void updateInputs(ItemStack result, IMutableTinkerStationInventory inv) {
    // shrink all stacks by 1
    for (int index = 0; index < inv.getInputCount(); index++) {
      inv.shrinkInput(index, 1);
    }
  }

  /** @deprecated use {@link #updateInputs(ItemStack, IMutableTinkerStationInventory)} */
  @Override
  @Deprecated
  default NonNullList<ItemStack> getRemainingItems(ITinkerStationInventory inv) {
    return NonNullList.from(ItemStack.EMPTY);
  }
}
