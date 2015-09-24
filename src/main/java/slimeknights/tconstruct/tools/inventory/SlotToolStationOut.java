package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotToolStationOut extends Slot {

  public ContainerToolStation parent;

  public SlotToolStationOut(int index, int xPosition, int yPosition, ContainerToolStation container) {
    super(new InventoryCraftResult(), index, xPosition, yPosition);

    this.parent = container;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return false;
  }

  @Override
  public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
    parent.onResultTaken(playerIn, stack);
    stack.onCrafting(playerIn.getEntityWorld(), playerIn, 1);

    super.onPickupFromSlot(playerIn, stack);
  }
}
