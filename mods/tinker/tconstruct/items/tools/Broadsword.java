package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.tools.Weapon;
import net.minecraft.item.Item;

public class Broadsword extends Weapon
{
	public Broadsword(int itemID)
	{
		super(itemID, 4);
		this.setUnlocalizedName("InfiTool.Broadsword");
	}

	@Override
	public Item getHeadItem ()
	{
		return TContent.swordBlade;
	}

	@Override
	public Item getAccessoryItem ()
	{
		return TContent.wideGuard;
	}
	
	public float getDurabilityModifier ()
	{
		return 1.2f;
	}
	
	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_sword_blade";
		case 1:
			return "_sword_blade_broken";
		case 2:
			return "_sword_handle";
		case 3:
			return "_sword_accessory";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_sword_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "broadsword";
	}
}
