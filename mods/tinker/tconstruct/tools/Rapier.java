package mods.tinker.tconstruct.tools;

import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.library.AbilityHelper;
import mods.tinker.tconstruct.library.Weapon;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Rapier extends Weapon
{
	public Rapier(int itemID)
	{
		super(itemID, 2);
		this.setUnlocalizedName("InfiTool.Rapier");
	}

	public EnumAction getItemUseAction (ItemStack par1ItemStack)
	{
		return EnumAction.none;
	}

	public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
	{
		return stack;
	}

	@Override
	public boolean hitEntity (ItemStack stack, EntityLiving mob, EntityLiving player)
	{
		//AbilityHelper.hitEntity(stack, mob, player, damageVsEntity);
		AbilityHelper.knockbackEntity(mob, 0.8f);
		mob.motionY *= 0.5;
		if (mob.hurtResistantTime > 16)
			mob.hurtResistantTime -= 6;
		return true;
	}

	public float getDurabilityModifier ()
	{
		return 0.7f;
	}

	public float chargeAttack ()
	{
		return 2f;
	}

	public boolean pierceArmor ()
	{
		return true;
	}

	@Override
	protected Item getHeadItem ()
	{
		return TContent.swordBlade;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return TContent.crossbar;
	}

	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_rapier_blade";
		case 1:
			return "_rapier_blade_broken";
		case 2:
			return "_rapier_handle";
		case 3:
			return "_rapier_accessory";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_rapier_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "rapier";
	}
}
