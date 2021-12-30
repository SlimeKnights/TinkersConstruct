package slimeknights.tconstruct.tables.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.table.CraftingStationBlockEntity;
import slimeknights.tconstruct.tables.menu.slot.PlayerSensitiveLazyResultSlot;

import javax.annotation.Nullable;

public class CraftingStationContainerMenu extends TabbedContainerMenu<CraftingStationBlockEntity> {
  private final PlayerSensitiveLazyResultSlot resultSlot;

  /**
   * Standard constructor
   * @param id    Window ID
   * @param inv   Player inventory
   * @param tile  Relevant tile entity
   */
  public CraftingStationContainerMenu(int id, Inventory inv, @Nullable CraftingStationBlockEntity tile) {
    super(TinkerTables.craftingStationContainer.get(), id, inv, tile);

    // unfortunately, nothing works with no tile
    if (tile != null) {
      // send the player the current recipe, as we only sync to open containers
      tile.syncRecipe(inv.player);

      // add crafting slots first, as each added slot will clear the result cache
      for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
          this.addSlot(new Slot(tile, col + row * 3, 30 + col * 18, 17 + row * 18));
        }
      }
      // add result slot, will fetch result cache
      this.addSlot(resultSlot = new PlayerSensitiveLazyResultSlot(inv.player, tile.getCraftingResult(), 124, 35));

      this.addChestSideInventory();
    } else {
      // requirement for final variable
      resultSlot = null;
    }

    this.addInventorySlots();
  }

  /**
   * Factory constructor
   * @param id   Window ID
   * @param inv  Player inventory
   * @param buf  Buffer for fetching tile
   */
  public CraftingStationContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, CraftingStationBlockEntity.class));
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = this.slots.get(index);
    // fix issue on shift clicking from the result slot if the recipe result mismatches the displayed item
    if (slot == resultSlot) {
      if (tile != null && slot.hasItem()) {
        // return the original result so shift click works
        ItemStack original = slot.getItem().copy(); // TODO: are these copies really needed?
        // but add the true result into the inventory
        ItemStack result = tile.getResultForPlayer(player);
        if (!result.isEmpty()) {
          boolean nothingDone = true;
          if (subContainers.size() > 0) { // the sub container check does not do well with 0 sub containers
            nothingDone = this.refillAnyContainer(result, this.subContainers);
          }
          nothingDone &= this.moveToPlayerInventory(result);
          if (subContainers.size() > 0) {
            nothingDone &= this.moveToAnyContainer(result, this.subContainers);
          }
          // if successfully added to an inventory, update
          if (!nothingDone) {
            tile.takeResult(player, result, result.getCount());
            tile.getCraftingResult().clearContent();
            return original;
          }
        } else {
          tile.notifyUncraftable(player);
        }
      }
      return ItemStack.EMPTY;
    } else {
      return super.quickMoveStack(player, index);
    }
  }

  @Override
  public void slotsChanged(Container inventoryIn) {
    // handled in TE item display logic
  }

  @Override
  public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
    return slot != this.resultSlot && super.canTakeItemForPickAll(stack, slot);
  }
}
