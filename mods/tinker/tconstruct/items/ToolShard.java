package mods.tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ToolShard extends ToolPart
{

	public ToolShard(int id, String part, String tex)
	{
		super(id, part, tex);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	public void getSubItems(int id, CreativeTabs tab, List list)
    {
		for (int i = 1; i < 5; i++)
			list.add(new ItemStack(id, 1, i));
		for (int i = 6; i < 9; i++)
			list.add(new ItemStack(id, 1, i));
		for (int i = 10; i < 17; i++)
			list.add(new ItemStack(id, 1, i));
    }
}
