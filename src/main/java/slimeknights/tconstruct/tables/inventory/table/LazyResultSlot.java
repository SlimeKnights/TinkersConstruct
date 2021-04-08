package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import slimeknights.tconstruct.tables.tileentity.crafting.LazyResultInventory;

/**
 * Slot for display of {@link LazyResultInventory}.
 */
@SuppressWarnings("WeakerAccess")
public class LazyResultSlot extends Slot {
  private final LazyResultInventory inventory;
  private int amountCrafted = 0;
  public LazyResultSlot(LazyResultInventory inventory, int xPosition, int yPosition) {
    super(inventory, 0, xPosition, yPosition);
    this.inventory = inventory;
  }

  @Override
  public boolean canInsert(ItemStack stack) {
    return false;
  }

  @Override
  public ItemStack takeStack(int amount) {
    if (this.hasStack()) {
      this.amountCrafted += Math.min(amount, this.getStack().getCount());
    }

    return super.takeStack(amount);
  }

  @Override
  public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
    ItemStack result = inventory.craftResult(player, amountCrafted);
    amountCrafted = 0;
    return result;
  }

  @Override
  protected void onCrafted(ItemStack stack, int amount) {
    this.amountCrafted += amount;
    this.onCrafted(stack);
  }

  @Override
  protected void onTake(int numItemsCrafted) {
    this.amountCrafted += numItemsCrafted;
  }
}
