package tinker.tconstruct.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

public class CastingRecipe
{
	public final ItemStack output;
	public final LiquidStack castingMetal;
	public final ItemStack cast;
	public final boolean consumeCast;
	public final int coolTime;
	
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
		if (metal.isLiquidEqual(metal) && metal.amount >= castingMetal.amount && ItemStack.areItemStacksEqual(this.cast, cast))
			return true;
		else
			return false;
	}
}