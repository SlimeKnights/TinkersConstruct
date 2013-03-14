package mods.tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MetalPattern extends Pattern
{

	public MetalPattern(int id, String partType, String patternType, String folder)
	{
		super(id, partType, patternType, folder);
	}

	public void getSubItems (int id, CreativeTabs tab, List list)
	{
		for (int i = 0; i < patternName.length; i++)
			list.add(new ItemStack(id, 1, i));
	}
}
