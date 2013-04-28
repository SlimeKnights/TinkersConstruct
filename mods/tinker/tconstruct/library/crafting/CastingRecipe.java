package mods.tinker.tconstruct.library.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

public class CastingRecipe
{
	public ItemStack output;
	public LiquidStack castingMetal;
	public ItemStack cast;
	public boolean consumeCast;
	public int coolTime;
	
	public CastingRecipe(ItemStack replacement, LiquidStack metal, ItemStack cast, boolean consume, int delay)
	{
		castingMetal = metal;
		this.cast = cast;
		output = replacement;
		consumeCast = consume;
		coolTime = delay;
	}
	
	public boolean matches(LiquidStack metal, ItemStack cast)
	{
		if (castingMetal.isLiquidEqual(metal) && ItemStack.areItemStacksEqual(this.cast, cast))
			return true;
		else
			return false;
	}
	
	public ItemStack getResult()
	{
		return output.copy();
	}
}