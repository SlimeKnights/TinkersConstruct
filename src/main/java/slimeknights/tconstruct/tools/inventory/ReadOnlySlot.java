package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReadOnlySlot extends Slot {
  public ReadOnlySlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return false;
  }

  @Override
  public void putStack(ItemStack stack) {}

  @Override
  public ItemStack decrStackSize(int amount) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canTakeStack(PlayerEntity playerIn) {
    return false;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean isEnabled() {
    return true;
  }
}
