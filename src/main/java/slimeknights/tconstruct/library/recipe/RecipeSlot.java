package slimeknights.tconstruct.library.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * Helper for interfacing with slots in JEI without adding a compile time dependency on JEI
 * @see RecipeSlots
 */
public interface RecipeSlot<T> {
  /** Fallback to use when an item slot is not present */
  RecipeSlot<ItemStack> EMPTY_ITEM = new Empty<>(ItemStack.EMPTY);
  /** Fallback to use when a fluid slot is not present */
  RecipeSlot<FluidStack> EMPTY_FLUID = new Empty<>(FluidStack.EMPTY);

  /** Gets the currently displayed ingredient */
  T get();

  /** Sets the ingredient to display the given override */
  void set(T ingredient);

  /** Sets the ingredient to display the given overrides */
  void set(List<T> ingredients);

  /** Represents an empty slot that cannot be interacted with */
  record Empty<T>(T emptyValue) implements RecipeSlot<T> {
    @Override
    public T get() {
      return emptyValue;
    }

    @Override
    public void set(T ingredient) {}

    @Override
    public void set(List<T> ingredients) {}
  }
}
