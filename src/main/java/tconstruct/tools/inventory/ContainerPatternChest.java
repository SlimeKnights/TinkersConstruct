package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import tconstruct.common.inventory.ContainerSimpleChest;
import tconstruct.common.tileentity.TileSimpleChest;

public class ContainerPatternChest extends ContainerSimpleChest {

  public ContainerPatternChest(int rows, int columns, InventoryPlayer playerInventory, TileSimpleChest tile) {
    super(tile, rows, columns, playerInventory);
  }

  @Override
  protected Slot createSlot(IInventory inventory, int index, int x, int y) {
    return super.createSlot(inventory, index, x, y);
  }
}
