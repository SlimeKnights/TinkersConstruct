package mods.tinker.tconstruct.tools;

import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.library.Weapon;
import net.minecraft.item.Item;

public class Broadsword extends Weapon
{
	public Broadsword(int itemID)
	{
		super(itemID, 4);
		this.setUnlocalizedName("InfiTool.Broadsword");
	}

	@Override
	protected Item getHeadItem ()
	{
		return TContent.swordBlade;
	}

	@Override
	protected Item getAccessoryItem ()
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
