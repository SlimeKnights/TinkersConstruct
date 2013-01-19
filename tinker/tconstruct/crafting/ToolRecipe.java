package tinker.tconstruct.crafting;

import tinker.tconstruct.TConstructContent;
import tinker.tconstruct.tools.ToolCore;
import net.minecraft.item.Item;

/*
 * Small class for checking if a particular tool combination is this one
 */

public class ToolRecipe
{
	Item head;
	Item accessory;
	ToolCore item;
	
	public ToolRecipe(Item h, Item acc, ToolCore i)
	{
		head = h;
		accessory = acc;
		item = i;
	}
	
	public boolean validHead(Item he)
	{
		if (head == he)
			return true;
		else
			return false;
	}
	
	public boolean validHandle(Item handle)
	{
		return (handle == TConstructContent.toolRod || handle == Item.stick || handle == Item.bone);
	}
	
	public boolean validAccessory(Item acc)
	{
		if (accessory == null && acc == null)
			return true;
		else if (accessory == TConstructContent.toolRod)
			return validHandle(acc);
		else if (accessory == acc)
			return true;
		else
			return false;
	}
	
	public ToolCore getType()
	{
		return item;
	}
}
