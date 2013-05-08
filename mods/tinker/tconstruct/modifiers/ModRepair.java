package mods.tinker.tconstruct.modifiers;

import mods.tinker.tconstruct.library.crafting.PatternBuilder;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.ToolMod;
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
		//System.out.println("Increase: "+increase);
		
		int modifiers = tags.getInteger("Modifiers");
		float mods = 1.0f;
		if (modifiers == 2)
		    mods = 0.8f;
		else if (modifiers == 1)
		    mods = 0.6f;
		else if (modifiers == 0)
		    mods = 0.4f;
		
		increase *= mods;
		
        int repair = tags.getInteger("RepairCount");
        repair += 1;
        tags.setInteger("RepairCount", repair);
        
        float repairCount = (100 - repair) / 100f;
        if (repairCount < 0.5f)
            repairCount = 0.5f;
        increase *= repairCount;

        //System.out.println("Modified increase: "+increase);
		        
		damage -= increase;
		if (damage < 0)
			damage = 0;
		tags.setInteger("Damage", damage);
		
		AbilityHelper.damageTool(tool, 0, null, true);
		//tool.setItemDamage(damage * 100 / dur);
		
	}
	
	@Override
	public void addMatchingEffect (ItemStack tool) {}
}
