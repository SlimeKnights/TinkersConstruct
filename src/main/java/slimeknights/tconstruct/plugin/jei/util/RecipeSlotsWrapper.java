package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.recipe.RecipeSlots;

import java.util.List;

/**
 * Wrapper around JEI slots to allow interfacing with them outside JEI without a compile time dependency.
 * Intended for groups of slots where index is meaningful.
 * @param slots       Slots to wrap
 * @param type        Slot type
 * @param emptyValue  Value to return if the slot is the wrong type.
 * @see RecipeSlotWrapper
 */
public record RecipeSlotsWrapper<T>(List<IRecipeSlotDrawable> slots, IIngredientType<T> type, T emptyValue) implements RecipeSlots<T> {
  @Override
  public T get(int index) {
    if (index >= 0 && index < slots.size()) {
      return slots.get(index).getDisplayedIngredient(type).orElse(emptyValue);
    }
    return emptyValue;
  }

  @Override
  public void set(int index, T ingredient) {
    if (index >= 0 && index < slots.size()) {
      slots.get(index).createDisplayOverrides().addIngredient(type, ingredient);
    }
  }

  @Override
  public void set(int index, List<T> ingredients) {
    if (index >= 0 && index < slots.size()) {
      slots.get(index).createDisplayOverrides().addIngredients(type, ingredients);
    }
  }


  /* Helpers */

  /** Makes a {@link RecipeSlots} wrapper from the given slots list. */
  public static <T> RecipeSlots<T> create(List<IRecipeSlotDrawable> slots, IIngredientType<T> type, RecipeSlots<T> fallback) {
    if (!slots.isEmpty()) {
      return new RecipeSlotsWrapper<>(slots, type, fallback.get(0));
    }
    return fallback;
  }

  /** Makes a {@link RecipeSlots} wrapper from filtering the given slots list. */
  public static <T> RecipeSlots<T> create(List<IRecipeSlotDrawable> allSlots, String prefix, IIngredientType<T> type, RecipeSlots<T> fallback) {
    return create(CategoryUtil.filterSlots(allSlots, prefix), type, fallback);
  }

  /** Makes a wrapper for a list of item slots. */
  public static RecipeSlots<ItemStack> createItem(List<IRecipeSlotDrawable> slots) {
    return create(slots, VanillaTypes.ITEM_STACK, RecipeSlots.EMPTY_ITEM);
  }

  /** Makes a wrapper for a filtered list of item slots */
  public static RecipeSlots<ItemStack> createItem(List<IRecipeSlotDrawable> slots, String prefix) {
    return create(slots, prefix, VanillaTypes.ITEM_STACK, RecipeSlots.EMPTY_ITEM);
  }
}
