package slimeknights.tconstruct.tables.inventory.chest;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class StencilSlot extends Slot {

  private boolean requireBlank;

  public StencilSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean requireBlank) {
    super(inventoryIn, index, xPosition, yPosition);
    this.requireBlank = requireBlank;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return true;
    //TODO FIX UP
    /*
    if(stack == null || !(stack.getItem() instanceof IPattern)) {
      return false;
    }

    return !requireBlank || !(stack.getItem() instanceof Pattern) || ((Pattern) stack.getItem()).isBlankPattern(stack);*/
  }
}
