package slimeknights.tconstruct.library;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.misc.IItemHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyItemHandler implements IItemHandler {
  public static final EmptyItemHandler INSTANCE = new EmptyItemHandler();

  @Override
  public int getSlots() {
    return 0;
  }

  @Override
  public int getSlotLimit(int slot) {
    return 0;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return false;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return stack;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    return ItemStack.EMPTY;
  }
}
