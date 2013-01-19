package tinker.tconstruct.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/* Adds a boolean NBTTag */

public class ModBoolean extends ToolMod
{	
	public ModBoolean(ItemStack[] items, int effect, String tag)
	{
		super(items, effect, tag);
	}
	
	protected boolean canModify (ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(key); //Will fail if the modifier is false or the tag doesn't exist
	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		
		if (!tags.hasKey(key))
		{
			tags.setBoolean(key, true);
			
			int modifiers = tags.getInteger("Modifiers");
			modifiers -= 1;
			tags.setInteger("Modifiers", modifiers);
		}
	}
}
