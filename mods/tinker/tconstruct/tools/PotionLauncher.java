package mods.tinker.tconstruct.tools;

import mods.tinker.tconstruct.entity.LaunchedPotion;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
		if (stack.getItemDamage() == 0)
			stack.setItemDamage(1);
		return stack;
	}
	
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int time)
    {
		if (stack.getItemDamage() == 1)
			stack.setItemDamage(2);
    }

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration (ItemStack stack)
	{
		int meta = stack.getItemDamage();
		if (meta == 1)
			return 72000;
		else if (meta == 2)
			return 0;
		
		return 30;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction (ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
			return EnumAction.bow;
		else
			return EnumAction.none;
	}
	
	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
	{
		if (stack.getItemDamage() == 2)
		{
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote)
			{
				world.spawnEntityInWorld(new LaunchedPotion(world, player, stack));
			}
			
			stack.damageItem(1, player);
			System.out.println("Rawr! "+stack.getItemDamage());
		}

		else
		{
			if (player.capabilities.isCreativeMode)
			{
				player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
			}
		}
		return stack;
	}

	/**
	 * Return the enchantability factor of the item, most of the time is based on material.
	 */
	public int getItemEnchantability ()
	{
		return 1;
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
}
