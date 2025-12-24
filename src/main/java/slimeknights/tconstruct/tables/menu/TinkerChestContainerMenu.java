package slimeknights.tconstruct.tables.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.chest.AbstractChestBlockEntity;
import slimeknights.tconstruct.tables.menu.module.SideInventoryContainer;

import javax.annotation.Nullable;

public class TinkerChestContainerMenu extends TabbedContainerMenu<AbstractChestBlockEntity> {
  protected SideInventoryContainer<AbstractChestBlockEntity> inventory;
  public TinkerChestContainerMenu(int id, Inventory inv, @Nullable AbstractChestBlockEntity tileEntity) {
    super(TinkerTables.tinkerChestContainer.get(), id, inv, tileEntity);
    // columns don't matter since they get set by gui
    if (this.tile != null) {
      this.inventory = new DynamicChestInventory(TinkerTables.tinkerChestContainer.get(), this.containerId, inv, this.tile, 8, 18, 8);
      this.addSubContainer(inventory, true);
    }
    this.addInventorySlots();
  }

  public TinkerChestContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, AbstractChestBlockEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 102;
  }

  /** Resizable inventory */
  public static class DynamicChestInventory extends SideInventoryContainer<AbstractChestBlockEntity> {
    public DynamicChestInventory(MenuType<?> containerType, int windowId, Inventory inv, AbstractChestBlockEntity tile, int x, int y, int columns) {
      super(containerType, windowId, inv, tile, x, y, columns);
    }
  }
}
