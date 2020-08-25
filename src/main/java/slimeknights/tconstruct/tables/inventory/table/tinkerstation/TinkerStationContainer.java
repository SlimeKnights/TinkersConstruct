package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.table.LazyResultSlot;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import javax.annotation.Nullable;

public class TinkerStationContainer extends BaseStationContainer<TinkerStationTileEntity> {

  private final LazyResultSlot resultSlot;
  private final TinkerSlot tinkerSlot;

  /**
   * Standard constructor
   * @param id    Window ID
   * @param inv   Player inventory
   * @param tile  Relevant tile entity
   */
  public TinkerStationContainer(int id, PlayerInventory inv, @Nullable TinkerStationTileEntity tile) {
    super(TinkerTables.tinkerStationContainer.get(), id, inv, tile);

    // unfortunately, nothing works with no tile
    if (tile != null) {
      // send the player the current recipe, as we only sync to open containers
      tile.syncRecipe(inv.player);

      int index;

      for (index = 0; index < tile.getSizeInventory() - 1; index++) {
        this.addSlot(new TinkerStationInSlot(tile, index, 0, 0, this));
      }

      this.addSlot(this.tinkerSlot = new TinkerSlot(tile, TinkerStationTileEntity.TINKER_SLOT, 0, 0));

      // add result slot, will fetch result cache
      this.addSlot(this.resultSlot = new LazyResultSlot(tile.getCraftingResult(), 124, 35));
    }
    else {
      // requirement for final variable
      this.tinkerSlot = null;
      this.resultSlot = null;
    }

    this.addInventorySlots();
  }

  /**
   * Factory constructor
   * @param id   Window ID
   * @param inv  Player inventory
   * @param buf  Buffer for fetching tile
   */
  public TinkerStationContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, TinkerStationTileEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 92;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot != this.resultSlot && super.canMergeSlot(stack, slot);
  }

  public void setToolSelection(int activeSlots, boolean mainSlotHidden) {
    assert this.tile != null;

    if (activeSlots > this.tile.getSizeInventory()) {
      activeSlots = this.tile.getSizeInventory();
    }

    for (int i = 0; i < this.tile.getSizeInventory(); i++) {
      Slot slot = this.inventorySlots.get(i);

      if (slot instanceof TinkerStationInSlot) {
        TinkerStationInSlot slotToolPart = (TinkerStationInSlot) slot;

        if (i >= activeSlots) {
          slotToolPart.deactivate();
        }
        else {
          slotToolPart.activate();
        }
      }
      else if (slot instanceof TinkerSlot) {
        TinkerSlot tinkerSlot = (TinkerSlot) slot;

        if (mainSlotHidden) {
          tinkerSlot.deactivate();
        }
        else {
          tinkerSlot.activate();
        }
      }
    }
  }

  public ItemStack getResult() {
    return this.resultSlot.getStack();
  }
}
