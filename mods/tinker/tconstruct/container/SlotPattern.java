package mods.tinker.tconstruct.container;

import mods.tinker.common.IPattern;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPattern extends Slot
{
    public SlotPattern(IInventory builder, int par3, int par4, int par5)
    {
        super(builder, par3, par4, par5);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack stack)
    {
        return stack.getItem() instanceof IPattern;
    }
}
