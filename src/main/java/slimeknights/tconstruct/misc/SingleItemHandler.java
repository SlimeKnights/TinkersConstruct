package slimeknights.tconstruct.misc;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import slimeknights.mantle.tileentity.MantleTileEntity;

public abstract class SingleItemHandler<T extends MantleTileEntity> implements IItemHandlerModifiable {
  protected final T parent;
  private final int maxStackSize;

  public SingleItemHandler(T parent, int maxStackSize) {
    this.parent = parent;
    this.maxStackSize = maxStackSize;
  }

  /** Current item in this slot */
  @Getter
  private ItemStack stack = ItemStack.EMPTY;

  /**
   * Sets the stack in this duct
   * @param newStack  New stack
   */
  public void setStack(ItemStack newStack) {
    this.stack = newStack;
    parent.markDirtyFast();
  }

  /**
   * Checks if the given stack is valid for this slot
   * @param stack  Stack
   * @return  True if valid
   */
  protected abstract boolean isItemValid(ItemStack stack);


  /* Properties */

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return slot == 0 && isItemValid(stack);
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public int getSlotLimit(int slot) {
    return maxStackSize;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if (slot == 0) {
      return stack;
    }
    return ItemStack.EMPTY;
  }


  /* Interaction */

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    if (slot == 0) {
      setStack(stack);
    }
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (slot != 0 || !this.stack.isEmpty()) {
      return stack;
    }
    if (!isItemValid(slot, stack)) {
      return stack;
    }
    if (!simulate) {
      ItemStack copy = stack.copy();
      copy.setCount(1);
      setStack(copy);
    }
    ItemStack copy = stack.copy();
    copy.setCount(stack.getCount() - 1);
    return copy;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0 || slot != 0) {
      return ItemStack.EMPTY;
    }
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }

    // if amount is less than our size, need to do some shrinking
    if (amount < stack.getCount()) {
      ItemStack result = stack.copy();
      result.setCount(amount);
      if (!simulate) {
        ItemStack copy = stack.copy();
        copy.setCount(stack.getCount() - amount);
        setStack(copy);
      }
      return result;
    }
    // equal to or bigger means we give them our stack directly
    if (simulate) {
      return stack.copy();
    } else {
      ItemStack ret = stack;
      setStack(ItemStack.EMPTY);
      return ret;
    }
  }

  /**
   * Writes this module to NBT
   * @return  Module in NBT
   */
  public CompoundTag writeToNBT() {
    CompoundTag nbt = new CompoundTag();
    if (!stack.isEmpty()) {
      stack.toTag(nbt);
    }
    return nbt;
  }

  /**
   * Reads this module from NBT
   * @param nbt  NBT
   */
  public void readFromNBT(CompoundTag nbt) {
    stack = ItemStack.fromTag(nbt);
  }
}
