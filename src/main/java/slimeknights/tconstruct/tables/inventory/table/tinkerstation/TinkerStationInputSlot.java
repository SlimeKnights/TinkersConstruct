package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.tables.tileentity.crafting.LazyResultInventory;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import javax.annotation.Nullable;

/** Represents an input slot on the Tinker Station */
public class TinkerStationInputSlot extends TinkerStationSlot {
  private final LazyResultInventory craftResult;
  @Nullable @Getter @Setter
  private ResourceLocation icon;

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

    return super.isItemValid(stack);
  }

  @Override
  public void onSlotChanged() {
    craftResult.clear();
    super.onSlotChanged();
  }
}
