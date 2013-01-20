package tinker.tconstruct.tools;

import tinker.tconstruct.AbilityHelper;
import tinker.tconstruct.TConstructContent;
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
		AbilityHelper.hitEntity(stack, mob, player, damageVsEntity);
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

	@Override
	protected Item getHeadItem ()
	{
		return TConstructContent.swordBlade;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return TConstructContent.crossbar;
	}
}
