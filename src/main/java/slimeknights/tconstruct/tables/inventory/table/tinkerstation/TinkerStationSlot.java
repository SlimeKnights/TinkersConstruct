package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import lombok.Getter;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

/** Class for common logic with tinker station input slots */
public abstract class TinkerStationSlot extends Slot {
  @Getter
  private boolean dormant;
  public TinkerStationSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  /** Activates this slot */
  public void activate() {
    this.dormant = false;
  }

  /** Deactivates this slot */
  public void deactivate() {
    this.dormant = true;
  }
}
