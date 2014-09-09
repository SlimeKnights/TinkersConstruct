package tconstruct.tools.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import tconstruct.library.util.IPattern;

public class SlotPattern extends Slot
{
    public SlotPattern(IInventory builder, int par3, int par4, int par5)
    {
        super(builder, par3, par4, par5);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid (ItemStack stack)
    {
        return stack.getItem() instanceof IPattern;
    }
}