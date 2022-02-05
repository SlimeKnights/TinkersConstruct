package slimeknights.tconstruct.tools.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReadOnlySlot extends Slot {
  public ReadOnlySlot(Container inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return false;
  }

  @Override
  public void set(ItemStack stack) {}

  @Override
  public ItemStack remove(int amount) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean mayPickup(Player playerIn) {
    return false;
  }

  @Override
  public boolean isActive() {
    return true;
  }
}
