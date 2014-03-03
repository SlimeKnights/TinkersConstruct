package tconstruct.util.landmine.behavior;

import java.util.Iterator;
import java.util.List;

import tconstruct.items.blocks.ItemBlockLandmine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * 
 * @author fuj1n
 *
 */
public class BehaviorPotion extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        if (ItemPotion.isSplash(par5ItemStack.getItemDamage()))
        {
            par1World.playSoundAtEntity(triggerer, "random.bow", 0.5F, 0.4F / (ItemBlockLandmine.getRandom().nextFloat() * 0.4F + 0.8F));

            if (!par1World.isRemote)
            {
                if (triggerer instanceof EntityLivingBase)
                {
                    par1World.spawnEntityInWorld(new EntityPotion(par1World, (EntityLivingBase) triggerer, par5ItemStack));
                }
                else
                {
                    par1World.spawnEntityInWorld(new EntityPotion(par1World, par2, par3, par4, par5ItemStack));
                }
            }
        }
        else
        {
            if (triggerer instanceof EntityPlayer)
            {
                Item.potion.onEaten(par5ItemStack, par1World, (EntityPlayer) triggerer);
            }
            else if (triggerer instanceof EntityLivingBase)
            {
                if (!par1World.isRemote)
                {
                    List list = Item.potion.getEffects(par5ItemStack);

                    if (list != null)
                    {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext())
                        {
                            PotionEffect potioneffect = (PotionEffect) iterator.next();
                            ((EntityLivingBase) triggerer).addPotionEffect(new PotionEffect(potioneffect));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return false;
    }
}
