package tinker.armory.content;

import tinker.armory.Armory;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArmorStandItem extends Item
{
	public ArmorStandItem(int id) 
	{ 
		super(id);
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        int var11 = world.getBlockId(x, y, z);

        if (var11 == Block.snow.blockID)
        {
            side = 1;
        }
        else if (var11 != Block.vine.blockID && var11 != Block.tallGrass.blockID && var11 != Block.deadBush.blockID
                && (Block.blocksList[var11] == null || !Block.blocksList[var11].isBlockReplaceable(world, x, y, z)))
        {
            if (side == 0)
                --y;
            
            if (side == 1)
                ++y;

            if (side == 2)
                --z;

            if (side == 3)
                ++z;

            if (side == 4)
                --x;

            if (side == 5)
                ++x;
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(x, y, z, side, stack))
        {
            return false;
        }
        if (spawnEntity(x, y, z, new ArmorStandEntity(world), world, player) )
        {
        	stack.stackSize--;
        	return true;
        }
        player.swingItem();
        return false;
    }
	
	public static boolean spawnEntity(double x, double y, double z, Entity entity, World world, EntityPlayer player)
    {
    	if (!world.isRemote)
    	{
    		entity.setPosition(x+0.5, y, z+0.5);
	        world.spawnEntityInWorld(entity);
	        world.playAuxSFX(2001, (int)x, (int)y, (int)z, Block.stone.blockID);
	        return true;
    	}
    	return false;
    }
	
	public String getTextureFile()
    {
        return Armory.texture;
    }
}
