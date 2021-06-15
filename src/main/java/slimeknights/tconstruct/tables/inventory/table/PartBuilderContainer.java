package slimeknights.tconstruct.tables.inventory.table;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import slimeknights.tconstruct.library.utils.LambdaIntReference;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nullable;

public class PartBuilderContainer extends BaseStationContainer<PartBuilderTileEntity> {
  // slots
  @Getter
  private final Slot patternSlot;
  @Getter
  private final Slot inputSlot;
  @Getter
  private final LazyResultSlot outputSlot;

  public PartBuilderContainer(int windowIdIn, PlayerInventory playerInventoryIn, @Nullable PartBuilderTileEntity partBuilderTileEntity) {
    super(TinkerTables.partBuilderContainer.get(), windowIdIn, playerInventoryIn, partBuilderTileEntity);

    // unfortunately, nothing works with no tile
    if (tile != null) {
      // slots
      this.addSlot(this.outputSlot = new LazyResultSlot(tile.getCraftingResult(), 148, 33));
      // inputs
      this.addSlot(this.patternSlot = new PatternSlot(tile, 8, 34));
      this.addSlot(this.inputSlot = new Slot(tile, PartBuilderTileEntity.MATERIAL_SLOT, 29, 34));

      // other inventories
      this.addChestSideInventory();
      this.addInventorySlots();

      // listen for the button to change in the tile
      this.trackInt(new LambdaIntReference(-1, tile::getSelectedIndex, i -> {
        tile.selectRecipe(i);
        this.updateScreen();
      }));
      // update for the first time
      this.updateScreen();
    } else {
      this.patternSlot = null;
      this.inputSlot = null;
      this.outputSlot = null;
    }
  }

  public PartBuilderContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, PartBuilderTileEntity.class));
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {}

  /**
   * Called when a pattern button is pressed
   */
  @Override
  public boolean enchantItem(PlayerEntity playerIn, int id) {
    if (id >= 0 && tile != null) {
      tile.selectRecipe(id);
    }
    return true;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
    return slotIn != this.outputSlot && super.canMergeSlot(stack, slotIn);
  }

  /**
   * Slot for the pattern, updates buttons on change
   */
  private static class PatternSlot extends Slot {
    private PatternSlot(PartBuilderTileEntity tile, int x, int y) {
      super(tile, PartBuilderTileEntity.PATTERN_SLOT, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      // TODO: tag
      return stack.getItem() == TinkerTables.pattern.get();
    }
  }
}
