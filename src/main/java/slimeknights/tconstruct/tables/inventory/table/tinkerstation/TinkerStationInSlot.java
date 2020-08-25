package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class TinkerStationInSlot extends Slot {

  public TinkerStationContainer parent;
  public boolean dormant;
  public ItemStack icon;

  public TinkerStationInSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, TinkerStationContainer parentContainer) {
    super(inventoryIn, index, xPosition, yPosition);
    this.parent = parentContainer;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    // dormant slots don't take any items, they can only be taken out of
    if (this.dormant) {
      return false;
    }

    return super.isItemValid(stack);
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

  public void updateIcon(ItemStack icon) {
    this.icon = icon;
  }

  @Override
  public void onSlotChanged() {
    this.parent.updateScreen();
  }
}
