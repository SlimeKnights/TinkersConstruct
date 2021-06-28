package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.tables.tileentity.table.crafting.LazyResultInventory;

/**
 * Slot for display of {@link LazyResultInventory}.
 */
@SuppressWarnings("WeakerAccess")
public class LazyResultSlot extends Slot {
  protected final LazyResultInventory inventory;
  protected int amountCrafted = 0;
  public LazyResultSlot(LazyResultInventory inventory, int xPosition, int yPosition) {
    super(inventory, 0, xPosition, yPosition);
    this.inventory = inventory;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return false;
  }

  @Override
  public ItemStack decrStackSize(int amount) {
    if (this.getHasStack()) {
      this.amountCrafted += Math.min(amount, this.getStack().getCount());
    }

    return super.decrStackSize(amount);
  }

  @Override
  public ItemStack onTake(PlayerEntity player, ItemStack stack) {
    ItemStack result = inventory.craftResult(player, amountCrafted);
    amountCrafted = 0;
    return result;
  }

  @Override
  protected void onCrafting(ItemStack stack, int amount) {
    this.amountCrafted += amount;
    this.onCrafting(stack);
  }

  @Override
  protected void onSwapCraft(int numItemsCrafted) {
    this.amountCrafted += numItemsCrafted;
  }
}
