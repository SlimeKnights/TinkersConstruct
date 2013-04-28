package mods.tinker.tconstruct.library;

import mods.tinker.tconstruct.library.crafting.PatternBuilder.MaterialSet;
import net.minecraft.item.ItemStack;

public interface IPattern
{
	public int getPatternCost (int metadata);
	public ItemStack getPatternOutput(ItemStack stack, MaterialSet set);
}
