package slimeknights.tconstruct.shared.inventory;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PersistentCraftingInventory extends CraftingInventory {

  private final int length;
  private final Container eventHandler;
  private final IInventory parent;
  private boolean doNotCallUpdates;

  public PersistentCraftingInventory(Container eventHandler, IInventory parent, int width, int height) {
    super(eventHandler, width, height);
    int k = width * height;

    assert (k == parent.getSizeInventory());

    this.parent = parent;
    this.length = k;
    this.eventHandler = eventHandler;
    this.doNotCallUpdates = false;
  }

  @Override
  public int getSizeInventory() {
    return this.length;
  }

  @Override
  public boolean isEmpty() {
    return this.parent.isEmpty();
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int index) {
    return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.parent.getStackInSlot(index);
  }

  @Nonnull
  public ItemStack getStackInSlotOnClosing(int index) {
    return ItemStack.EMPTY;
  }

  @Nonnull
  @Override
  public ItemStack decrStackSize(int index, int count) {
    if (!this.getStackInSlot(index).isEmpty()) {
      ItemStack itemstack;

      if (this.getStackInSlot(index).getCount() <= count) {
        itemstack = this.getStackInSlot(index);
        this.setInventorySlotContents(index, ItemStack.EMPTY);
        return itemstack;
      }
      else {
        itemstack = this.getStackInSlot(index).split(count);

        if (this.getStackInSlot(index).getCount() == 0) {
          this.setInventorySlotContents(index, ItemStack.EMPTY);
        }

        this.onCraftMatrixChanged();
        return itemstack;
      }
    }
    else {
      return ItemStack.EMPTY;
    }
  }

  @Override
  public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
    this.parent.setInventorySlotContents(index, stack);
    this.onCraftMatrixChanged();
  }

  @Override
  public void markDirty() {
    this.parent.markDirty();
  }

  @Override
  public void clear() {
    // inventory can't clear the tile container
  }

  /**
   * If set to true no eventhandler.onCraftMatrixChanged calls will be made.
   * This is used to prevent recipe check when changing the item slots when something is crafted
   * (since each slot with an item is reduced by 1, it changes -> callback)
   */
  public void setDoNotCallUpdates(boolean doNotCallUpdates) {
    this.doNotCallUpdates = doNotCallUpdates;
  }

  public void onCraftMatrixChanged() {
    if (!this.doNotCallUpdates) {
      this.eventHandler.onCraftMatrixChanged(this);
    }
  }
}
