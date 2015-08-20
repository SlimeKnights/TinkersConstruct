package slimeknights.tconstruct.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Used to wrap the slots inside Modules/Subcontainers
 */
public class SlotWrapper extends Slot {
  public final Slot parent;

  public SlotWrapper(Slot slot) {
    super(slot.inventory, slot.getSlotIndex(), slot.xDisplayPosition, slot.yDisplayPosition);
    this.parent = slot;
  }

  @Override
  public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
    parent.onSlotChange(p_75220_1_, p_75220_2_);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return parent.isItemValid(stack);
  }

  @Override
  public boolean canTakeStack(EntityPlayer playerIn) {
    return parent.canTakeStack(playerIn);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean canBeHovered() {
    return parent.canBeHovered();
  }
}
