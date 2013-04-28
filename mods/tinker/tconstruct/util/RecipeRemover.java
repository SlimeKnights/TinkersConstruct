package mods.tinker.tconstruct.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;

public class RecipeRemover
{
	public static void removeShapedRecipes(List<ItemStack> removelist)
	{
		for (ItemStack stack : removelist)
			removeShapedRecipe(stack);
	}
	
	public static void removeShapedRecipe (ItemStack resultItem)
	{
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		for (int i = 0; i < recipes.size(); i++)
		{
			IRecipe tmpRecipe = recipes.get(i);
			if (tmpRecipe instanceof ShapedRecipes)
			{
				ShapedRecipes recipe = (ShapedRecipes) tmpRecipe;
				ItemStack recipeResult = recipe.getRecipeOutput();

				if (ItemStack.areItemStacksEqual(resultItem, recipeResult))
				{
					recipes.remove(i--);
				}
			}
		}
	}
}
