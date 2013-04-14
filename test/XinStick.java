package test;

import mods.tinker.tconstruct.entity.BlueSlime;
import mods.tinker.tconstruct.entity.NitroCreeper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		//spawnItem(player.posX, player.posY, player.posZ, tool, world);
		//CartEntity cart = new CartEntity(world, 1);
		//cart.cartType = 1;
		spawnEntity(player.posX, player.posY+1, player.posZ, new NitroCreeper(world), world, player);
		return stack;
	}

	/*public boolean onItemUse (ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		if (!par3World.isRemote)
		{
			par3World.spawnEntityInWorld(new CartEntity(par3World, (double) ((float) par4 + 0.5F), (double) ((float) par5 + 1.5F), (double) ((float) par6 + 0.5F), 0));
		}

		//--par1ItemStack.stackSize;
		return true;
	}*/

	public static void spawnItem (double x, double y, double z, ItemStack stack, World world)
	{
		if (!world.isRemote)
		{
			EntityItem entityitem = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
			//entityitem.delayBeforeCanPickup = 10;
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
