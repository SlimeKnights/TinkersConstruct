package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.tileentity.table.crafting.PartBuilderInventoryWrapper;

import java.util.Optional;

public interface IPartBuilderRecipe extends ICommonRecipe<IPartBuilderInventory> {
  /** Gets the pattern needed for this recipe,
   * if there are multiple recipes with the same pattern, they are effectively merged */
  Pattern getPattern();

  /**
   * Gets the number of material needed for this recipe
   * @return  Material amount
   */
  int getCost();

  /**
   * Checks if the recipe can possibly match. Should treat empty input as a match, and does not need to check sizes
   * @param inv  Inventory instance
   * @return  True if the recipe matches the given pattern
   */
  boolean partialMatch(IPartBuilderInventory inv);

  /**
   * Gets the number of material items consumed by this recipe
   * @param inv  Crafting inventory
   * @return  Number of items consumed
   */
  default int getItemsUsed(IPartBuilderInventory inv) {
    return Optional.ofNullable(inv.getMaterial())
                   .map(mat -> mat.getItemsUsed(getCost()))
                   .orElse(1);
  }

  /* Recipe data */

  @Override
  default RecipeType<?> getType() {
    return RecipeTypes.PART_BUILDER;
  }

  @Override
  default ItemStack getToastSymbol() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  /** Gets the leftover from performing this recipe */
  default ItemStack getLeftover(PartBuilderInventoryWrapper inventoryWrapper) {
    MaterialRecipe recipe = inventoryWrapper.getMaterial();
    if (recipe != null) {
      int value = recipe.getValue();
      if (value > 1) {
        int remainder = value - getCost() % value;
        if (remainder > 0) {
          ItemStack leftover = recipe.getLeftover();
          leftover.setCount(leftover.getCount() * remainder);
          return leftover;
        }
      }
    }
    return ItemStack.EMPTY;
  }
}
