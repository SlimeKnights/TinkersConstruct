package tconstruct.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotRestricted extends Slot {
  private final Item allowedItem;

  public SlotRestricted(Item item, IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
    allowedItem = item;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return stack != null && stack.getItem() == allowedItem;
  }
}
