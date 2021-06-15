package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.tables.tileentity.table.TinkerStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.crafting.LazyResultInventory;

import javax.annotation.Nullable;

/** Represents an input slot on the Tinker Station */
public class TinkerStationInputSlot extends TinkerStationSlot {
  private final LazyResultInventory craftResult;
  @Nullable @Getter @Setter
  private Pattern icon;
  @Nullable @Getter @Setter
  private Item filter;

  public TinkerStationInputSlot(TinkerStationTileEntity tile, int index, int xPosition, int yPosition) {
    super(tile, index, xPosition, yPosition);
    this.craftResult = tile.getCraftingResult();
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    // dormant slots don't take any items, they can only be taken out of
    if (this.isDormant()) {
      return false;
    }
    // if we have a filter, use the filter
    return filter == null || filter == stack.getItem();
  }

  @Override
  public void onSlotChanged() {
    craftResult.clear();
    super.onSlotChanged();
  }
}
