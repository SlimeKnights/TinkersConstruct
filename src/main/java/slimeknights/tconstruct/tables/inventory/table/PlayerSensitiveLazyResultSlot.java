package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.tables.tileentity.table.crafting.LazyResultInventory;

/**
 * Extension of lazy result slot that adds player access when possible
 */
public class PlayerSensitiveLazyResultSlot extends LazyResultSlot {
  private final PlayerEntity player;
  public PlayerSensitiveLazyResultSlot(PlayerEntity player, LazyResultInventory inventory, int xPosition, int yPosition) {
    super(inventory, xPosition, yPosition);
    this.player = player;
  }

  @Override
  public ItemStack getStack() {
    return this.inventory.getResult(player);
  }

  @Override
  public ItemStack decrStackSize(int amount) {
    return this.inventory.getResult(player).copy();
  }
}
