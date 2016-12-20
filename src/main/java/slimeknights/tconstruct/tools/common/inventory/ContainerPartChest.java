package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import slimeknights.tconstruct.tools.common.tileentity.TilePartChest;
import slimeknights.tconstruct.tools.common.tileentity.TileTinkerChest;

public class ContainerPartChest extends ContainerTinkerStation<TilePartChest> {

  protected ContainerSideInventory<TilePartChest> inventory;

  public ContainerPartChest(InventoryPlayer playerInventory, TilePartChest tile) {
    super(tile);

    // chest inventory. we have it as a module
    inventory = new DynamicChestInventory(tile, 8, 18, 8); // columns don't matter since they get set by gui
    this.addSubContainer(inventory, true);

    // player inventory
    this.addPlayerInventory(playerInventory, 8, 84);
  }

  // dynamic chest inventory as a module
  public static class DynamicChestInventory extends ContainerSideInventory<TilePartChest> {

    public DynamicChestInventory(TilePartChest tile, int x, int y, int columns) {
      super(tile, x, y, columns);

      // add the theoretically possible slots
      while(this.inventorySlots.size() < TileTinkerChest.MAX_INVENTORY) {
        this.addSlotToContainer(createSlot(itemHandler, this.inventorySlots.size(), 0, 0));
      }
    }

    @Override
    protected Slot createSlot(IItemHandler itemHandler, int index, int x, int y) {
      return new PartSlot(tile, index, x, y);
    }
  }

  // slot that only accepts parts
  public static class PartSlot extends SlotItemHandler {

    private final TilePartChest tile;

    public PartSlot(TilePartChest tile, int index, int xPosition, int yPosition) {
      super(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), index, xPosition, yPosition);

      this.tile = tile;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return tile.isItemValidForSlot(this.getSlotIndex(), stack);
    }
  }
}
