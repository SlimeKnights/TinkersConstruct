package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;

import lombok.Getter;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.table.LazyResultSlot;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TinkerStationContainer extends BaseStationContainer<TinkerStationTileEntity> {
  @Getter
  private final List<Slot> inputSlots;
  private final LazyResultSlot resultSlot;

  /**
   * Standard constructor
   * @param id    Window ID
   * @param inv   Player inventory
   * @param tile  Relevant tile entity
   */
  public TinkerStationContainer(int id, PlayerInventory inv, @Nullable TinkerStationTileEntity tile) {
    super(TinkerTables.tinkerStationContainer, id, inv, tile);

    // unfortunately, nothing works with no tile
    if (tile != null) {
      // send the player the current recipe, as we only sync to open containers
      tile.syncRecipe(inv.player);


      inputSlots = new ArrayList<>();
      inputSlots.add(this.addSlot(new TinkerableSlot(tile, TinkerStationTileEntity.TINKER_SLOT, 0, 0)));

      int index;
      for (index = 0; index < tile.size() - 1; index++) {
        inputSlots.add(this.addSlot(new TinkerStationInputSlot(tile, index + TinkerStationTileEntity.INPUT_SLOT, 0, 0)));
      }

      // add result slot, will fetch result cache
      this.addSlot(this.resultSlot = new LazyResultSlot(tile.getCraftingResult(), 124, 37));
    }
    else {
      // requirement for final variable
      this.inputSlots = Collections.emptyList();
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
  public TinkerStationContainer(int id, PlayerInventory inv, PacketByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, TinkerStationTileEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 92;
  }

  @Override
  public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
    return slot != this.resultSlot && super.canInsertIntoSlot(stack, slot);
  }

  /**
   * Updates the active slots from the screen
   * @param activeSlots     Active slots
   * @param mainSlotHidden  If true, main slot is hidden
   */
  public void setToolSelection(int activeSlots, boolean mainSlotHidden) {
    assert this.tile != null;

    if (activeSlots > this.tile.size()) {
      activeSlots = this.tile.size();
    }

    for (int i = 0; i < this.tile.size(); i++) {
      Slot slot = this.slots.get(i);

      if (slot instanceof TinkerStationSlot) {
        TinkerStationSlot slotToolPart = (TinkerStationSlot) slot;
        if (i == TinkerStationTileEntity.TINKER_SLOT ? mainSlotHidden : i > activeSlots) {
          slotToolPart.deactivate();
        }
        else {
          slotToolPart.activate();
        }
      }
    }
  }

  public ItemStack getResult() {
    return this.resultSlot.getStack();
  }
}
