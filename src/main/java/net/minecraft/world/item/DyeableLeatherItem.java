package slimeknights.tconstruct.compat.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

/** Compatibility facade for the removed dyeable leather item interface. */
public interface DyeableLeatherItem {
  default boolean hasCustomColor(ItemStack stack) {
    return stack.has(DataComponents.DYED_COLOR);
  }

  default int getColor(ItemStack stack) {
    return DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR);
  }

  default void clearColor(ItemStack stack) {
    stack.remove(DataComponents.DYED_COLOR);
  }

  default void setColor(ItemStack stack, int color) {
    stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
  }
}
