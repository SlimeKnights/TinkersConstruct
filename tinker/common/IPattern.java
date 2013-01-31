package tinker.common;

import net.minecraft.item.ItemStack;
import tinker.tconstruct.crafting.PatternBuilder.MaterialSet;

public interface IPattern
{
	public int getPatternCost (int metadata);
	public ItemStack getPatternOutput(ItemStack stack, MaterialSet set);
}
