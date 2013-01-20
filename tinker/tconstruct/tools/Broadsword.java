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
}
