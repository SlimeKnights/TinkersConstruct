package slimeknights.tconstruct.tables.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

import javax.annotation.Nullable;

public class TinkerChestContainer extends BaseStationContainer<TinkerChestTileEntity> {
  protected SideInventoryContainer<TinkerChestTileEntity> inventory;
  public TinkerChestContainer(int id, PlayerInventory inv, @Nullable TinkerChestTileEntity tileEntity) {
    super(TinkerTables.tinkerChestContainer.get(), id, inv, tileEntity);
    // columns don't matter since they get set by gui
    if (this.tile != null) {
      this.inventory = new TinkerChestContainer.DynamicChestInventory(TinkerTables.tinkerChestContainer.get(), this.windowId, inv, this.tile, 8, 18, 8);
      this.addSubContainer(inventory, true);
    }
    this.addInventorySlots();
  }

  public TinkerChestContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, TinkerChestTileEntity.class));
  }

  /** Resizable inventory */
  public static class DynamicChestInventory extends SideInventoryContainer<TinkerChestTileEntity> {
    public DynamicChestInventory(ContainerType<?> containerType, int windowId, PlayerInventory inv, TinkerChestTileEntity tile, int x, int y, int columns) {
      super(containerType, windowId, inv, tile, x, y, columns);
      // add the theoretically possible slots
      while (this.inventorySlots.size() < tile.getMaxInventory()) {
        this.addSlot(this.createSlot(new EmptyHandler(), this.inventorySlots.size(), 0, 0));
      }
    }

    @Override
    protected Slot createSlot(IItemHandler inventory, int index, int x, int y) {
      if (this.tile == null) {
        return super.createSlot(inventory, index, x, y);
      }
      return new ChestSlot(this.tile, index, x, y);
    }
  }

  /** Slot to filter chest contents */
  public static class ChestSlot extends Slot {
    public final TinkerChestTileEntity chest;
    public ChestSlot(TinkerChestTileEntity tileEntity, int index, int xPosition, int yPosition) {
      super(tileEntity, index, xPosition, yPosition);
      this.chest = tileEntity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return this.chest.isItemValidForSlot(this.getSlotIndex(), stack);
    }
  }
}
