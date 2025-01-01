package slimeknights.tconstruct.tables.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.mantle.inventory.CustomResultSlot;
import slimeknights.mantle.inventory.IContainerCraftingCustom;

import javax.annotation.Nonnull;

/**
 * Same as {@link CustomResultSlot}, but does not require an crafting inventory
 */
public class CraftingResultSlot extends ResultSlot {
  private final IContainerCraftingCustom callback;
  @SuppressWarnings("ConstantConditions")
  public CraftingResultSlot(IContainerCraftingCustom callback, Player player, Container inv, int index, int x, int y) {
    // pass in null for CraftingInventory
    super(player, null, inv, index, x, y);
    this.callback = callback;
  }

  /* Methods that reference CraftingInventory */

  @Override
  protected void checkTakeAchievements(ItemStack stack) {
    if (this.removeCount > 0) {
      stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
      ForgeEventFactory.firePlayerCraftingEvent(this.player, stack, this.container);
    }
    this.removeCount = 0;
  }

  @Override
  public void onTake(Player playerIn, @Nonnull ItemStack stack) {
    this.checkTakeAchievements(stack);
    this.callback.onCrafting(playerIn, stack, this.container);
  }
}
