package tconstruct.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import tconstruct.common.tileentity.TileSimpleChest;

public class ContainerSimpleChest extends BaseContainer<TileSimpleChest> {

  public ContainerSimpleChest(TileSimpleChest tile, int rows, int columns, InventoryPlayer playerInventory) {
    super(tile);

    int index = 0;

    // chest inventory
    for(int i = 0; i < rows; ++i) {
      for(int j = 0; j < columns; ++j) {
        // safety
        if(index > tile.getInventory().getSizeInventory()) {
          break;
        }

        this.addSlotToContainer(createSlot(tile.getInventory(), index, 8 + j * 18, 18 + i * 18));
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
