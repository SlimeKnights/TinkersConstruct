package tconstruct.mechworks.landmine.behavior;

import java.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import tconstruct.mechworks.itemblocks.ItemBlockLandmine;

//TODO Add a block that is similar to landmine, but only triggers by redstone, does not hurt the player, and does not disappear when used(also does not do anything that can deal damage)

/**
 * 
 * @author fuj1n
 * 
 */
public class BehaviorShears extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        Random rand = ItemBlockLandmine.getRandom();

        List<IShearable> sheeps = par1World.getEntitiesWithinAABB(IShearable.class, AxisAlignedBB.getBoundingBox(par2 - 2, par3 - 2, par4 - 2, par2 + 2, par3 + 2, par4 + 2));

        if (sheeps != null && !sheeps.isEmpty())
        {
            Iterator<IShearable> i1 = sheeps.iterator();
            IShearable sheep;
            while (i1.hasNext())
            {
                sheep = i1.next();
                if (sheep.isShearable(par5ItemStack, par1World, par2, par3, par4))
                {
                    if (rand.nextInt(2) == 0)
                    {
                        ArrayList<ItemStack> drops = sheep.onSheared(par5ItemStack, par1World, par2, par3, par4, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, par5ItemStack));
                        if (sheep instanceof EntityLivingBase)
                        {
                            par5ItemStack.damageItem(1, (EntityLivingBase) sheep);
                        }
                        Iterator<ItemStack> i2 = drops.iterator();
                        while (i2.hasNext())
                        {
                            if (sheep instanceof Entity)
                            {
                                Entity ent = (Entity) sheep;
                                dropItem(par1World, (int) ent.posX, (int) ent.posY, (int) ent.posZ, i2.next());
                            }
                            else
                            {
                                dropItem(par1World, par2, par3, par4, i2.next());
                            }
                        }
                    }
                }
            }
        }

        if (willBlockBeRemoved)
        {
            dropItem(par1World, par2, par3, par4, par5ItemStack);
        }
    }

    protected void dropItem (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack)
    {
        if (!par1World.isRemote && par1World.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double) (par1World.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (par1World.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (par1World.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(par1World, (double) par2 + d0, (double) par3 + d1, (double) par4 + d2, par5ItemStack);
            entityitem.delayBeforeCanPickup = 10;
            par1World.spawnEntityInWorld(entityitem);
        }
    }

    // Shear some sheep around a small area

    @Override
    public boolean shouldItemBeRemoved (ItemStack par1ItemStack, boolean willBlockBeRemoved)
    {
        return willBlockBeRemoved;
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    public boolean effectStacks ()
    {
        return false;
    }

}
