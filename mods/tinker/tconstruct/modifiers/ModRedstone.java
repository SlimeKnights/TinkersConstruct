package mods.tinker.tconstruct.modifiers;

import java.util.Arrays;
import java.util.List;

import mods.tinker.common.ToolMod;
import mods.tinker.tconstruct.library.ToolCore;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModRedstone extends ToolMod
{
	String tooltipName;
	int increase;
	int max;

	public ModRedstone(ItemStack[] items, int effect, int inc)
	{
		super(items, effect, "Redstone");
		tooltipName = "\u00a74Haste";
		increase = inc;
		max = 50;
	}

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		ToolCore toolItem = (ToolCore) tool.getItem();
		if (!validType(toolItem))
		    return false;
		
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (!tags.hasKey(key))
			return tags.getInteger("Modifiers") > 0;

		int keyPair[] = tags.getIntArray(key);
		if (keyPair[0] + increase <= keyPair[1])
			return true;

		
		else if (keyPair[0] == keyPair[1])
			return tags.getInteger("Modifiers") > 0;

		else
			return false;
	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (tags.hasKey(key))
		{
			int[] keyPair = tags.getIntArray(key);
			if (keyPair[0] % max == 0)
			{
				keyPair[0] += increase;
				keyPair[1] += max;
				tags.setIntArray(key, keyPair);
				
				int modifiers = tags.getInteger("Modifiers");
				modifiers -= 1;
				tags.setInteger("Modifiers", modifiers);
			}
			else
			{
				keyPair[0] += increase;
				tags.setIntArray(key, keyPair);
			}
			updateModTag(tool, keyPair);
		}
		else
		{
			int modifiers = tags.getInteger("Modifiers");
			modifiers -= 1;
			tags.setInteger("Modifiers", modifiers);
			String modName = "\u00a74Redstone ("+increase+"/"+max+")";
			int tooltipIndex = addToolTip(tool, tooltipName, modName);
			int[] keyPair = new int[] { increase, max, tooltipIndex };
			tags.setIntArray(key, keyPair);
		}
		
		int miningSpeed = tags.getInteger("MiningSpeed");
		miningSpeed += (increase*8);
		tags.setInteger("MiningSpeed", miningSpeed);
		
		if (tags.hasKey("MiningSpeed2"))
		{
			int miningSpeed2 = tags.getInteger("MiningSpeed2");
			miningSpeed2 += (increase*8);
			tags.setInteger("MiningSpeed2", miningSpeed2);
		}
	}
	
	void updateModTag(ItemStack tool, int[] keys)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		String tip = "ModifierTip"+keys[2];
		String modName = "\u00a74Redstone ("+keys[0]+"/"+keys[1]+")";
		tags.setString(tip, modName);
	}
	
	public boolean validType(ToolCore tool)
    {
        List list = Arrays.asList(tool.toolCategories());
        return list.contains("harvest");
    }
}
