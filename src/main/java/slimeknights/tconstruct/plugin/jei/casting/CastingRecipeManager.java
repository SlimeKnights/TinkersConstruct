package slimeknights.tconstruct.plugin.jei.casting;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.display.FilteredRecipe;
import slimeknights.tconstruct.plugin.jei.util.manager.FluidRecipeCache;
import slimeknights.tconstruct.plugin.jei.util.manager.ItemRecipeCache;

import java.util.List;

/** Plugin handling filterable casting recipes. */
public class CastingRecipeManager implements ISimpleRecipeManagerPlugin<IDisplayableCastingRecipe> {
  @Getter
  private final List<IDisplayableCastingRecipe> allRecipes;
  private final ItemRecipeCache<IDisplayableCastingRecipe> inputItemCache, outputItemCache;
  private final FluidRecipeCache<IDisplayableCastingRecipe> fluidCache;

  public CastingRecipeManager(IIngredientManager ingredientManager, List<IDisplayableCastingRecipe> recipes) {
    this.allRecipes = FilteredRecipe.alwaysVisible(recipes);
    IIngredientHelper<ItemStack> itemHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
    IIngredientHelper<FluidStack> fluidHelper = ingredientManager.getIngredientHelper(ForgeTypes.FLUID_STACK);
    inputItemCache = new ItemRecipeCache<>(itemHelper, recipes, false);
    outputItemCache = new ItemRecipeCache<>(itemHelper, recipes, true);
    fluidCache = new FluidRecipeCache<>(fluidHelper, recipes, false);
  }

  @Override
  public boolean isHandledInput(ITypedIngredient<?> input) {
    IIngredientType<?> type = input.getType();
    return type == VanillaTypes.ITEM_STACK || type == ForgeTypes.FLUID_STACK;
  }

  @Override
  public boolean isHandledOutput(ITypedIngredient<?> output) {
    return output.getType() == VanillaTypes.ITEM_STACK;
  }

  @Override
  public List<IDisplayableCastingRecipe> getRecipesForInput(ITypedIngredient<?> input) {
    ITypedIngredient<ItemStack> item = input.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return inputItemCache.filterRecipes(item.getIngredient());
    }
    ITypedIngredient<FluidStack> fluid = input.cast(ForgeTypes.FLUID_STACK);
    if (fluid != null) {
      return fluidCache.filterRecipes(fluid.getIngredient());
    }
    return List.of();
  }

  @Override
  public List<IDisplayableCastingRecipe> getRecipesForOutput(ITypedIngredient<?> output) {
    ITypedIngredient<ItemStack> item = output.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return outputItemCache.filterRecipes(item.getIngredient());
    }
    return List.of();
  }
}
