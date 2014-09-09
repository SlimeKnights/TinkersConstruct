package tconstruct.mechworks.landmine.behavior;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.mechworks.itemblocks.ItemBlockLandmine;

/**
 * 
 * @author fuj1n
 * 
 */
public class BehaviorFirecharge extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        EnumFacing face = getFacing(par1World, par2, par3, par4);

        for (int n = 0; n < par5ItemStack.stackSize; n++)
        {
            triggerer.attackEntityFrom(DamageSource.generic, 9F);
            triggerer.setFire(4);
            double d0 = par2 + (double) ((float) face.getFrontOffsetX() * 0.3F);
            double d1 = par3 + (double) ((float) face.getFrontOffsetX() * 0.3F);
            double d2 = par4 + (double) ((float) face.getFrontOffsetZ() * 0.3F);
            Random random = par1World.rand;
            double d3 = random.nextGaussian() * 0.05D + (double) face.getFrontOffsetX();
            double d4 = random.nextGaussian() * 0.05D + (double) face.getFrontOffsetY();
            double d5 = random.nextGaussian() * 0.05D + (double) face.getFrontOffsetZ();
            par1World.spawnEntityInWorld(new EntitySmallFireball(par1World, d0, d1, d2, d3, d4, d5));

            for (int i = par2 - 2; i <= (par2 + 2); i++)
            {
                for (int j = par4 - 2; j <= (par4 + 2); j++)
                {
                    if ((par1World.getBlock(i, par3, j) == null || (par1World.getBlock(i, par3, j) != null && par1World.getBlock(i, par3, j).isReplaceable(par1World, i, par3, j))) && ItemBlockLandmine.getRandom().nextInt(5) == 0 && Blocks.fire.canPlaceBlockAt(par1World, i, par3, j))
                    {
                        par1World.setBlock(i, par3, j, Blocks.fire);
                    }
                }
            }
        }
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return true;
    }
}
