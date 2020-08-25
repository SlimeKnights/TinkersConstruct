package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.ValidationResult;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.ITinkerStationInventory;

public interface ITinkerStationRecipe extends IRecipe<ITinkerStationInventory> {
  /* Recipe data */

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.TINKER_STATION;
  }

  @Override
  default boolean isDynamic() {
    return true;
  }

  /* Required methods */

  /** @deprecated unused */
  @Deprecated
  @Override
  default boolean canFit(int width, int height) {
    return true;
  }

  /** If true, this recipe matches the given inputs, ignoring current tool state */
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
