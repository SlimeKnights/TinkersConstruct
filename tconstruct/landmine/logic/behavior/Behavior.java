package tconstruct.landmine.logic.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import tconstruct.landmine.Helper;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

/**
 * 
 * @author fuj1n
 *
 */
public abstract class Behavior
{

    public static HashMap<Integer, Behavior> behaviorsListItems = new HashMap<Integer, Behavior>();
    public static HashMap<Integer, Behavior> behaviorsListBlocks = new HashMap<Integer, Behavior>();
    protected static Behavior defaultBehavior;

    public static Behavior dummy = new BehaviorDummy();
    public static Behavior utilityMode = new BehaviorPreventExplode();
    public static Behavior explosive = new BehaviorExplosive();
    public static Behavior firework = new BehaviorFirework();
    public static Behavior blockThrow = new BehaviorBlockThrow();
    public static Behavior potion = new BehaviorPotion();
    public static Behavior fireball = new BehaviorFirecharge();
    public static Behavior spawn = new BehaviorSpawnEgg();
    public static Behavior shoot = new BehaviorProjectile();
    public static Behavior shear = new BehaviorShears();

    public static void registerBuiltInBehaviors ()
    {
        defaultBehavior = new BehaviorDefault();

        addBehavior(new ItemStack(Item.stick), dummy);
        addBehavior(new ItemStack(Item.redstone), utilityMode);
        addBehavior(new ItemStack(Block.torchWood), blockThrow);
        addBehavior(new ItemStack(Item.gunpowder), explosive);
        addBehavior(new ItemStack(Block.tnt), explosive);
        addBehavior(new ItemStack(Item.firework), firework);
        addBehavior(new ItemStack(Item.potion), potion);
        addBehavior(new ItemStack(Item.fireballCharge), fireball);
        addBehavior(new ItemStack(Item.monsterPlacer), spawn);
        addBehavior(new ItemStack(Item.arrow), shoot);
        addBehavior(new ItemStack(Item.snowball), shoot);
        addBehavior(new ItemStack(Item.enderPearl), shoot);
        addBehavior(new ItemStack(Item.shears), shear);

        //Make sure the part below this comment is executed last(to avoid conflicts)
        for (int i = 1; i < Block.blocksList.length; i++)
        {
            if (Block.blocksList[i] != null && Block.blocksList[i].isOpaqueCube() && Block.blocksList[i].renderAsNormalBlock() && !(Block.blocksList[i] instanceof ITileEntityProvider)
                    && !behaviorsListBlocks.containsKey(new ItemStack(Block.blocksList[i])))
            {
                addBehavior(new ItemStack(Block.blocksList[i]), blockThrow);
            }
        }
    }

    public static Behavior getBehaviorFromStack (ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return null;
        }

        if (par1ItemStack.getItem() instanceof ItemBlock)
        {
            if (!behaviorsListBlocks.isEmpty())
            {
                return behaviorsListBlocks.get(par1ItemStack.itemID);
            }
            else
            {
                return null;
            }
        }
        else
        {
            if (!behaviorsListItems.isEmpty())
            {
                return behaviorsListItems.get(par1ItemStack.itemID);
            }
            else
            {
                return null;
            }
        }
    }

    public static Behavior getDefaulBehavior ()
    {
        return defaultBehavior;
    }

    public static void addBehavior (ItemStack par1ItemStack, Behavior par2Behavior)
    {
        if (par1ItemStack.getItem() instanceof ItemBlock)
        {
            if (!behaviorsListBlocks.containsKey(par1ItemStack.itemID))
            {
                behaviorsListBlocks.put(par1ItemStack.itemID, par2Behavior);
            }
        }
        else
        {
            if (!behaviorsListItems.containsKey(par1ItemStack.itemID))
            {
                behaviorsListItems.put(par1ItemStack.itemID, par2Behavior);
            }
        }
    }

    public abstract void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved);

    public int getStackLimit (ItemStack par1ItemStack)
    {
        return 1;
    }

    public void getInformation (ItemStack par1ItemStack, List par2List)
    {
    }

    //Will return false if the effect does not stack among the slots
    public boolean effectStacks ()
    {
        return true;
    }

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

    public boolean doesBehaviorPreventRemovalOfBlock (ItemStack par1ItemStack)
    {
        return false;
    }

    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return true;
    }

    public boolean isBehaviorExchangableWithOffensive (ItemStack par1ItemStack)
    {
        return true;
    }

    public boolean shouldItemBeRemoved (ItemStack par1ItemStack, boolean willBlockGetRemoved)
    {
        return true;
    }

    public static final int arrayIndexOfStack (ArrayList<ItemStack> stacks, ItemStack item)
    {
        Iterator<ItemStack> i1 = stacks.iterator();

        int index = 0;

        while (i1.hasNext())
        {
            ItemStack stack = i1.next();
            if (stack.isItemEqual(item))
            {
                return index;
            }
            index++;
        }

        return -1;
    }

    public static final boolean arrayContainsEqualStack (ArrayList<ItemStack> stacks, ItemStack item)
    {
        Iterator<ItemStack> i1 = stacks.iterator();

        while (i1.hasNext())
        {
            ItemStack stack = i1.next();
            if (stack.isItemEqual(item))
            {
                return true;
            }
        }

        return false;
    }

    public boolean overridesDefault ()
    {
        return false;
    }

}
