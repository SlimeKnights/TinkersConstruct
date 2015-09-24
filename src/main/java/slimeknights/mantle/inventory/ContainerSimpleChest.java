package slimeknights.mantle.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import slimeknights.mantle.tileentity.TileInventory;

public class ContainerSimpleChest extends BaseContainer<TileInventory> {

  public ContainerSimpleChest(TileInventory tile, int rows, int columns, InventoryPlayer playerInventory) {
    super(tile);

    int index = 0;

    // chest inventory
    for(int i = 0; i < rows; ++i) {
      for(int j = 0; j < columns; ++j) {
        // safety
        if(index > tile.getSizeInventory()) {
          break;
        }

        this.addSlotToContainer(createSlot(tile, index, 8 + j * 18, 18 + i * 18));
        index++;
      }
    }

    // player inventory
    addPlayerInventory(playerInventory, 17, 86);
  }

  protected Slot createSlot(IInventory inventory, int index, int x, int y) {
    return new Slot(inventory, index, x, y);
  }
}
