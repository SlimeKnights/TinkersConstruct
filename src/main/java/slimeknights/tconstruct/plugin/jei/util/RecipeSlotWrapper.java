package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.RecipeSlot;

import java.util.List;

/**
 * Wrapper around a JEI slot to allow interfacing with it outside JEI without a compile time dependency.
 * @param slot        Slot to wrap
 * @param type        Slot type
 * @param emptyValue  Value to return if the slot is the wrong type.
 * @param mirrors     Slot mirrors to update when the slot is updated.
 * @see RecipeSlotsWrapper
 */
public record RecipeSlotWrapper<T>(IRecipeSlotDrawable slot, IIngredientType<T> type, T emptyValue, List<IRecipeSlotDrawable> mirrors) implements RecipeSlot<T> {
  public RecipeSlotWrapper(IRecipeSlotDrawable slot, IIngredientType<T> type, T emptyValue, IRecipeSlotDrawable... mirrors) {
    this(slot, type, emptyValue, List.of(mirrors));
  }

  public RecipeSlotWrapper(IRecipeSlotDrawable slot, IIngredientType<T> type, T emptyValue) {
    this(slot, type, emptyValue, List.of());
  }

  @Override
  public T get() {
    return slot.getDisplayedIngredient(type).orElse(emptyValue);
  }

  @Override
  public void set(T ingredient) {
    slot.createDisplayOverrides().addIngredient(type, ingredient);
    for (IRecipeSlotDrawable mirror : mirrors) {
      mirror.createDisplayOverrides().addIngredient(type, ingredient);
    }
  }

  @Override
  public void set(List<T> ingredients) {
    slot.createDisplayOverrides().addIngredients(type, ingredients);
    for (IRecipeSlotDrawable mirror : mirrors) {
      mirror.createDisplayOverrides().addIngredients(type, ingredients);
    }
  }


  /* Helpers */

  /** Makes a {@link RecipeSlot} wrapper from the given slots list. */
  public static <T> RecipeSlot<T> create(List<IRecipeSlotDrawable> slots, String name, IIngredientType<T> type, RecipeSlot<T> fallback) {
    IRecipeSlotDrawable slot = CategoryUtil.findSlot(slots, name);
    if (slot != null) {
      return new RecipeSlotWrapper<>(slot, type, fallback.get());
    }
    return fallback;
  }

  /** Makes a wrapper for an item slot. */
  public static RecipeSlot<ItemStack> createItem(List<IRecipeSlotDrawable> slots, String name) {
    return create(slots, name, VanillaTypes.ITEM_STACK, RecipeSlot.EMPTY_ITEM);
  }

  /** Makes a wrapper for a fluid slot. */
  public static RecipeSlot<FluidStack> createFluid(List<IRecipeSlotDrawable> slots, String name) {
    return create(slots, name, ForgeTypes.FLUID_STACK, RecipeSlot.EMPTY_FLUID);
  }
}
