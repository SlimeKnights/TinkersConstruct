package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tables.TinkerTables;

public class CastChestTileEntity extends ChestTileEntity {
  public CastChestTileEntity() {
    super(TinkerTables.castChestTile.get(), TConstruct.makeTranslationKey("gui", "cast_chest"), DEFAULT_MAX, 4);
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    // check if there is no other slot containing that item
    for (int i = 0; i < this.getSizeInventory(); i++) {
      if (ItemStack.areItemsEqual(itemstack, this.getStackInSlot(i))) {
        return i == slot;
      }
    }
    return TinkerTags.Items.GOLD_CASTS.contains(itemstack.getItem());
  }
}
