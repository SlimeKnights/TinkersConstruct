package tinker.tconstruct;

import tinker.tconstruct.crafting.ToolBuilder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabTools extends CreativeTabs
{
	ItemStack display;
	
	public TabTools(String label) 
	{
		super(label);
	}
	
	public void init(ItemStack stack)
	{
		display = stack;
	}
	
	public ItemStack getIconItemStack()
    {
		return display;
    }
}