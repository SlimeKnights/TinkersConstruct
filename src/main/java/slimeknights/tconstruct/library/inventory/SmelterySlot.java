package slimeknights.tconstruct.library.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class SmelterySlot extends Slot {

  public SmelterySlot(Inventory inventory, int index, int x, int y) {
    super(inventory, index, x, y);
  }

  @Override
  public int getMaxItemCount() {
    return 1;
  }
}
