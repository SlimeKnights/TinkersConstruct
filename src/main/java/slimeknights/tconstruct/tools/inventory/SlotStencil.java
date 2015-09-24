package slimeknights.tconstruct.tools.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.tools.Pattern;

public class SlotStencil extends Slot {

  public SlotStencil(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    if(stack == null || !(stack.getItem() instanceof Pattern))
      return false;

    return ((Pattern) stack.getItem()).isBlankPattern(stack);
  }
}
