package slimeknights.tconstruct.library.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * Helper for interfacing with a group of slots in JEI without adding a compile time dependency on JEI.
 * @see RecipeSlot
 */
public interface RecipeSlots<T> {
  /** Fallback to use when an item slot is not present */
  RecipeSlots<ItemStack> EMPTY_ITEM = new Empty<>(ItemStack.EMPTY);
  /** Fallback to use when a fluid slot is not present */
  RecipeSlots<FluidStack> EMPTY_FLUID = new Empty<>(FluidStack.EMPTY);

  /** Gets the currently displayed ingredient */
  T get(int index);

  /** Sets the ingredient to display the given override */
  void set(int index, T ingredient);

  /** Sets the ingredient to display the given overrides */
  void set(int index, List<T> ingredients);

  /** Represents an empty slot that cannot be interacted with */
  record Empty<T>(T emptyValue) implements RecipeSlots<T> {
    @Override
    public T get(int index) {
      return emptyValue;
    }

    @Override
    public void set(int index, T ingredient) {}

    @Override
    public void set(int index, List<T> ingredients) {}
  }
}
