package tconstruct.landmine.logic.behavior;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * @author fuj1n
 *
 */
public class BehaviorFirework extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        for (int i = 0; i < par5ItemStack.stackSize; i++)
        {
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(par1World, (double) par2, (double) par3, (double) par4, par5ItemStack);
            par1World.spawnEntityInWorld(entityfireworkrocket);

            boolean shouldRun = true;
            if (triggerer instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) triggerer;
                if (player.capabilities.isCreativeMode)
                {
                    // Remove the bellow comment to make the player in creative
                    // mode not get launched
                    // shouldRun = false;
                }
            }

            if (shouldRun)
            {
                triggerer.mountEntity(entityfireworkrocket);
            }
        }
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    public boolean overridesDefault ()
    {
        return true;
    }

}
