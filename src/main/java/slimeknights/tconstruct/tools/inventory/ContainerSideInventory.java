package slimeknights.tconstruct.tools.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import slimeknights.mantle.inventory.BaseContainer;

public class ContainerSideInventory<T extends TileEntity & IInventory> extends BaseContainer<T> {
  public final int columns;
  public final int slotCount;

  public ContainerSideInventory(T tile, IInventory inventory, int x, int y, int columns) {
    super(tile);

    this.columns = columns;
    this.slotCount = inventory.getSizeInventory();

    int rows = inventory.getSizeInventory() / columns;
    if(inventory.getSizeInventory() % columns != 0)
      rows++;

    int index = 0;
    for(int r = 0; r < rows; r++) {
      for(int c = 0; c < columns; c++) {
        if(index >= inventory.getSizeInventory())
          break;
        this.addSlotToContainer(createSlot(inventory, index, x + c*18, y + r*18));
        index++;
      }
    }
  }

  protected Slot createSlot(IInventory inventory, int index, int x, int y) {
    return new Slot(inventory, index, x, y);
  }

  public int getSlotCount() { return slotCount; }

  public int getSizeInventory() {
    return tile.getSizeInventory();
  }
}
