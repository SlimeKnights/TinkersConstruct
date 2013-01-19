package tinker.tconstruct.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/* Adds an integer NBTTag */

public class ModInteger extends ToolMod
{
	String key;
	int initialIncrease;
	int secondaryIncrease;
	
	public ModInteger(ItemStack[] items, int effect, String dataKey, int increase)
	{
		super(items, effect, dataKey);
		initialIncrease = secondaryIncrease = increase;
	}
	
	public ModInteger(ItemStack[] items, int effect, String dataKey, int increase1, int increase2)
	{
		super(items, effect, dataKey);
		initialIncrease = increase1;
		secondaryIncrease = increase2;
	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (tags.hasKey(key))
		{
			int increase = tags.getInteger(key);
			increase += secondaryIncrease;
		}
		else
		{
			tags.setInteger(key, initialIncrease);
		}
		
		int modifiers = tags.getInteger("Modifiers");
		modifiers -= 1;
		tags.setInteger("Modifiers", modifiers);
	}

}
