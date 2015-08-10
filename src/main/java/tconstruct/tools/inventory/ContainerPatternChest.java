package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TilePatternChest;

public class ContainerPatternChest extends ContainerTinkerStation<TilePatternChest> {

  public ContainerPatternChest(int rows, int columns, InventoryPlayer playerInventory, TilePatternChest tile) {
    super(tile);

    int index = 0;

    // chest inventory
    for(int i = 0; i < rows; ++i) {
      for(int j = 0; j < columns; ++j) {
        // safety
        if(index > tile.getSizeInventory()) {
          break;
        }

        this.addSlotToContainer(new SlotStencil(tile, index, 8 + j * 18, 18 + i * 18));
        index++;
      }
    }

    // player inventory
    addPlayerInventory(playerInventory, 17, 86);
  }

  public static class SideInventory extends ContainerSideInventory {

    public SideInventory(TileEntity tile, IInventory inventory, int x, int y, int columns) {
      super(tile, inventory, x, y, columns);
    }

    @Override
    protected Slot createSlot(IInventory inventory, int index, int x, int y) {
      return new SlotStencil((TilePatternChest)tile, index, x, y);
    }
  }
}
