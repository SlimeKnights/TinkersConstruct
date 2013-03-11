package mods.tinker.tconstruct.modifiers;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/* IC2 support */

public class ModElectric extends ModBoolean
{	
	public ArrayList<ItemStack> circuits = new ArrayList<ItemStack>();
	public ArrayList<ItemStack> batteries = new ArrayList<ItemStack>();
	
	public ModElectric()
	{
		super(new ItemStack[0], 9, "Electric", "\u00a7e", "");
	}
	
	@Override
	public boolean matches (ItemStack[] input, ItemStack tool)
	{
		if (input[0] == null || input[1] == null)
			return false;
		
		boolean circuit = false;
		boolean battery = false;
		
		for (ItemStack stack : circuits)
		{
			if (stack.isItemEqual(input[0]) || stack.isItemEqual(input[1]))
				circuit = true;
		}
		
		for (ItemStack stack : batteries)
		{
			if (stack.isItemEqual(input[0]) || stack.isItemEqual(input[1]))
				battery = true;
		}
		return circuit && battery && canModify(tool, input);
	}
	
	/*@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (!tags.hasKey(key))
			return tags.getInteger("Modifiers") > 0;

		return false;
	}*/
	
	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound();
		
		if (!tags.hasKey(key))
		{
			tags.setBoolean(key, true);
			
			int modifiers = tags.getCompoundTag("InfiTool").getInteger("Modifiers");
			modifiers -= 1;
			tags.getCompoundTag("InfiTool").setInteger("Modifiers", modifiers);
			tags.setInteger("charge", 0);
			tags.setDouble("electricity", 0);
			tags.setInteger(key, 1);
			addModifierTip(tool, "\u00a7eElectric");
		}
	}
}
