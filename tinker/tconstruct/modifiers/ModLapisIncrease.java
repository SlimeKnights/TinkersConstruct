package tinker.tconstruct.modifiers;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import tinker.common.ToolMod;
import tinker.tconstruct.tools.Weapon;

public class ModLapisIncrease extends ToolMod
{
	int increase;

	public ModLapisIncrease(ItemStack[] items, int effect, int inc)
	{
		super(items, effect, "Lapis");
		increase = inc;
	}

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (!tags.hasKey(key))
			return false;

		int keyPair[] = tags.getIntArray(key);
		
		if (keyPair[0] + increase <= 100)
			return true;
		else
			return false;

	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int keyPair[] = tags.getIntArray(key);
		keyPair[0] += increase;
		tags.setIntArray(key, keyPair);
		if (tool.getItem() instanceof Weapon)
		{
			if (keyPair[0] >= 100)
				addEnchantment(tool, Enchantment.looting, 3);
			else if (keyPair[0] >= 40)
				addEnchantment(tool, Enchantment.looting, 2);
			else if (keyPair[0] >= 10)
				addEnchantment(tool, Enchantment.looting, 1);
		}
		else
		{
			if (keyPair[0] >= 100)
				addEnchantment(tool, Enchantment.fortune, 3);
			else if (keyPair[0] >= 40)
				addEnchantment(tool, Enchantment.fortune, 2);
			else if (keyPair[0] >= 10)
				addEnchantment(tool, Enchantment.fortune, 1);
		}
		
		updateModTag(tool, keyPair);
	}
	
	public void addEnchantment(ItemStack tool, Enchantment enchant, int level)
    {
		/*if (!tool.stackTagCompound.hasKey("ench"))
        {
			tool.stackTagCompound.setTag("ench", new NBTTagList("ench"));
        }*/

        //NBTTagList tags = (NBTTagList)tool.stackTagCompound.getTag("ench");
		NBTTagList tags = new NBTTagList("ench");
        Map enchantMap = EnchantmentHelper.getEnchantments(tool);
        Iterator iterator = enchantMap.keySet().iterator();
        int index;
        int lvl;
        boolean hasEnchant = false;
        while (iterator.hasNext())
        {
    		NBTTagCompound enchantTag = new NBTTagCompound();
        	index = ((Integer)iterator.next()).intValue();
        	lvl = (Integer) enchantMap.get(index);
        	if (index == enchant.effectId)
        	{
        		hasEnchant = true;
        		enchantTag.setShort("id", (short)index);
        		enchantTag.setShort("lvl", (short)((byte)level));
        		tags.appendTag(enchantTag);
        	}
        	else
        	{
        		enchantTag.setShort("id", (short)index);
        		enchantTag.setShort("lvl", (short)((byte)lvl));
        		tags.appendTag(enchantTag);
        	}
        }
        if (!hasEnchant)
        {
        	NBTTagCompound enchantTag = new NBTTagCompound();
        	enchantTag.setShort("id", (short)enchant.effectId);
        	enchantTag.setShort("lvl", (short)((byte)level));
        	tags.appendTag(enchantTag);
        }
        //this.stackTagCompound.setTag("ench", new NBTTagList("ench"));
        tool.stackTagCompound.setTag("ench", tags);
        /*if (enchantMap.containsKey(enchant.effectId))
        {
        	map.
        }*/
        //NBTTagList enchantTags = (NBTTagList)tool.stackTagCompound.getTag("ench");
        /*NBTTagCompound var4 = new NBTTagCompound();
        var4.setShort("id", (short)par1Enchantment.effectId);
        var4.setShort("lvl", (short)((byte)par2));
        enchantTags.appendTag(var4);*/
    }
	
	void updateModTag (ItemStack tool, int[] keys)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		String tip = "ModifierTip"+keys[1];
		String modName = "\u00a79Lapis ("+keys[0]+"/100)";
		tags.setString(tip, modName);
	}
}
