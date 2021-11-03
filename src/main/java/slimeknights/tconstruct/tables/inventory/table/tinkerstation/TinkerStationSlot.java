package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.tables.tileentity.table.TinkerStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.crafting.LazyResultInventory;

/** Class for common logic with tinker station input slots */
public class TinkerStationSlot extends Slot {
  private final LazyResultInventory craftResult;
  private LayoutSlot layout = null;
  public TinkerStationSlot(TinkerStationTileEntity tile, int index, int xPosition, int yPosition) {
    super(tile, index, xPosition, yPosition);
    this.craftResult = tile.getCraftingResult();
  }

  /** If true, this slot is inactive */
  public boolean isDormant() {
    return layout == null;
  }

  /** Activates this slot */
  public void activate(LayoutSlot layout) {
    this.layout = layout;
  }

  /** Deactivates this slot */
  public void deactivate() {
    this.layout = null;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    // dormant slots don't take any items, they can only be taken out of
    return layout != null && layout.isValid(stack);
  }

  @Override
  public void onSlotChanged() {
    craftResult.clear();
    super.onSlotChanged();
  }
}
