package tconstruct.world;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import tconstruct.tools.items.TitleIcon;

public class TDispenserBehaviorSpawnEgg extends BehaviorDefaultDispenseItem
{
    /**
     * Dispense the specified stack, play the dispense sound and spawn
     * particles.
     */
    @Override
    public ItemStack dispenseStack (IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(par1IBlockSource.getBlockMetadata());
        double d0 = par1IBlockSource.getX() + (double) enumfacing.getFrontOffsetX();
        double d1 = (double) ((float) par1IBlockSource.getYInt() + 0.2F);
        double d2 = par1IBlockSource.getZ() + (double) enumfacing.getFrontOffsetZ();
        EntityLiving entity = TitleIcon.activateSpawnEgg(par2ItemStack, par1IBlockSource.getWorld(), d0, d1, d2, 0);

        if (par2ItemStack.hasDisplayName())
        {
            ((EntityLiving) entity).setCustomNameTag(par2ItemStack.getDisplayName());
        }

        par2ItemStack.splitStack(1);
        return par2ItemStack;
    }
}
