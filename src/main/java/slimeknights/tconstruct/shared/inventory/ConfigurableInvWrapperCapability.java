package slimeknights.tconstruct.shared.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class ConfigurableInvWrapperCapability extends InvWrapper {

  private final boolean canInsert;
  private final boolean canExtract;

  public ConfigurableInvWrapperCapability(IInventory inv, boolean canInsert, boolean canExtract) {
    super(inv);
    this.canInsert = canInsert;
    this.canExtract = canExtract;
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if(!canInsert) {
      return stack;
    }
    return super.insertItem(slot, stack, simulate);
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if(!canExtract) {
      return ItemStack.EMPTY;
    }
    return super.extractItem(slot, amount, simulate);
  }
}
