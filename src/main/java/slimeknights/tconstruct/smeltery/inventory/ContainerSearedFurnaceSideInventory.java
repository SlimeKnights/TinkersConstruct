package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;
import slimeknights.tconstruct.tools.common.inventory.ContainerSideInventory;

public class ContainerSearedFurnaceSideInventory extends ContainerSideInventory<TileSearedFurnace> {

  public ContainerSearedFurnaceSideInventory(TileSearedFurnace tile, int x, int y, int columns) {
    super(tile, x, y, columns);
  }

  @Override
  protected Slot createSlot(IItemHandler itemHandler, int index, int x, int y) {
    return new SearedFurnaceSlot(tile, itemHandler, index, x, y);
  }

  private static class SearedFurnaceSlot extends SlotItemHandler {

    private TileSearedFurnace tile;
    private int index;
    public SearedFurnaceSlot(TileSearedFurnace tile, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
      this.tile = tile;
      this.index = index;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return true;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
      return 16;
    }

    @Override
    public void onSlotChanged() {
      // don't update client side, its going to sync in a tick or two
      if(!tile.getWorld().isRemote) {
        this.tile.updateHeatRequired(index);
      }
    }
  }
}
