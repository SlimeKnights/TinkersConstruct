package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tinkering.ITinkerStationDisplay;

public class TinkerSlot extends Slot {

  public boolean dormant;

  public TinkerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    // dormant slots don't take any items, they can only be taken out of
    if (this.dormant) {
      return false;
    }

    return stack != ItemStack.EMPTY && stack.getItem() instanceof ITinkerStationDisplay;
  }

  public boolean isDormant() {
    return this.dormant;
  }

  public void activate() {
    this.dormant = false;
  }

  public void deactivate() {
    this.dormant = true;
  }
}
