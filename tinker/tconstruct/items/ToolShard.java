package tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ToolShard extends ToolPart
{

	public ToolShard(int id, int icon, String tex)
	{
		super(id, icon, tex);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	public void getSubItems(int id, CreativeTabs tab, List list)
    {
		for (int i = 1; i < 5; i++)
			list.add(new ItemStack(id, 1, i));
		for (int i = 6; i < 9; i++)
			list.add(new ItemStack(id, 1, i));
		for (int i = 10; i < 15; i++)
			list.add(new ItemStack(id, 1, i));
    }
}
