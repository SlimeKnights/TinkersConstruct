package slimeknights.tconstruct.plugin.jei.casting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;

public class CastingRecipeChecker {

  private static CastingRecipeWrapper recipeWrapper;

  public static List<CastingRecipeWrapper> getCastingRecipes() {
    List<CastingRecipeWrapper> recipes = new ArrayList<>();
    Map<Triple<Item, Item, Fluid>, List<ItemStack>> castDict = Maps.newHashMap();

    for(ICastingRecipe irecipe : TinkerRegistry.getAllTableCastingRecipes()) {
      if(irecipe instanceof CastingRecipe) {
        CastingRecipe recipe = (CastingRecipe) irecipe;

        if(recipe.cast != null && recipe.getResult() != null && recipe.getResult().getItem() instanceof Cast) {
          Triple<Item, Item, Fluid> output = Triple.of(recipe.getResult().getItem(), Cast.getPartFromTag(recipe.getResult()), recipe.getFluid().getFluid());

          if(!castDict.containsKey(output)) {
            List<ItemStack> list = Lists.newLinkedList();
            castDict.put(output, list);

            recipeWrapper = new CastingRecipeWrapper(list, recipe, JEIPlugin.castingCategory.castingTable);

            if(recipeWrapper.isValid(false)) {
              recipes.add(recipeWrapper);
            }
          }

          castDict.get(output).addAll(recipe.cast.getInputs());
        }
        else {
          recipeWrapper = new CastingRecipeWrapper(recipe, JEIPlugin.castingCategory.castingTable);

          if(recipeWrapper.isValid(true)) {
            recipes.add(recipeWrapper);
          }
        }
      }
    }

    for(ICastingRecipe irecipe : TinkerRegistry.getAllBasinCastingRecipes()) {
      if(irecipe instanceof CastingRecipe) {
        CastingRecipe recipe = (CastingRecipe) irecipe;

        recipeWrapper = new CastingRecipeWrapper(recipe, JEIPlugin.castingCategory.castingBasin);

        if(recipeWrapper.isValid(true)) {
          recipes.add(recipeWrapper);
        }
      }
    }

    return recipes;
  }
}
