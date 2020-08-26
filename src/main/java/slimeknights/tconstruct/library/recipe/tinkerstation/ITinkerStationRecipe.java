package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.ValidationResult;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.ITinkerStationInventory;

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
}
