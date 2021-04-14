package slimeknights.tconstruct.misc;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemHandlerHelper {

  public static ItemStack copyStackWithSize(ItemStack stack, int count) {
    final ItemStack copy = stack.copy();
    copy.setCount(count);
    return copy;
  }

  public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
    if (a.isEmpty() || !sameItem(a, b) || a.hasTag() != b.hasTag())
      return false;

    return (!a.hasTag() || a.getTag().equals(b.getTag()));
  }

  private static boolean sameItem(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && a.getItem() == b.getItem();
  }
}
