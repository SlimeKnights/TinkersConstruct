package slimeknights.tconstruct.tables.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.tileentity.chest.ChestTileEntity;

import javax.annotation.Nullable;

public class TinkerChestContainer extends BaseStationContainer<ChestTileEntity> {
  protected SideInventoryContainer<ChestTileEntity> inventory;
  public TinkerChestContainer(int id, Inventory inv, @Nullable ChestTileEntity tileEntity) {
    super(TinkerTables.tinkerChestContainer.get(), id, inv, tileEntity);
    // columns don't matter since they get set by gui
    if (this.tile != null) {
      this.inventory = new DynamicChestInventory(TinkerTables.tinkerChestContainer.get(), this.containerId, inv, this.tile, 8, 18, 8);
      this.addSubContainer(inventory, true);
    }
    this.addInventorySlots();
  }

  public TinkerChestContainer(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, ChestTileEntity.class));
  }

  /** Resizable inventory */
  public static class DynamicChestInventory extends SideInventoryContainer<ChestTileEntity> {
    public DynamicChestInventory(MenuType<?> containerType, int windowId, Inventory inv, ChestTileEntity tile, int x, int y, int columns) {
      super(containerType, windowId, inv, tile, x, y, columns);
    }
  }
}
