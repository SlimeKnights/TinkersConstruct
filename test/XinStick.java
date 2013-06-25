package test;

import mods.tinker.tconstruct.entity.Automaton;
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
 * mDiyo's development testing item
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
		//if (!world.isRemote)
			//MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) player, -7);
		//player.travelToDimension(-7);
	    Automaton entity = new Automaton(world);
	    entity.setOwner(player);
	    //entity.username = "NekoGloop";
		spawnEntity(player.posX, player.posY+1, player.posZ, entity, world, player);
	    //healPlayer(player);
		//removeChunk(world, player.posX, player.posZ);
		return stack;
	}
	
	/*public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
	    EntityLightningBolt entity = new EntityLightningBolt(world, x, y, z);
        world.spawnEntityInWorld(entity);
        entity = new EntityLightningBolt(world, x-3, y+4, z-3);
        world.spawnEntityInWorld(entity);
        entity = new EntityLightningBolt(world, x-3, y+4, z+3);
        world.spawnEntityInWorld(entity);
        entity = new EntityLightningBolt(world, x+3, y+4, z-3);
        world.spawnEntityInWorld(entity);
        entity = new EntityLightningBolt(world, x+3, y+4, z+3);
        world.spawnEntityInWorld(entity);
        return false;
    }*/
	
	public static void healPlayer(EntityPlayer player)
	{
	    player.setEntityHealth(50);
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
			//((BlueSlime) entity).setSlimeSize(8);
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
