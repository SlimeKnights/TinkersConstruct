package slimeknights.tconstruct.misc;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class InventoryItemHandler extends SimpleInventory implements IItemHandler{

  @Override
  public int getSlots() {
    return size();
  }

  @Override
  public @NotNull ItemStack getStackInSlot(int slot) {
    return getStack(slot);
  }

  @Override
  public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
    throw new RuntimeException("CRAB!"); // FIXME
  }

  @Override
  public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
    throw new RuntimeException("CRAB!"); // FIXME
  }

  @Override
  public int getSlotLimit(int slot) {
    return getMaxCountPerStack();
  }

  @Override
  public boolean isItemValid(int slot, @NotNull ItemStack stack) {
    return true;
  }
}
