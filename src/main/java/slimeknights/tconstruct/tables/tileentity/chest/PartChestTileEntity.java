package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

public class PartChestTileEntity extends TinkerChestTileEntity {

  public PartChestTileEntity() {
    // limit of 4 parts per slot
    super(TinkerTables.partChestTile.get(), Util.makeTranslationKey("gui", "part_chest"), DEFAULT_MAX, 16);
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    // check if there is no other slot containing that item
    for (int i = 0; i < this.getSizeInventory(); i++) {
      // don't compare count
      if (ItemStack.areItemsEqual(itemstack, this.getStackInSlot(i))
        && ItemStack.areItemStackTagsEqual(itemstack, this.getStackInSlot(i))) {
        return i == slot; // only allowed in the same slot
      }
    }
    return itemstack.getItem() instanceof IMaterialItem;
  }
}
