package tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class BlankPattern extends CraftingItem
{
	public BlankPattern(int id, int icon, String tex)
	{
		super(id, icon, tex);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	public String getItemNameIS(ItemStack stack)
	{
		int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, 2);
		return getItemName() + "." +patternTypes[arr];
	}

	public void getSubItems (int id, CreativeTabs tab, List list)
	{
		for (int i = 0; i < 2; i++)
			list.add(new ItemStack(id, 1, i));
	}

	public static final String[] patternTypes = new String[] { 
		"pattern", "cast" };
}
