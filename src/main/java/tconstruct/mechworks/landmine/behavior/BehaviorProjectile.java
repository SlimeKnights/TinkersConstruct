package tconstruct.mechworks.landmine.behavior;

import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * 
 * @author fuj1n
 * 
 */
public class BehaviorProjectile extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        IProjectile entity = null;

        EnumFacing enumfacing = getFacing(par1World, par2, par3, par4);

        if (par5ItemStack.getItem() == Items.arrow)
        {
            entity = new EntityArrow(par1World, par2, par3, par4);
            ((EntityArrow) entity).canBePickedUp = 1;
        }
        else if (par5ItemStack.getItem() == Items.snowball)
        {
            entity = new EntitySnowball(par1World, par2, par3, par4);
        }
        else if (par5ItemStack.getItem() == Items.ender_pearl)
        {
            if (triggerer instanceof EntityLivingBase)
            {
                entity = new EntityEnderPearl(par1World, (EntityLivingBase) triggerer);
                ((EntityEnderPearl) entity).setPosition(par2, par3, par4);
            }
            else
            {
                entity = new EntityEnderPearl(par1World, par2, par3, par4);
            }
        }

        if (entity == null)
        {
            return;
        }

        entity.setThrowableHeading((double) enumfacing.getFrontOffsetX(), (double) ((float) enumfacing.getFrontOffsetY() + 0.1F), (double) enumfacing.getFrontOffsetZ(), 1.1F, 6.0F);
        par1World.spawnEntityInWorld((Entity) entity);
        par1World.playAuxSFX(1002, par2, par3, par4, 0);
    }

    // Projectiles: snowballs, arrows

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return true;
    }

}
