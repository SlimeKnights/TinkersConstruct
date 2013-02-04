package tinker.tconstruct.tools;

import tinker.tconstruct.AbilityHelper;
import tinker.tconstruct.TContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Rapier extends Weapon
{
	public Rapier(int itemID, String tex)
	{
		super(itemID, 2, tex);
		this.setItemName("InfiTool.Rapier");
	}
	
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.none;
    }
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        return stack;
    }
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLiving mob, EntityLiving player)
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
	
	public float chargeAttack()
	{
		return 2f;
	}
	
	public boolean pierceArmor()
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
	
	protected String getRenderString (int renderPass, boolean broken)
	{
		switch (renderPass)
		{
		case 0:
			return "_rapier_handle.png";
		case 1:
			if (broken)
				return "_rapier_blade_broken.png";
			else
				return "_rapier_blade.png";
		case 2:
			return "_rapier_accessory.png";
		default:
			return "";
		}
	}

	protected String getEffectString (int renderPass)
	{
		return "_rapier_effect.png";
	}
}
