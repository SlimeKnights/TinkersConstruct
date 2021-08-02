package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

/**
 * Chest that holds parts, up to 8 of a given material and type
 */
public class PartChestTileEntity extends ChestTileEntity {
  public PartChestTileEntity() {
    super(TinkerTables.partChestTile.get(), TConstruct.makeTranslationKey("gui", "part_chest"), new PartChestItemHandler());
  }

  /** Item handler for part chests */
  public static class PartChestItemHandler extends ScalingChestItemHandler {
    @Override
    public int getSlotLimit(int slot) {
      return 8;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
      // check if there is no other slot containing that item
      for (int i = 0; i < this.getSlots(); i++) {
        // don't compare count
        if (ItemStack.areItemsEqual(stack, this.getStackInSlot(i)) && ItemStack.areItemStackTagsEqual(stack, this.getStackInSlot(i))) {
          return i == slot; // only allowed in the same slot
        }
      }
      return stack.getItem() instanceof IMaterialItem;
    }
  }
}
