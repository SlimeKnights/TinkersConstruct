package tconstruct.landmine.logic.behavior;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * @author fuj1n
 *
 */
public class BehaviorExplosive extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        float explodeStrenght = 0;
        if (par5ItemStack.itemID == Block.tnt.blockID)
        {
            explodeStrenght = 4.0F * par5ItemStack.stackSize;
        }
        else if (par5ItemStack.itemID == Item.gunpowder.itemID)
        {
            explodeStrenght = 0.8F * par5ItemStack.stackSize;
        }

        par1World.createExplosion(new EntityTNTPrimed(par1World, par2, par3, par4, (EntityLivingBase) null), par2, par3, par4, explodeStrenght, true);
    }

    @Override
    public void getInformation (ItemStack par1ItemStack, List par2List)
    {
        String str = "UNDEFINED";
        if (par1ItemStack.itemID == Block.tnt.blockID)
        {
            str = "medium";
        }
        else if (par1ItemStack.itemID == Item.gunpowder.itemID)
        {
            str = "small";
        }

        //par2List.add("This item explodes with " + str + " power when the landmine is triggered.");
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return true;
    }

    @Override
    //Change this to false to greatly decrease the maximum potential of explosions but at the same time, make them launch the player much higher(I mean the explosions with 2 or more TNT
    public boolean effectStacks ()
    {
        return true;
    }

}
