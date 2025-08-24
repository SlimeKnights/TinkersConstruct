package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.material.IMaterialValue;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface IPartBuilderRecipe extends ICommonRecipe<IPartBuilderContainer> {
  /** Default patterns in a part builder recipe, Forge has cache invalidation for vanilla, so this is fine as long as that persists */
  Ingredient DEFAULT_PATTERNS = Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS);
  /** Pattern to use for recipes that don't implement the standard pattern behavior */
  Pattern MISSING = new Pattern(TConstruct.MOD_ID, "missingno");

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
  default ItemStack assemble(IPartBuilderContainer inv, RegistryAccess access, Pattern pattern) {
    return assemble(inv, access);
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

  /** Gets the leftover from performing this recipe */
  default ItemStack getLeftover(IPartBuilderContainer inventoryWrapper, Pattern pattern) {
    IMaterialValue recipe = inventoryWrapper.getMaterial();
    if (recipe != null) {
      int value = recipe.getValue();
      if (value > 1) {
        int needed = recipe.getNeeded();
        int remainder = (value - getCost() * needed) % value;
        if (remainder < 0) {
          remainder += value;
        }
        if (remainder > 0) {
          ItemStack leftover = recipe.getLeftover();
          if (!leftover.isEmpty()) {
            leftover.setCount(leftover.getCount() * remainder / needed);
          }
          return leftover;
        }
      }
    }
    return ItemStack.EMPTY;
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
