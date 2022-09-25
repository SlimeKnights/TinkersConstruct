package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.material.IMaterialValue;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.inventory.PartBuilderContainerWrapper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface IPartBuilderRecipe extends ICommonRecipe<IPartBuilderContainer> {
  /** Gets the pattern needed for this recipe,
   * if there are multiple recipes with the same pattern, they are effectively merged */
  Pattern getPattern();

  /** Gets a stream of all patterns matching this recipe, allows one recipe to match on multiple patterns */
  default Stream<Pattern> getPatterns(IPartBuilderContainer inv) {
    return Stream.of(getPattern());
  }

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
  boolean partialMatch(IPartBuilderContainer inv);

  /**
   * Gets the number of material items consumed by this recipe
   * @param inv  Crafting inventory
   * @return  Number of items consumed
   */
  default int getItemsUsed(IPartBuilderContainer inv) {
    return Optional.ofNullable(inv.getMaterial())
                   .map(mat -> mat.getItemsUsed(getCost()))
                   .orElse(1);
  }

  /** Assembles the result with the given pattern */
  default ItemStack assemble(IPartBuilderContainer inv, Pattern pattern) {
    return assemble(inv);
  }

  /* Recipe data */

  @Override
  default RecipeType<?> getType() {
    return TinkerRecipeTypes.PART_BUILDER.get();
  }

  @Override
  default ItemStack getToastSymbol() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  /** @deprecated use {@link #getLeftover(PartBuilderContainerWrapper, Pattern)} */
  @Deprecated
  default ItemStack getLeftover(PartBuilderContainerWrapper inventoryWrapper) {
    IMaterialValue recipe = inventoryWrapper.getMaterial();
    if (recipe != null) {
      int value = recipe.getValue();
      if (value > 1) {
        int remainder = (value - getCost()) % value;
        if (remainder < 0) {
          remainder += value;
        }
        if (remainder != 0) {
          ItemStack leftover = recipe.getLeftover();
          leftover.setCount(leftover.getCount() * remainder);
          return leftover;
        }
      }
    }
    return ItemStack.EMPTY;
  }

  /**
   * Gets the leftover from performing this recipe
   * TODO: switch to the interface for the parameter
   */
  default ItemStack getLeftover(PartBuilderContainerWrapper inventoryWrapper, Pattern pattern) {
    return getLeftover(inventoryWrapper);
  }

  /** Gets the title to display on the part builder panel. If null, displays default info */
  @Nullable
  default Component getTitle() {
    return null;
  }

  /** Gets the text to display on the part builder screen */
  default List<Component> getText(IPartBuilderContainer inv) {
    return Collections.emptyList();
  }
}
