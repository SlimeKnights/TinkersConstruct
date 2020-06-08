package slimeknights.tconstruct.tables.inventory.chest;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

import javax.annotation.Nullable;

public class PatternChestContainer extends TinkerStationContainer<PatternChestTileEntity> {

  protected SideInventoryContainer<PatternChestTileEntity> inventory;

  public PatternChestContainer(int id, PlayerInventory inv, PatternChestTileEntity tileEntity) {
    super(TinkerTables.patternChestContainer.get(), id, inv, tileEntity);

    this.inventory = new PatternChestContainer.DynamicChestInventory(TinkerTables.patternChestContainer.get(), this.windowId, inv, this.tile, 8, 18, 8); // columns don't matter since they get set by gui
    this.addSubContainer(inventory, true);

    this.addInventorySlots();
  }

  public PatternChestContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, PatternChestTileEntity.class));
  }

  public static class DynamicChestInventory extends SideInventoryContainer<PatternChestTileEntity> {

    public DynamicChestInventory(ContainerType<?> containerType, int windowId, PlayerInventory inv, PatternChestTileEntity tile, int x, int y, int columns) {
      super(containerType, windowId, inv, tile, x, y, columns);

      // add the theoretically possible slots
      while (this.inventorySlots.size() < TinkerChestTileEntity.MAX_INVENTORY) {
        this.addSlot(this.createSlot(new EmptyHandler(), this.inventorySlots.size(), 0, 0));
      }
    }

    @Override
    protected Slot createSlot(IItemHandler inventory, int index, int x, int y) {
      return new PatternChestSlot(this.tile, index, x, y);
    }
  }

  public static class PatternChestSlot extends StencilSlot {

    @Nullable
    public final PatternChestTileEntity patternChestTileEntity;

    public PatternChestSlot(@Nullable PatternChestTileEntity tileEntity, int index, int xPosition, int yPosition) {
      super(tileEntity, index, xPosition, yPosition, false);

      this.patternChestTileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      if (this.patternChestTileEntity == null) {
        return true;
      }
      return this.patternChestTileEntity.isItemValidForSlot(this.getSlotIndex(), stack); // slot parameter is unused
    }
  }
}
