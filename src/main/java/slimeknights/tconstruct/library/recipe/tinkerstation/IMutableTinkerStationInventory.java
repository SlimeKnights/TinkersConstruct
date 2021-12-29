package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Extension of {@link ITinkerStationInventory} to allow modifying inventory contents
 */
public interface IMutableTinkerStationInventory extends ITinkerStationInventory {
  /**
   * Sets the input in the given slot
   * @param index  Slot to set
   * @param stack  New stack
   */
  void setInput(int index, ItemStack stack);

  /**
   * Gives an extra recipe result to the user of the inventory
   * @param stack  Extra stack
   */
  void giveItem(ItemStack stack);


  /**
   * Shrinks a slot by the given count, returning the properly sized container
   * @param slot    Slot to shrink
   * @param amount  Amount to shrink by
   */
  default void shrinkInput(int slot, int amount, ItemStack container) {
    ItemStack stack = getInput(slot);
    if (!stack.isEmpty()) {
      // determine how large to make the container
      int count = stack.getCount();
      if (!container.isEmpty()) {
        container.setCount(Math.min(count, amount));
      }
      // if removing all items, set the slot to the container. If container is empty that is fine
      if (amount >= count) {
        setInput(slot, container);
      } else {
        // otherwise, shrink the stack and add the container
        stack = stack.copy();
        stack.shrink(amount);
        setInput(slot, stack);
        if (!container.isEmpty()) {
          giveItem(container);
        }
      }
    }
  }

  /**
   * Shrinks a slot by the given count, returning the properly sized container
   * @param slot    Slot to shrink
   * @param amount  Amount to shrink by
   */
  default void shrinkInput(int slot, int amount) {
    ItemStack stack = getInput(slot);
    if (!stack.isEmpty()) {
      ItemStack container = stack.getContainerItem();
      if (container.isEmpty() && stack.getItem() == Items.POTION) {
        container = new ItemStack(Items.GLASS_BOTTLE);
      }
      shrinkInput(slot, amount, container);
    }
  }
}
