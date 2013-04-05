package mods.tinker.tconstruct.tools;

import java.util.List;

import mods.tinker.tconstruct.entity.LaunchedPotion;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PotionLauncher extends Item
{
	@SideOnly(Side.CLIENT)
	private Icon[] icons;
	public static final String[] textureNames = new String[] { "potionlauncher" };

	public PotionLauncher(int par1)
	{
		super(par1);
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxDamage(3);
	}

	public ItemStack onEaten (ItemStack stack, World world, EntityPlayer player)
	{
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		boolean loaded = tags.getBoolean("Loaded");
		//boolean fired = tags.getBoolean("Fired");
		if (!loaded)
		{
			tags.setBoolean("Loaded", true);
		}
		/*else if (fired)
		{
			tags.setBoolean("Loaded", false);
			tags.setBoolean("Ready", false);
			tags.setBoolean("Fired", false);
		}*/
		return stack;
	}

	public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int time)
	{
		/*NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		boolean loaded = tags.getBoolean("Loaded");
		boolean ready = tags.getBoolean("Ready");
		boolean fired = tags.getBoolean("Fired");
		if (loaded)
		{
			tags.setBoolean("Ready", true);
		}
		if (loaded && ready && fired)
		{
			tags.setBoolean("Loaded", false);
			tags.setBoolean("Ready", false);
			tags.setBoolean("Fired", false);
		}*/
	}

	public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
	{
		/*NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		boolean loaded = tags.getBoolean("Loaded");
		boolean ready = tags.getBoolean("Ready");
		boolean fired = tags.getBoolean("Fired");
		if (loaded && ready && !fired)
		{
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote)
			{
				world.spawnEntityInWorld(new LaunchedPotion(world, player, stack));
			}
			tags.setBoolean("Fired", true);
		}
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));*/
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		if (!tags.getBoolean("Loaded"))
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	public void onUpdate (ItemStack stack, World world, Entity entity, int slot, boolean equipped)
	{
		/*if (equipped && entity instanceof EntityPlayer)
		{  
			EntityPlayer player = ((EntityPlayer) entity);
			if (!player.worldObj.isRemote)
			System.out.println(player.getItemInUse());
			NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
			//System.out.println("useTime "+((EntityPlayer) entity).getItemInUseCount());
		}*/
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration (ItemStack stack)
	{
		//if (!stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Loaded"))
			return 30;
		//else
			//return 72000;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction (ItemStack stack)
	{
		if (!stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Loaded"))
			return EnumAction.bow;
		else
			return EnumAction.none;
	}

	@SideOnly(Side.CLIENT)
	public void updateIcons (IconRegister par1IconRegister)
	{
		this.icons = new Icon[textureNames.length];

		for (int i = 0; i < this.icons.length; ++i)
		{
			this.icons[i] = par1IconRegister.registerIcon("tinker:" + textureNames[i]);
		}
	}

	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage (int meta)
	{
		return icons[0];
	}

	@Override
	public void getSubItems (int id, CreativeTabs tabs, List list)
	{
		ItemStack stack = new ItemStack(id, 1, 0);
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound tags = new NBTTagCompound();
		compound.setCompoundTag("InfiTool", tags);

		tags.setBoolean("Loaded", false);

		stack.setTagCompound(compound);

		list.add(stack);
	}

	@Override
	public boolean swingItem (EntityPlayer player, ItemStack stack)
	{
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		if (tags.getBoolean("Loaded"))
		{
			World world = player.worldObj;
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote)
			{
				world.spawnEntityInWorld(new LaunchedPotion(world, player, stack));
			}
			tags.setBoolean("Loaded", false);
			return false;
		}
		return true;
	}
}
