package tinker.tconstruct.crafting;

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
	private HashMap<List<Integer>, Float> materialReduction = new HashMap<List<Integer>, Float>();
	
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
	
	public CastingRecipe getCastingRecipe(LiquidStack metal, ItemStack cast)
	{
		for (CastingRecipe recipe : casts)
		{
			if (recipe.matches(metal, cast))
				return recipe;
		}
		return null;
	}
	
	public float getMaterialReduction(LiquidStack metal)
	{
		if (materialReduction.containsKey(Arrays.asList(metal.itemID, metal.itemMeta)))
		{
			return materialReduction.get(Arrays.asList(metal.itemID, metal.itemMeta));
		}
		return 1f;
	}
}
