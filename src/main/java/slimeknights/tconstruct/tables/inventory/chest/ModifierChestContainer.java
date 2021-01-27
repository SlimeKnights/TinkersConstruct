package slimeknights.tconstruct.tables.inventory.chest;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.tileentity.chest.ModifierChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

import javax.annotation.Nullable;

public class ModifierChestContainer extends BaseStationContainer<ModifierChestTileEntity> {

  protected SideInventoryContainer<ModifierChestTileEntity> inventory;

  public ModifierChestContainer(int id, PlayerInventory inv, ModifierChestTileEntity tileEntity) {
    super(TinkerTables.modifierChestContainer.get(), id, inv, tileEntity);

    this.inventory = new ModifierChestContainer.DynamicChestInventory(TinkerTables.modifierChestContainer.get(), this.windowId, inv, this.tile, 8, 18, 8); // columns don't matter since they get set by gui
    this.addSubContainer(inventory, true);

    this.addInventorySlots();
  }

  public ModifierChestContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, ModifierChestTileEntity.class));
  }

  public static class DynamicChestInventory extends SideInventoryContainer<ModifierChestTileEntity> {

    public DynamicChestInventory(ContainerType<?> containerType, int windowId, PlayerInventory inv, ModifierChestTileEntity tile, int x, int y, int columns) {
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

  public static class PatternChestSlot extends PatternSlot {

    @Nullable
    public final ModifierChestTileEntity modifierChestTileEntity;

    public PatternChestSlot(@Nullable ModifierChestTileEntity tileEntity, int index, int xPosition, int yPosition) {
      super(tileEntity, index, xPosition, yPosition);

      this.modifierChestTileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      if (this.modifierChestTileEntity == null) {
        return true;
      }

      return this.modifierChestTileEntity.isItemValidForSlot(this.getSlotIndex(), stack); // slot parameter is unused
    }
  }
}
