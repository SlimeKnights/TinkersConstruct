package tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MetalPattern extends Pattern
{

	public MetalPattern(int id, int icon, String tex)
	{
		super(id, icon, tex);
	}

	public void getSubItems (int id, CreativeTabs tab, List list)
	{
		for (int i = 0; i < patternName.length; i++)
			list.add(new ItemStack(id, 1, i));
	}
}
