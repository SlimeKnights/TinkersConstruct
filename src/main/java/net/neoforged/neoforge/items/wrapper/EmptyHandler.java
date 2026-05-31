package slimeknights.tconstruct.compat.neoforged.neoforge.items.wrapper;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/** Empty item handler singleton removed from NeoForge. */
public enum EmptyHandler implements IItemHandler {
  INSTANCE;

  @Override
  public int getSlots() {
    return 0;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return stack;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    return ItemStack.EMPTY;
  }

  @Override
  public int getSlotLimit(int slot) {
    return 0;
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return false;
  }
}
