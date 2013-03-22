package mods.tinker.tconstruct.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

/* Melting becomes hardened */
public class LiquidCasting
{
	public static LiquidCasting instance = new LiquidCasting();
	private ArrayList<CastingRecipe> casts = new ArrayList<CastingRecipe>();
	
	public static void addCastingRecipe(ItemStack replacement, LiquidStack metal, ItemStack cast, boolean consume, int delay)
	{
		instance.casts.add(new CastingRecipe(replacement, metal, cast, consume, delay));
	}
	
	public static void addCastingRecipe(ItemStack replacement, LiquidStack metal, ItemStack cast, int delay)
		{ addCastingRecipe(replacement, metal, cast, false, delay); }
	public static void addCastingRecipe(ItemStack replacement, LiquidStack metal, int delay)
		{ addCastingRecipe(replacement, metal, null, false, delay); }
	
	public int getCastingDelay(LiquidStack metal, ItemStack cast)
	{
		for (CastingRecipe recipe : casts)
		{
			if (recipe.matches(metal, cast))
				return recipe.coolTime;
		}
		return -1;
	}
	
	public int getCastingAmount(LiquidStack metal, ItemStack cast)
	{
		CastingRecipe recipe = getCastingRecipe(metal, cast);
		if (recipe != null)
			return recipe.castingMetal.amount;
		return 0;
	}
	
	public CastingRecipe getCastingRecipe(LiquidStack metal, ItemStack cast)
	{
		for (CastingRecipe recipe : casts)
		{
			if (recipe.matches(metal, cast))
				return recipe;
		}
		return null;
	}
}
