package slimeknights.tconstruct.tables.inventory.chest;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.tables.TinkerTables;

public class PatternSlot extends Slot {

  public PatternSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return (stack.getItem() == TinkerTables.pattern.get());
  }
}
