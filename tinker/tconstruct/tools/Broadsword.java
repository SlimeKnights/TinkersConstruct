package tinker.tconstruct.tools;

import tinker.tconstruct.AbilityHelper;
import tinker.tconstruct.TConstructContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Broadsword extends Weapon
{
	public Broadsword(int itemID, String tex)
	{
		super(itemID, 4, tex);
		this.setItemName("InfiTool.Broadsword");
	}

	@Override
	protected Item getHeadItem ()
	{
		return TConstructContent.swordBlade;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return TConstructContent.largeGuard;
	}
	
	public float getDurabilityModifier ()
	{
		return 1.2f;
	}
	
	protected String getRenderString (int renderPass, boolean broken)
	{
		switch (renderPass)
		{
		case 0:
			return "_sword_handle.png";
		case 1:
			if (broken)
				return "_sword_blade_broken.png";
			else
				return "_sword_blade.png";
		case 2:
			return "_sword_accessory.png";
		default:
			return "";
		}
	}

	protected String getEffectString (int renderPass)
	{
		return "_sword_effect.png";
	}
}
