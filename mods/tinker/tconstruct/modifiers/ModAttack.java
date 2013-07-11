package mods.tinker.tconstruct.modifiers;

import java.util.Arrays;
import java.util.List;

import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.tools.ToolMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModAttack extends ToolMod
{
	String tooltipName;
	int increase;
	int max = 72;
	String guiType;

	public ModAttack(String type, ItemStack[] items, int effect, int inc)
	{
		super(items, effect, "ModAttack");
		tooltipName = "\u00a7fSharpness";
		guiType = type;
		increase = inc;
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
		    int amount = 24;
		    ToolCore toolItem = (ToolCore) tool.getItem();
		    if (toolItem.pierceArmor() || !nerfType(toolItem))
		        amount = 36;
		    
			int[] keyPair = tags.getIntArray(key);
			
			int leftToBoost = amount - (keyPair[0] % amount);
			if (increase >= leftToBoost)
			{	            
	            int attack = tags.getInteger("Attack");
	            attack += 1;
	            tags.setInteger("Attack", attack);
			}
			
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
			String modName = "\u00a7f"+guiType+" ("+increase+"/"+max+")";
			int tooltipIndex = addToolTip(tool, tooltipName, modName);
			int[] keyPair = new int[] { increase, max, tooltipIndex };
			tags.setIntArray(key, keyPair);
            
            int attack = tags.getInteger("Attack");
            attack += 1;
            tags.setInteger("Attack", attack);
		}
	}
	
	void updateModTag(ItemStack tool, int[] keys)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		String tip = "ModifierTip"+keys[2];
		String modName = "\u00a7f"+guiType+" ("+keys[0]+"/"+keys[1]+")";
		tags.setString(tip, modName);
	}
	
    public boolean validType(ToolCore tool)
    {
        return true;
        /*List list = Arrays.asList(tool.toolCategories());
        return list.contains("melee") || list.contains("harvest") || list.contains("ammo");*/
    }
	
	public boolean nerfType(ToolCore tool)
	{
	    List list = Arrays.asList(tool.toolCategories());
	    return list.contains("throwing") || list.contains("ammo");
	}
}
