package tconstruct.armor.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class SlotUnused extends Slot
{
    public SlotUnused(IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    @Override
    public int getSlotStackLimit ()
    {
        return 0;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for
     * the armor slots.
     */
    @Override
    public boolean isItemValid (ItemStack par1ItemStack)
    {
        return false;
    }
}