package slimeknights.tconstruct.tables.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import slimeknights.tconstruct.misc.IItemHandler;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

import org.jetbrains.annotations.Nullable;

public class TinkerChestContainer extends BaseStationContainer<TinkerChestTileEntity> {
  protected SideInventoryContainer<TinkerChestTileEntity> inventory;
  public TinkerChestContainer(int id, PlayerInventory inv, @Nullable TinkerChestTileEntity tileEntity) {
    super(TinkerTables.tinkerChestContainer, id, inv, tileEntity);
    // columns don't matter since they get set by gui
    if (this.tile != null) {
      this.inventory = new DynamicChestInventory(TinkerTables.tinkerChestContainer, this.syncId, inv, this.tile, 8, 18, 8);
      this.addSubContainer(inventory, true);
    }
    this.addInventorySlots();
  }

  public TinkerChestContainer(int id, PlayerInventory inv, PacketByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, TinkerChestTileEntity.class));
  }

  /** Resizable inventory */
  public static class DynamicChestInventory extends SideInventoryContainer<TinkerChestTileEntity> {
    public DynamicChestInventory(ScreenHandlerType<?> containerType, int windowId, PlayerInventory inv, TinkerChestTileEntity tile, int x, int y, int columns) {
      super(containerType, windowId, inv, tile, x, y, columns);
      // add the theoretically possible slots
      while (this.slots.size() < tile.getMaxInventory()) {
        throw new RuntimeException("CRAB!");
        //TODO: PORT
//        this.addSlot(this.createSlot(new EmptyHandler(), this.slots.size(), 0, 0));
      }
    }
//
//    @Override
//    protected Slot createSlot(IItemHandler inventory, int index, int x, int y) {
//      if (this.tile == null) {
//        return super.createSlot(inventory, index, x, y);
//      }
//      return new ChestSlot(this.tile, index, x, y);
//    }
  }



  /** Slot to filter chest contents */
  public static class ChestSlot extends Slot {
    public final TinkerChestTileEntity chest;
    public ChestSlot(TinkerChestTileEntity tileEntity, int index, int xPosition, int yPosition) {
      super(tileEntity, index, xPosition, yPosition);
      this.chest = tileEntity;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
      System.out.println("Crab Warn TinkerChestContainer");
      return true;
//      return this.chest.isValid(this.getSlotIndex(), stack);
    }
  }
}
