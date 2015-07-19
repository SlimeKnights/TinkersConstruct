package tconstruct.tools.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import tconstruct.common.inventory.BaseContainer;

public class ContainerSideInventory extends BaseContainer<TileEntity> {
  public final int columns;
  public final int slotCount;

  public ContainerSideInventory(TileEntity tile, IInventory inventory, int x, int y, int columns) {
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
        this.addSlotToContainer(new Slot(inventory, index, x + c*18, y + r*18));
        index++;
      }
    }
  }

  public int getSlotCount() { return slotCount; }
}
