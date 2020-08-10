package slimeknights.tconstruct.tables.inventory.chest;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

import javax.annotation.Nullable;

public class PartChestContainer extends TinkerStationContainer<PartChestTileEntity> {

  protected SideInventoryContainer<PartChestTileEntity> inventory;

  public PartChestContainer(int id, PlayerInventory inv, @Nullable PartChestTileEntity tileEntity) {
    super(TinkerTables.partChestContainer.get(), id, inv, tileEntity);

    this.inventory = new PartChestContainer.DynamicChestInventory(TinkerTables.partChestContainer.get(), windowId, inv, tile, 8, 18, 8); // columns don't matter since they get set by gui
    this.addSubContainer(inventory, true);

    this.addInventorySlots();
  }

  public PartChestContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, PartChestTileEntity.class));
  }

  public static class DynamicChestInventory extends SideInventoryContainer<PartChestTileEntity> {

    public DynamicChestInventory(ContainerType<?> containerType, int windowId, PlayerInventory inv, @Nullable PartChestTileEntity tile, int x, int y, int columns) {
      super(containerType, windowId, inv, tile, x, y, columns);

      // add the theoretically possible slots
      while (this.inventorySlots.size() < TinkerChestTileEntity.MAX_INVENTORY) {
        this.addSlot(this.createSlot(new EmptyHandler(), this.inventorySlots.size(), 0, 0));
      }
    }

    @Override
    protected Slot createSlot(IItemHandler itemHandler, int index, int x, int y) {
      return new PartSlot(this.tile, index, x, y, this.itemHandler.orElseGet(EmptyHandler::new));
    }
  }

  public static class PartSlot extends SlotItemHandler {

    @Nullable
    public final PartChestTileEntity partChestTileEntity;

    public PartSlot(@Nullable PartChestTileEntity tileEntity, int index, int xPosition, int yPosition, IItemHandler itemHandler) {
      super(itemHandler, index, xPosition, yPosition);

      this.partChestTileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      if (this.partChestTileEntity == null) {
        return true;
      }
      return this.partChestTileEntity.isItemValidForSlot(this.getSlotIndex(), stack); // slot parameter is unused
    }
  }
}
