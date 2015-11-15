package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import slimeknights.tconstruct.tools.tileentity.TilePatternChest;

public class ContainerPatternChest extends ContainerTinkerStation<TilePatternChest> {

  protected ContainerSideInventory inventory;

  public ContainerPatternChest(InventoryPlayer playerInventory, TilePatternChest tile) {
    super(tile);

    // chest inventory. we have it as a module
    inventory = new SideInventory(tile, tile, 8, 18, 9); // columns don't matter since they get set by gui
    this.addSubContainer(inventory, true);

    // player inventory
    this.addPlayerInventory(playerInventory, 8, 84);
  }

  public static class SideInventory extends ContainerSideInventory {

    public SideInventory(TileEntity tile, IInventory inventory, int x, int y, int columns) {
      super(tile, inventory, x, y, columns);
    }

    @Override
    protected Slot createSlot(IInventory inventory, int index, int x, int y) {
      return new SlotPatternChest((TilePatternChest)tile, index, x, y);
    }
  }

  public static class SlotPatternChest extends SlotStencil {

    public final TilePatternChest patternChest;

    public SlotPatternChest(TilePatternChest inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);

      this.patternChest = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return super.isItemValid(stack) && patternChest.isItemValidForSlot(0, stack); // slot parameter is unused
    }
  }
}
