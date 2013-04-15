package test;

import mods.tinker.tconstruct.entity.BlueSlime;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

/*
 * mDiyo's development testing mod
 * Does everything on right-click!
 */

public class XinStick extends Item
{
	public XinStick(int id)
	{
		super(id);
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public Icon getIconFromDamage(int par1)
    {
	    return Item.arrow.getIconFromDamage(par1);
    }

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		spawnEntity(player.posX, player.posY+1, player.posZ, new BlueSlime(world), world, player);
	    System.out.println("Health! "+player.getHealth());
	    //healPlayer(player);
		return stack;
	}
	
	public static void healPlayer(EntityPlayer player)
	{
	    player.setEntityHealth(200);
	}

	public static void spawnItem (double x, double y, double z, ItemStack stack, World world)
	{
		if (!world.isRemote)
		{
			EntityItem entityitem = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
			world.spawnEntityInWorld(entityitem);
		}
	}

	public static void spawnEntity (double x, double y, double z, Entity entity, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			entity.setPosition(x, y, z);
			entity.setAngles(player.cameraYaw, player.cameraYaw);
			((EntityLiving) entity).initCreature();
			world.spawnEntityInWorld(entity);
		}
	}

	public void removeChunk (World world, double dx, double dz)
	{
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				for (int y = 0; y < 128; y++)
				{
					world.setBlock((int) (x + dx), y, (int) (z + dz), 0, 0, 3);
				}
			}
		}
	}
}
