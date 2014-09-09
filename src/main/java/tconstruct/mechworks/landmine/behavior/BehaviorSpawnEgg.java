package tconstruct.mechworks.landmine.behavior;

import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * 
 * @author fuj1n
 * 
 */
public class BehaviorSpawnEgg extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        EnumFacing enumfacing = getFacing(par1World, par2, par3, par4);
        double d0 = par2 + Math.random() + (double) enumfacing.getFrontOffsetX();
        double d1 = (double) ((float) par3 + 0.2F);
        double d2 = par4 + Math.random() + (double) enumfacing.getFrontOffsetZ();
        Entity entity = ItemMonsterPlacer.spawnCreature(par1World, par5ItemStack.getItemDamage(), d0, d1, d2);

        if (entity instanceof EntityLivingBase && par5ItemStack.hasDisplayName())
        {
            ((EntityLiving) entity).setCustomNameTag(par5ItemStack.getDisplayName());
        }
    }

    @Override
    public boolean effectStacks ()
    {
        return false;
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return true;
    }

}
