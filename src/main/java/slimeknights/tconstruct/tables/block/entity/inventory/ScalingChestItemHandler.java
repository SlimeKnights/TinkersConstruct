package slimeknights.tconstruct.tables.block.entity.inventory;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.mantle.block.entity.MantleBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Base logic for scaling chest inventories */
public abstract class ScalingChestItemHandler extends ItemStackHandler implements IChestItemHandler {
  /** Default maximum size */
  protected static final int DEFAULT_MAX = 256;
  /** Current size for display in containers */
  @Getter
  private int visualSize = 1;
  /** TE owning this inventory */
  @Setter @Nullable
  private MantleBlockEntity parent;

  public ScalingChestItemHandler(int size) {
    super(size);
  }

  public ScalingChestItemHandler() {
    this(DEFAULT_MAX);
  }

  @Override
  public abstract boolean isItemValid(int slot, ItemStack stack);

  @Override
  protected void onLoad() {
    int newLimit = getSlots();
    if (newLimit > 1 && this.getStackInSlot(newLimit - 1).isEmpty()) {
      while (newLimit > 1 && this.getStackInSlot(newLimit - 2).isEmpty()) {
        newLimit--;
      }
    }
    this.visualSize = newLimit;
  }

  /** Updates the visual size of the inventory */
  private void updateVisualSize(int slotChanged, ItemStack stack) {
    // if the slot is too large, nothing to do
    int maxSlots = getSlots();
    if (slotChanged >= maxSlots) {
      return;
    }
    // if the slot is past the current one, update to there
    if (stack.isEmpty()) {
      // if the current index was the last slot, decrease size
      if (slotChanged + 1 == visualSize || (slotChanged + 2 == visualSize && this.getStackInSlot(visualSize - 1).isEmpty())) {
        while (visualSize > 1 && this.getStackInSlot(visualSize - 2).isEmpty()) {
          visualSize--;
        }
      }
    } else {
      // if the current index is past the max, increase visual size to this plus 1
      if (visualSize < maxSlots && visualSize < slotChanged + 2) {
        visualSize =slotChanged + 2;
      }
    }
  }


  /* Hook in visual size update */

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    super.setStackInSlot(slot, stack);
    updateVisualSize(slot, stack);
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    ItemStack result = super.insertItem(slot, stack, simulate);
    if (!simulate) {
      updateVisualSize(slot, getStackInSlot(slot));
    }
    return result;
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    ItemStack result = super.extractItem(slot, amount, simulate);
    if (!simulate) {
      updateVisualSize(slot, getStackInSlot(slot));
    }
    return result;
  }

  @Override
  protected void onContentsChanged(int slot) {
    if (parent != null) {
      parent.setChangedFast();
    }
  }
}
