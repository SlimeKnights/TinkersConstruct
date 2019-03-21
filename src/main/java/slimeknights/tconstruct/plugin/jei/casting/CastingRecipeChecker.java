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
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TinkerIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.shared.TinkerFluids;

public class CastingRecipeChecker {

  private static CastingRecipeWrapper recipeWrapper;

  public static List<CastingRecipeWrapper> getCastingRecipes() {
    List<CastingRecipeWrapper> recipes = new ArrayList<>();
    Map<Triple<Item, Item, Fluid>, List<ItemStack>> castDict = Maps.newHashMap();

    // skip recipes with brass or alubrass if those are not integrated
    // done since we have to register those fluids as casts in init, but don't know until postInit if they are used
    boolean hasAlubrass = TinkerIntegration.isIntegrated(TinkerFluids.alubrass);
    boolean hasBrass = TinkerIntegration.isIntegrated(TinkerFluids.brass);
    for(ICastingRecipe irecipe : TinkerRegistry.getAllTableCastingRecipes()) {
      if(irecipe instanceof CastingRecipe) {
        CastingRecipe recipe = (CastingRecipe) irecipe;

        // skip recipes that use either alubrass or brass
        if (fluidHidden(recipe.getFluid(), hasAlubrass, hasBrass)) {
          continue;
        }

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

  private static boolean fluidHidden(FluidStack fluidStack, boolean hasAlubrass, boolean hasBrass) {
    if(fluidStack == null) {
      return true;
    }
    Fluid fluid = fluidStack.getFluid();
    return fluid == null || (!hasAlubrass && fluid == TinkerFluids.alubrass) || (!hasBrass && fluid == TinkerFluids.brass);
  }
}
