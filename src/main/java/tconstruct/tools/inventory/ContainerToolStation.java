package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TileToolStation;

public class ContainerToolStation extends ContainerMultiModule<TileToolStation> {

  public ContainerToolStation(InventoryPlayer playerInventory, TileToolStation tile) {
    super(tile);

    // output slot
    addSlotToContainer(new Slot(tile, 0, 124,38));

    // modular slots for input
    // Area: 7,18, 80x64

    // slot1 - center for repairing/main slot
    int x = 7 + 80/2 - 8;
    int y = 18 + 64/2 - 8;

    x -= 6;

    addSlotToContainer(new Slot(tile, 0, x, y));

    addSlotToContainer(new Slot(tile, 1, x-18, y+20)); // -20,+20
    addSlotToContainer(new Slot(tile, 2, x-22, y-5));  // -22, -7
    addSlotToContainer(new Slot(tile, 3, x, y-23));    // +-0, -21
    addSlotToContainer(new Slot(tile, 4, x+22, y-5));  // +22, -7
    addSlotToContainer(new Slot(tile, 5, x+18, y+20)); // +20,+20

    this.addPlayerInventory(playerInventory, 8, 84 + 8);
  }
}
