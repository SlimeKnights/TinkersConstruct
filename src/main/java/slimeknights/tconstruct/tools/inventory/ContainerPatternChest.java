package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import slimeknights.tconstruct.tools.tileentity.TilePatternChest;

public class ContainerPatternChest extends ContainerTinkerStation<TilePatternChest> {

  public ContainerPatternChest(int rows, int columns, InventoryPlayer playerInventory, TilePatternChest tile) {
    super(tile);

    int index = 0;

    // chest inventory. we have it as a module
    ContainerSideInventory inv = new SideInventory(tile, tile, 8, 18, columns);
    /*for(int i = 0; i < rows; ++i) {
      for(int j = 0; j < columns; ++j) {
        // safety
        if(index > tile.getSizeInventory()) {
          break;
        }

        this.addSlotToContainer(new SlotStencil(tile, index, 8 + j * 18, 18 + i * 18));
        index++;
      }
    }*/
    this.addSubContainer(inv, true);

    // player inventory
    this.addPlayerInventory(playerInventory, 8, 84);
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
