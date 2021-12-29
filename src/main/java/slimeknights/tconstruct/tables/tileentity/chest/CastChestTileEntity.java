package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tables.TinkerTables;

/**
 * Chest that holds casts, up to 4 of every type
 */
public class CastChestTileEntity extends ChestTileEntity {
  private static final Component NAME = TConstruct.makeTranslation("gui", "cast_chest");
  public CastChestTileEntity(BlockPos pos, BlockState state) {
    super(TinkerTables.castChestTile.get(), pos, state, NAME, new CastChestIItemHandler());
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
        if (ItemStack.isSame(stack, this.getStackInSlot(i))) {
          return i == slot;
        }
      }
      return TinkerTags.Items.GOLD_CASTS.contains(stack.getItem());
    }
  }
}
