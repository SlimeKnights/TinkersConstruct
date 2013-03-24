package mods.tinker.tconstruct.tools;

import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.library.Weapon;
import net.minecraft.item.Item;

public class Longsword extends Weapon
{
	public Longsword(int itemID)
	{
		super(itemID, 4);
		this.setUnlocalizedName("InfiTool.Longsword");
	}
	
	/*public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }*/
	
	/*public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        NBTTagCompound tags = stack.getTagCompound();
        tags.getCompoundTag("InfiTool").setBoolean("InUse", true);
        return stack;
    }*/
	
	public float chargeAttack()
	{
		return 1.2f;
	}
	
	/*public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int time)
    {
		if (time > 5)
			AbilityHelper.thrust(stack, world, player);
		player.swingItem();
    }*/

	@Override
	protected Item getHeadItem ()
	{
		return TContent.swordBlade;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return TContent.medGuard;
	}
	
	/*@Override
	public boolean hitEntity(ItemStack stack, EntityLiving mob, EntityLiving player)
	{
		NBTTagCompound tags = stack.getTagCompound();
		System.out.println("Inuuse: "+tags.getCompoundTag("InfiTool").getBoolean("InUse"));
		if (tags.getCompoundTag("InfiTool").getBoolean("InUse"))
		{
			AbilityHelper.hitEntity(stack, mob, player, damageVsEntity, 2.0f);
			AbilityHelper.knockbackEntity(mob, 3);
			tags.getCompoundTag("InfiTool").setBoolean("InUse", false);
		}
		else
			AbilityHelper.hitEntity(stack, mob, player, damageVsEntity);
		
		return true;
	}*/
	
	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_longsword_blade";
		case 1:
			return "_longsword_blade_broken";
		case 2:
			return "_longsword_handle";
		case 3:
			return "_longsword_accessory";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_longsword_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "longsword";
	}
}
