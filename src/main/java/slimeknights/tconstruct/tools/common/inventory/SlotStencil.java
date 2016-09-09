package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.Pattern;

public class SlotStencil extends Slot {

  public SlotStencil(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    if(stack == null || !(stack.getItem() instanceof IPattern)) {
      return false;
    }

    return !(stack.getItem() instanceof IPattern) || ((Pattern) stack.getItem()).isBlankPattern(stack);
  }
}
