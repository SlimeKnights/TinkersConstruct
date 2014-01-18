package tconstruct.inventory;

import mantle.blocks.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author fuj1n
 * 
 */
public class SlotOpaqueBlocksOnly extends SlotBlocksOnly
{

    public SlotOpaqueBlocksOnly(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
    }

    public boolean isItemValid (ItemStack par1ItemStack)
    {
        if (super.isItemValid(par1ItemStack))
        {
            Block b = BlockUtils.getBlockFromItemStack(par1ItemStack);
            return b.isOpaqueCube() && b.func_149686_d();
        }

        return false;
    }

    @Override
    public int getSlotStackLimit ()
    {
        return 1;
    }

}
