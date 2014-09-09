package tconstruct.armor.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.*;

/**
 * 
 * @author fuj1n
 * 
 */
public class SlotBlocksOnly extends Slot
{

    public SlotBlocksOnly(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid (ItemStack par1ItemStack)
    {
        return par1ItemStack.getItem() instanceof ItemBlock;
    }

    @Override
    public int getSlotStackLimit ()
    {
        return 1;
    }

}
