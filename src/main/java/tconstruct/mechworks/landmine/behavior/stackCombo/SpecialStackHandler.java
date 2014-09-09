package tconstruct.mechworks.landmine.behavior.stackCombo;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.mechworks.landmine.Helper;
import tconstruct.mechworks.landmine.behavior.Behavior;
import tconstruct.mechworks.logic.TileEntityLandmine;

/**
 * 
 * @author fuj1n
 * 
 */
public abstract class SpecialStackHandler
{

    public static ArrayList<SpecialStackHandler> handlers = new ArrayList<SpecialStackHandler>();

    public static void registerBuiltInStackHandlers ()
    {
        addSpecialBehavior(new SpecialStackHandlerRocketFireball());
    }

    public static void addSpecialBehavior (SpecialStackHandler handler)
    {
        if (!handlers.contains(handler))
        {
            handlers.add(handler);
        }
    }

    public abstract void checkStack (World par1World, int par2, int par3, int par4, Entity triggerer, ArrayList<ItemStack> stacks);

    public EnumFacing getFacing (World par1World, int par2, int par3, int par4)
    {
        ForgeDirection dir = Helper.convertMetaToForgeOrientation(par1World.getBlockMetadata(par2, par3, par4));

        switch (dir)
        {
        case DOWN:
            return EnumFacing.UP;
        case UP:
            return EnumFacing.DOWN;
        case WEST:
            return EnumFacing.WEST;
        case EAST:
            return EnumFacing.EAST;
        case SOUTH:
            return EnumFacing.NORTH;
        case NORTH:
            return EnumFacing.SOUTH;
        default:
            return EnumFacing.UP;
        }
    }

    public final void removeItemFromInventory (World par1World, int x, int y, int z, ItemStack item)
    {
        if (item == null || item.stackSize <= 0)
        {
            return;
        }

        TileEntityLandmine tileEntity = (TileEntityLandmine) par1World.getTileEntity(x, y, z);

        while (item.stackSize > 0)
        {
            boolean hasRemovedThisIteration = false;
            for (int i = 0; i < tileEntity.getSizeTriggerInventory(); i++)
            {
                if (!hasRemovedThisIteration && tileEntity.getStackInSlot(i) != null)
                {
                    if (tileEntity.getStackInSlot(i).isItemEqual(item))
                    {
                        tileEntity.decrStackSize(i, 1);
                        item.stackSize--;
                    }
                }

                if (!hasRemovedThisIteration && i == tileEntity.getSizeTriggerInventory() - 1)
                {
                    item.stackSize--;
                }
            }
        }
    }

    public static final int arrayIndexOfStack (ArrayList<ItemStack> stacks, ItemStack item)
    {
        return Behavior.arrayIndexOfStack(stacks, item);
    }

    public static final boolean arrayContainsEqualStack (ArrayList<ItemStack> stacks, ItemStack item)
    {
        return Behavior.arrayContainsEqualStack(stacks, item);
    }

    public boolean isOffensive (ArrayList<ItemStack> par1ArrayList)
    {
        return true;
    }

}
