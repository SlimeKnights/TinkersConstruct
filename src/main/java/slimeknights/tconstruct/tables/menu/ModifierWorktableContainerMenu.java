package slimeknights.tconstruct.tables.menu;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.util.sync.LambdaDataSlot;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.table.ModifierWorktableBlockEntity;
import slimeknights.tconstruct.tables.menu.slot.ArmorSlot;
import slimeknights.tconstruct.tables.menu.slot.LazyResultSlot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModifierWorktableContainerMenu extends TabbedContainerMenu<ModifierWorktableBlockEntity> {
  // slots
  @Getter
  private final List<Slot> inputSlots;
  @Getter
  private final LazyResultSlot outputSlot;

  public ModifierWorktableContainerMenu(int windowIdIn, Inventory inv, @Nullable ModifierWorktableBlockEntity tile) {
    super(TinkerTables.modifierWorktableContainer.get(), windowIdIn, inv, tile);

    // unfortunately, nothing works with no tile
    if (tile != null) {
      // send the player the current recipe, as we only sync to open containers
//      tile.syncRecipe(inv.player);
      // slots
      // inputs
      inputSlots = new ArrayList<>();
      inputSlots.add(this.addSlot(new WorktableSlot(this, tile, ModifierWorktableBlockEntity.TINKER_SLOT, 8, 21)));
      for (int index = 0; index < tile.getContainerSize() - 1; index++) {
        inputSlots.add(this.addSlot(new WorktableSlot(this, tile, index + ModifierWorktableBlockEntity.INPUT_START, 8, 45 + 22*index)));
      }
      // result
      this.addSlot(this.outputSlot = new LazyResultSlot(tile.getCraftingResult(), 125, 42));

      // listen for the button to change in the tile
      this.addDataSlot(new LambdaDataSlot(-1, tile::getSelectedIndex, i -> {
        tile.selectModifier(i);
        this.updateScreen();
      }));
      // update for the first time
      this.updateScreen();
    } else {
      this.inputSlots = Collections.emptyList();
      this.outputSlot = null;
    }

    // add armor and offhand slots, for convenience
    for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
      this.addSlot(new ArmorSlot(inv, slotType.getSlot(), 152, 16 + slotType.ordinal() * 18));
    }
    this.addSlot(new Slot(inv, 40, 132, 70).setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));

    // other inventories
    this.addChestSideInventory();
    this.addInventorySlots();
  }

  public ModifierWorktableContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, ModifierWorktableBlockEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 102;
  }

  @Override
  public void slotsChanged(Container inventoryIn) {

  }

  /**
   * Called when a pattern button is pressed
   */
  @Override
  public boolean clickMenuButton(Player playerIn, int id) {
    // no letting ghosts choose modifiers
    if (playerIn.isSpectator()) {
      return false;
    }
    if (id >= 0 && tile != null) {
      tile.selectModifier(id);
    }
    return true;
  }

  @Override
  public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
    return slotIn != this.outputSlot && super.canTakeItemForPickAll(stack, slotIn);
  }

  /** Slot to update recipe on change */
  private static class WorktableSlot extends Slot {

    private final TabbedContainerMenu<?> menu;
    private final ModifierWorktableBlockEntity tile;
    public WorktableSlot(TabbedContainerMenu<?> menu, ModifierWorktableBlockEntity tile, int index, int xPosition, int yPosition) {
      super(tile, index, xPosition, yPosition);
      this.menu = menu;
      this.tile = tile;
    }

    @Override
    public void setChanged() {
      tile.onSlotChanged(index);
      super.setChanged();
      menu.updateScreen();
    }
  }
}
