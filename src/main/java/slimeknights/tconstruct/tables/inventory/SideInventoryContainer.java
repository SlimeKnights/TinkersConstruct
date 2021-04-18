package slimeknights.tconstruct.tables.inventory;

import lombok.Getter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import java.util.Objects;

public class SideInventoryContainer<TILE extends BlockEntity> extends BaseContainer<TILE> {

  @Getter
  private final int columns;
  @Getter
  private final int slotCount;
  protected final Inventory itemHandler;

  public SideInventoryContainer(ScreenHandlerType<?> containerType, int windowId, PlayerInventory inv, @Nullable TILE tile, int x, int y, int columns) {
    this(containerType, windowId, inv, tile, null, x, y, columns);
  }

  public SideInventoryContainer(ScreenHandlerType<?> containerType, int windowId, PlayerInventory inv, @Nullable TILE tile, @Nullable Direction inventoryDirection, int x, int y, int columns) {
    super(containerType, windowId, inv, tile);

    if(tile instanceof SmelteryTileEntity) {
      this.itemHandler = ((SmelteryTileEntity) tile).meltingInventory;
    } else if(tile instanceof CraftingStationTileEntity) {
      this.itemHandler = ((CraftingStationTileEntity) tile).craftingInventory;
    } else {
      this.itemHandler = new SimpleInventory();
    }

    // slot properties
    this.slotCount = itemHandler.size();
    this.columns = columns;
    int rows = this.slotCount / columns;
    if (this.slotCount % columns != 0) {
      rows++;
    }

    // add slots
    int index = 0;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        if (index >= this.slotCount) {
          break;
        }
        this.addSlot(this.createSlot(itemHandler, index, x + c * 22, y + r * 18));
        index++;
      }
    }
  }

  /**
   * Creates a slot for this inventory
   * @param itemHandler  Item handler
   * @param index        Slot index
   * @param x            Slot X position
   * @param y            Slot Y position
   * @return  Inventory slot
   */
  protected Slot createSlot(Inventory itemHandler, int index, int x, int y) {
    return new Slot(itemHandler, index, x, y);
  }
}
