package slimeknights.tconstruct.library.recipe.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * IInventory extension for an inventory wrapper containing a single item. Primarily used for inventory slot wrappers
 */
public interface ISingleItemInventory extends IReadOnlyInventory {
  /**
   * Gets the relevant item in this inventory
   * @return  Contained item
   */
  ItemStack getStack();


  /* Multistack methods, redundant now */

  /** @deprecated use {{@link #getStack()}} */
  @Deprecated
  @Override
  default ItemStack getStackInSlot(int index) {
    return index == 0 ? getStack() : ItemStack.EMPTY;
  }

  @Override
  default boolean isEmpty() {
    return getStack().isEmpty();
  }

  /** @deprecated always 1, not useful */
  @Deprecated
  @Override
  default int getSizeInventory() {
    return 1;
  }


  /* Utils made more efficient */

  @Override
  default int count(Item itemIn) {
    ItemStack stack = getStack();
    if (stack.getItem() == itemIn) {
      return stack.getCount();
    }
    return 0;
  }

  @Override
  default boolean hasAny(Set<Item> set) {
    ItemStack stack = getStack();
    return !stack.isEmpty() && set.contains(getStack().getItem());
  }
}
