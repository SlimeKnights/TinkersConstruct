package slimeknights.tconstruct.library.recipe.inventory;

import lombok.AllArgsConstructor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
public class InventorySlotWrapper implements ISingleItemInventory {
  private final IInventory parent;
  private final int index;

  @Override
  public ItemStack getStack() {
    return parent.getStackInSlot(index);
  }
}
