package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import tconstruct.common.inventory.ContainerSimpleChest;
import tconstruct.common.inventory.SlotRestrictedItem;
import tconstruct.common.tileentity.TileInventory;
import tconstruct.tools.TinkerTools;

public class ContainerPatternChest extends ContainerSimpleChest {

  public ContainerPatternChest(int rows, int columns, InventoryPlayer playerInventory, TileInventory tile) {
    super(tile, rows, columns, playerInventory);
  }

  @Override
  protected Slot createSlot(IInventory inventory, int index, int x, int y) {
    return new SlotRestrictedItem(TinkerTools.pattern, inventory, index, x,y);
  }
}
