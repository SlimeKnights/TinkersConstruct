package tconstruct.mechworks.landmine.behavior;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.mechworks.landmine.*;

/**
 * 
 * @author fuj1n
 * 
 */
public abstract class Behavior
{

    public static HashMap<LandmineStack, Behavior> behaviorsListItems = new HashMap<LandmineStack, Behavior>();
    public static HashMap<LandmineStack, Behavior> behaviorsListBlocks = new HashMap<LandmineStack, Behavior>();
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

        addBehavior(new LandmineStack(Items.stick), dummy);
        addBehavior(new LandmineStack(Items.redstone), utilityMode);
        addBehavior(new LandmineStack(Blocks.torch), blockThrow);
        addBehavior(new LandmineStack(Items.gunpowder), explosive);
        addBehavior(new LandmineStack(Blocks.tnt), explosive);
        addBehavior(new LandmineStack(Items.fireworks), firework);
        addBehavior(new LandmineStack(Items.potionitem), potion);
        addBehavior(new LandmineStack(Items.fire_charge), fireball);
        addBehavior(new LandmineStack(Items.spawn_egg), spawn);
        addBehavior(new LandmineStack(Items.arrow), shoot);
        addBehavior(new LandmineStack(Items.snowball), shoot);
        addBehavior(new LandmineStack(Items.ender_pearl), shoot);
        addBehavior(new LandmineStack(Items.shears), shear);

        // Make sure the part below this comment is executed last(to avoid
        // conflicts)
        Iterator i1 = Block.blockRegistry.iterator();
        while (i1.hasNext())
        {
            Object ob = i1.next();
            if (ob != null && ob instanceof Block)
            {
                Block b = (Block) ob;
                if (b.getMaterial().isOpaque() && b.renderAsNormalBlock() && !b.canProvidePower() && !(b instanceof ITileEntityProvider) && !behaviorsListBlocks.containsKey(new ItemStack(b)))
                {
                    addBehavior(new LandmineStack(b), blockThrow);
                }
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
                for (int i = 0; i < behaviorsListBlocks.size(); i++)
                {
                    LandmineStack st = (LandmineStack) behaviorsListBlocks.keySet().toArray()[i];
                    if (st.equals(par1ItemStack))
                    {
                        return (Behavior) behaviorsListBlocks.values().toArray()[i];
                    }
                }
                return null;
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
                for (int i = 0; i < behaviorsListItems.size(); i++)
                {
                    LandmineStack st = (LandmineStack) behaviorsListItems.keySet().toArray()[i];
                    if (st.equals(par1ItemStack))
                    {
                        return (Behavior) behaviorsListItems.values().toArray()[i];
                    }
                }
                return null;
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

    public static void addBehavior (LandmineStack par1LandmineStack, Behavior par2Behavior)
    {
        if (par1LandmineStack.isBlock)
        {
            if (!behaviorsListBlocks.containsKey(par1LandmineStack))
            {
                behaviorsListBlocks.put(par1LandmineStack, par2Behavior);
            }
        }
        else
        {
            if (!behaviorsListItems.containsKey(par1LandmineStack))
            {
                behaviorsListItems.put(par1LandmineStack, par2Behavior);
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

    // Will return false if the effect does not stack among the slots
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
