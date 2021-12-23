package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tables.TinkerTables;

/**
 * Chest that holds casts, up to 4 of every type
 */
public class CastChestTileEntity extends ChestTileEntity {
  public CastChestTileEntity() {
    super(TinkerTables.castChestTile.get(), TConstruct.makeTranslationKey("gui", "cast_chest"), new CastChestIItemHandler());
  }

  /** Item handler for cast chests */
  public static class CastChestIItemHandler extends ScalingChestItemHandler {
    @Override
    public int getSlotLimit(int slot) {
      return 4;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
      for (int i = 0; i < this.getSlots(); i++) {
        if (ItemStack.areItemsEqual(stack, this.getStackInSlot(i))) {
          return i == slot;
        }
      }
      return TinkerTags.Items.GOLD_CASTS.contains(stack.getItem());
    }
  }
}
