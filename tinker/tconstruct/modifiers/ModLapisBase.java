package tinker.tconstruct.modifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/* Little mod for actually adding the lapis modifier */

public class ModLapisBase extends ToolMod
{
	public ModLapisBase(ItemStack[] items, int effect)
	{
		super(items, effect, "Lapis");
	}

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (!tags.hasKey(key))
			return tags.getInteger("Modifiers") > 0;

		return false;
	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		tags.setBoolean(key, true);
		
		String modName = "\u00a79Lapis (0/100)";
		int tooltipIndex = addToolTip(tool, "\u00a79Luck", modName);
		int[] keyPair = new int[] { 0, tooltipIndex };
		tags.setIntArray(key, keyPair);
		
		int modifiers = tags.getInteger("Modifiers");
		modifiers -= 1;
		tags.setInteger("Modifiers", modifiers);
	}
}
