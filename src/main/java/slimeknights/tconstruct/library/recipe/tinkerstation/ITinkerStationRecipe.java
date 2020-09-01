package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.ValidationResult;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.ITinkerStationInventory;

import java.util.List;
import java.util.function.Consumer;

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

  /** Gets the recipe result, assumes matches is true and validate returned SUCCESS */
  default ItemStack getCraftingResult(ITinkerStationInventory inv) {
    return getRecipeOutput().copy();
  }

  /**
   * Handles subtracting the inputs used by the recipe and passes any extra items (container items) to the consumer
   *
   * @param inventory the actual items in the inventory
   * @param extraStackConsumer handles the extra stacks if they are unable to be added to the inventory
   */
  void consumeInputs(List<ItemStack> inventory, Consumer<ItemStack> extraStackConsumer);

  /** Unused, please call consumeInputs */
  @Deprecated
  default NonNullList<ItemStack> getRemainingItems(ITinkerStationInventory inv) {
    return NonNullList.from(ItemStack.EMPTY);
  }
}
