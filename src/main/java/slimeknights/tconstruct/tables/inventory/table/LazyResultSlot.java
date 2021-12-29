package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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
  public boolean mayPlace(ItemStack stack) {
    return false;
  }

  @Override
  public ItemStack remove(int amount) {
    if (this.hasItem()) {
      this.amountCrafted += Math.min(amount, this.getItem().getCount());
    }

    return super.remove(amount);
  }

  @Override
  public void onTake(Player player, ItemStack stack) {
    //inventory.craftResult(player, amountCrafted); TODO: needed?
    inventory.clearContent();
    amountCrafted = 0;
  }

  @Override
  protected void onQuickCraft(ItemStack stack, int amount) {
    this.amountCrafted += amount;
    this.checkTakeAchievements(stack);
  }

  @Override
  protected void onSwapCraft(int numItemsCrafted) {
    this.amountCrafted += numItemsCrafted;
  }
}
