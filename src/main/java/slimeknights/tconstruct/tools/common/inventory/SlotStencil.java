package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.Pattern;

public class SlotStencil extends Slot {

  private boolean requireBlank;

  public SlotStencil(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean requireBlank) {
    super(inventoryIn, index, xPosition, yPosition);
    this.requireBlank = requireBlank;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    if(stack == null || !(stack.getItem() instanceof IPattern)) {
      return false;
    }

    return !requireBlank || !(stack.getItem() instanceof Pattern) || ((Pattern) stack.getItem()).isBlankPattern(stack);
  }
}
