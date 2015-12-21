package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import slimeknights.tconstruct.tools.tileentity.TileTinkerChest;
import slimeknights.tconstruct.tools.tileentity.TilePartChest;

public class ContainerPartChest extends ContainerTinkerStation<TilePartChest> {

  protected ContainerSideInventory inventory;

  public ContainerPartChest(InventoryPlayer playerInventory, TilePartChest tile) {
    super(tile);

    // chest inventory. we have it as a module
    inventory = new DynamicChestInventory(tile, tile, 8, 18, 8); // columns don't matter since they get set by gui
    this.addSubContainer(inventory, true);

    // player inventory
    this.addPlayerInventory(playerInventory, 8, 84);
  }

  // dynamic chest inventory as a module
  public static class DynamicChestInventory extends ContainerSideInventory {

    public DynamicChestInventory(TileEntity tile, IInventory inventory, int x, int y, int columns) {
      super(tile, inventory, x, y, columns);

      // add the theoretically possible slots
      while(this.inventorySlots.size() < TileTinkerChest.MAX_INVENTORY) {
        this.addSlotToContainer(createSlot(inventory, this.inventorySlots.size(), 0,0));
      }
    }

    @Override
    protected Slot createSlot(IInventory inventory, int index, int x, int y) {
      return new PartSlot((TilePartChest) tile, index, x, y);
    }
  }

  // slot that only accepts parts
  public static class PartSlot extends Slot {
    private final TilePartChest tile;

    public PartSlot(TilePartChest tile, int index, int xPosition, int yPosition) {
      super(tile, index, xPosition, yPosition);

      this.tile = tile;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return tile.isItemValidForSlot(this.getSlotIndex(), stack);
    }
  }
}
