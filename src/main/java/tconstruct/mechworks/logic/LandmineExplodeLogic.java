package tconstruct.mechworks.logic;

import java.util.*;
import mantle.world.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.mechworks.landmine.behavior.Behavior;

/**
 * This logic is designed only for blocks having TileEntityLandmine as their
 * tileEntity(otherwise ClassCastException will be awarded)
 * 
 * @author fuj1n
 * 
 */
public class LandmineExplodeLogic
{

    private final World worldObj;
    private final Entity triggerer;
    private final TileEntityLandmine tileEntity;
    private final int x, y, z;

    public LandmineExplodeLogic(World par1World, int par2, int par3, int par4, Entity entity)
    {
        worldObj = par1World;
        this.tileEntity = (TileEntityLandmine) par1World.getTileEntity(par2, par3, par4);
        this.x = par2;
        this.y = par3;
        this.z = par4;
        this.triggerer = entity;
    }

    public void explode ()
    {
        if (triggerer == null)
        {
            return;
        }

        boolean preventExplode = false;
        boolean isOffensive = true;
        boolean cancelDefault = false;

        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        boolean hasExploded = false;

        if (tileEntity == null)
        {
            return;
        }

        tileEntity.isExploding = true;

        for (int i = 0; i < tileEntity.getSizeTriggerInventory(); i++)
        {
            ItemStack currentStack = tileEntity.getStackInSlot(i);
            Behavior b = Behavior.getBehaviorFromStack(currentStack);
            if (b != null)
            {

                if (b.doesBehaviorPreventRemovalOfBlock(currentStack))
                {
                    preventExplode = true;
                }

                if (!b.isBehaviorExchangableWithOffensive(currentStack))
                {
                    isOffensive = false;
                }

                if (b.overridesDefault())
                {
                    cancelDefault = true;
                }

                if (!stacks.isEmpty() && Behavior.arrayContainsEqualStack(stacks, currentStack) && b.effectStacks())
                {
                    stacks.get(Behavior.arrayIndexOfStack(stacks, currentStack)).stackSize += currentStack.stackSize;
                }
                else
                {
                    stacks.add(currentStack.copy());
                }
            }
        }

        LandmineSpecialStackLogic specialStacks = new LandmineSpecialStackLogic(worldObj, x, y, z, triggerer, isOffensive, stacks);
        specialStacks.handleSpecialStacks();

        Iterator<ItemStack> i1 = stacks.iterator();
        while (i1.hasNext())
        {
            ItemStack currentStack = i1.next();
            Behavior b = Behavior.getBehaviorFromStack(currentStack);
            if (b != null)
            {
                if (isOffensive || !b.isOffensive(currentStack))
                {
                    b.executeLogic(worldObj, x, y, z, currentStack, triggerer, !preventExplode);
                    if (b.shouldItemBeRemoved(currentStack, !preventExplode))
                    {
                        if (b.effectStacks())
                        {
                            for (int i = 0; i < tileEntity.getSizeTriggerInventory(); i++)
                            {
                                if (tileEntity.getStackInSlot(i) != null)
                                {
                                    if (tileEntity.getStackInSlot(i).isItemEqual(currentStack))
                                    {
                                        tileEntity.setInventorySlotContents(i, null);
                                    }
                                }
                            }
                        }
                        else
                        {
                            boolean hasRemoved = false;

                            for (int i = 0; i < tileEntity.getSizeTriggerInventory() && !hasRemoved; i++)
                            {
                                if (tileEntity.getStackInSlot(i) != null)
                                {
                                    if (tileEntity.getStackInSlot(i).isItemEqual(currentStack))
                                    {
                                        tileEntity.setInventorySlotContents(i, null);
                                        hasRemoved = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Behavior defBeh = Behavior.getDefaulBehavior();
        if (defBeh != null && isOffensive && !cancelDefault)
        {
            defBeh.executeLogic(worldObj, x, y, z, null, triggerer, !preventExplode);
        }

        if (hasExploded || defBeh != null && !preventExplode)
        {
            worldObj.removeTileEntity(x, y, z);
            WorldHelper.setBlockToAir(worldObj, x, y, z);
        }
        else
        {
            tileEntity.isExploding = false;
        }
    }
}
