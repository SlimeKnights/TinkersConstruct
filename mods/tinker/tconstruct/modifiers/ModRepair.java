package mods.tinker.tconstruct.modifiers;

import mods.tinker.tconstruct.library.ToolMod;
import mods.tinker.tconstruct.library.crafting.PatternBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/* Little mod for actually adding the lapis modifier */

public class ModRepair extends ToolMod
{

	public ModRepair()
	{
		super(new ItemStack[0], 0, "");
	}
	
	@Override
	public boolean matches (ItemStack[] input, ItemStack tool)
	{
		return canModify(tool, input);
	}

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		if ((input[0] == null && input[1] == null) || (input[0] != null && input[1] != null)) //Only valid for one itemstack
			return false;
		
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");		
		if (tags.getInteger("Damage") > 0)
		{
			int headID = tags.getInteger("Head");
			int matID = -1;
			if (input [0] != null)
				matID = PatternBuilder.instance.getPartID(input[0]);
			else
				matID = PatternBuilder.instance.getPartID(input[1]);
			
			if (matID == headID)
				return true;
		}
		return false;
	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		tags.setBoolean("Broken", false);
		int damage = tags.getInteger("Damage");
		int dur = tags.getInteger("BaseDurability");
		
		int materialValue = 0;
		if (input [0] != null)
			materialValue = PatternBuilder.instance.getPartValue(input[0]);
		else
			materialValue = PatternBuilder.instance.getPartValue(input[1]);
		
		int increase = (int) (50 + (dur * 0.4f * materialValue));
		damage -= increase;
		if (damage < 0)
			damage = 0;
		tags.setInteger("Damage", damage);
		
		tool.setItemDamage(damage * 100 / dur);
		
		int repair = tags.getInteger("RepairCount");
		repair += 1;
		tags.setInteger("RepairCount", repair);
	}
	
	@Override
	public void addMatchingEffect (ItemStack tool) {}
}
