package slimeknights.tconstruct.tables.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import slimeknights.mantle.inventory.BaseContainer;

import javax.annotation.Nullable;

public class SideInventoryContainer<TILE extends TileEntity> extends BaseContainer<TILE> {

  public final int columns;
  public final int slotCount;
  protected final LazyOptional<IItemHandler> itemHandler;

  public SideInventoryContainer(ContainerType<?> containerType, int windowId, PlayerInventory inv, @Nullable TILE tile, int x, int y, int columns) {
    this(containerType, windowId, inv, tile, null, x, y, columns);
  }

  public SideInventoryContainer(ContainerType<?> containerType, int windowId, PlayerInventory inv, TILE tile, @Nullable Direction inventoryDirection, int x, int y, int columns) {
    super(containerType, windowId, inv, tile);

    if (tile == null) {
      this.itemHandler = LazyOptional.of(EmptyHandler::new);
    } else {
      if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryDirection).isPresent()) {
        this.itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryDirection);
      } else {
        this.itemHandler = LazyOptional.of(EmptyHandler::new);
      }
    }

    this.columns = columns;
    this.slotCount = this.getItemHandler().getSlots();

    int rows = this.slotCount / columns;

    if (this.slotCount % columns != 0) {
      rows++;
    }

    int index = 0;

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        if (index >= this.slotCount) {
          break;
        }

        this.addSlot(this.createSlot(this.getItemHandler(), index, x + c * 18, y + r * 18));
        index++;
      }
    }
  }

  private IItemHandler getItemHandler() {
    return this.itemHandler.orElseGet(EmptyHandler::new);
  }

  protected Slot createSlot(IItemHandler itemHandler, int index, int x, int y) {
    return new SlotItemHandler(itemHandler, index, x, y);
  }

  public int getSlotCount() {
    return this.slotCount;
  }

  public int getSizeInventory() {
    return this.getItemHandler().getSlots();
  }
}
