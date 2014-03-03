package common.darkknight.jewelrycraft;

import java.util.ArrayList;
import java.util.List;

import common.darkknight.jewelrycraft.item.ItemList;
import common.darkknight.jewelrycraft.util.JewelryNBT;
import common.darkknight.jewelrycraft.util.JewelrycraftUtil;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CreativeTabRings extends CreativeTabs
{
	public static ArrayList<ItemStack> metal = new ArrayList<ItemStack>();

	public CreativeTabRings(String par2Str) 
	{
		super(par2Str);
	}

	@Override
	public ItemStack getIconItemStack()
	{
		ItemStack ring = new ItemStack(ItemList.ring);
		JewelryNBT.addMetal(ring, JewelrycraftUtil.metal.get(0));
		JewelryNBT.addModifier(ring, JewelrycraftUtil.modifiers.get(3));
		JewelryNBT.addJewel(ring, JewelrycraftUtil.jewel.get(2));
		return ring;
	}

	public void displayAllReleventItems(List par1List)
	{
		ItemStack ring = new ItemStack(ItemList.ring);
		int index = 0, index2 = 0;
		while(index < OreDictionary.getOreNames().length)
		{
			while(index2 < OreDictionary.getOres(OreDictionary.getOreNames()[index]).size())
			{
				if(OreDictionary.getOres(OreDictionary.getOreNames()[index]).get(index2).getUnlocalizedName().toLowerCase().contains("ingot") && !metal.contains(OreDictionary.getOres(OreDictionary.getOreNames()[index]).get(index2)))
					metal.add(OreDictionary.getOres(OreDictionary.getOreNames()[index]).get(index2));
				index2++;
			}
			index2 = 0;
			index++;
		}   
		for(int i = 0; i < metal.size(); i++)
			for(int j = 0; j < JewelrycraftUtil.modifiers.size(); j++)
				for(int k = 0; k < JewelrycraftUtil.jewel.size(); k++)
				{
					par1List.add(ItemList.ring.getModifiedItemStack(metal.get(i), JewelrycraftUtil.modifiers.get(j),JewelrycraftUtil.jewel.get(k)));
				}
		//		par1List.removeAll(par1List);
		//		metal.removeAll(metal);
	}

}
