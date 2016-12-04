package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.common.inventory.ContainerSideInventory;

public class ContainerSmelterySideInventory extends ContainerSideInventory<TileSmeltery> {

  public ContainerSmelterySideInventory(TileSmeltery tile, int x, int y, int columns) {
    super(tile, x, y, columns);
  }

  @Override
  protected Slot createSlot(IItemHandler itemHandler, int index, int x, int y) {
    return new SmelterySlot(itemHandler, index, x, y);
  }

  private static class SmelterySlot extends SlotItemHandler {

    public SmelterySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return true;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
      return 1;
    }
  }
}
