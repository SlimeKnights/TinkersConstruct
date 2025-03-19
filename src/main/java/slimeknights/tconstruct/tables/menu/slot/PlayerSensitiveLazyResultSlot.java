package slimeknights.tconstruct.tables.menu.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer;

/**
 * Extension of lazy result slot that adds player access when possible
 */
public class PlayerSensitiveLazyResultSlot extends LazyResultSlot {
  /** Player using the slot */
  private final Player player;
  /** Last non-player sensitive result */
  private ItemStack lastNormalResult;
  /** Last player sensitive result */
  private ItemStack lastPlayerResult;
  public PlayerSensitiveLazyResultSlot(Player player, LazyResultContainer inventory, int xPosition, int yPosition) {
    super(inventory, xPosition, yPosition);
    this.player = player;
  }

  @Override
  public ItemStack getItem() {
    // if we have not yet calculated the player specific result, or the inventory recalculated it, then recalculate
    ItemStack newResult = inventory.getResult();
    if (lastPlayerResult == null || lastNormalResult != newResult) {
      lastNormalResult = newResult;
      lastPlayerResult = inventory.calcResult(player);
    }
    return lastPlayerResult;
  }

  @Override
  public ItemStack remove(int amount) {
    ItemStack result = getItem().copy();
    if (!result.isEmpty()) {
      this.amountCrafted += Math.min(amount, result.getCount());
    }
    return result;
  }
}
