package slimeknights.tconstruct.tables.menu.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer;

/**
 * Extension of lazy result slot that adds player access when possible
 */
public class PlayerSensitiveLazyResultSlot extends LazyResultSlot {
  private final Player player;
  public PlayerSensitiveLazyResultSlot(Player player, LazyResultContainer inventory, int xPosition, int yPosition) {
    super(inventory, xPosition, yPosition);
    this.player = player;
  }

  @Override
  public ItemStack getItem() {
    return this.inventory.getResult(player);
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
