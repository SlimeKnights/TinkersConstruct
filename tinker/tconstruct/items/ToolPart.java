package tinker.tconstruct.items;


import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import tinker.common.IToolPart;

public class ToolPart extends CraftingItem
	implements IToolPart
{
	public ToolPart(int id, int icon, String tex)
	{
		super(id, icon, tex);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	public String getItemNameIS(ItemStack stack)
	{
		int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, 15);
		return getItemName() + "." +toolMaterialNames[arr];
	}
	
	public void getSubItems(int id, CreativeTabs tab, List list)
    {
		for (int i = 0; i < 17; i++)
			list.add(new ItemStack(id, 1, i));
    }
	
	public static final String[] toolMaterialNames = new String[] { 
		"Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Alumite", "Steel" };

	@Override
	public int getMaterialID (ItemStack stack)
	{
		return stack.getItemDamage();
	}
}
