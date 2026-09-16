package slimeknights.tconstruct.plugin.jei.modifiers;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.recipe.display.FilteredRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.plugin.jei.util.ItemRecipeCache;

import java.util.List;

public class ToolModificationRecipeManager implements ISimpleRecipeManagerPlugin<IDisplayToolModification> {
  @Getter
  private final List<IDisplayToolModification> allRecipes;
  private final ItemRecipeCache<IDisplayToolModification> inputItemCache, outputItemCache;

  public ToolModificationRecipeManager(IIngredientManager ingredientManager, List<IDisplayToolModification> recipes) {
    this.allRecipes = FilteredRecipe.alwaysVisible(recipes);
    IIngredientHelper<ItemStack> itemHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
    inputItemCache = new ItemRecipeCache<>(itemHelper, recipes, false);
    outputItemCache = new ItemRecipeCache<>(itemHelper, recipes, true);
  }

  @Override
  public boolean isHandledInput(ITypedIngredient<?> input) {
    return input.getType() == VanillaTypes.ITEM_STACK;
  }

  @Override
  public boolean isHandledOutput(ITypedIngredient<?> output) {
    return output.getType() == VanillaTypes.ITEM_STACK;
  }

  @Override
  public List<IDisplayToolModification> getRecipesForInput(ITypedIngredient<?> input) {
    ITypedIngredient<ItemStack> item = input.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return inputItemCache.filterRecipes(item.getIngredient());
    }
    return List.of();
  }

  @Override
  public List<IDisplayToolModification> getRecipesForOutput(ITypedIngredient<?> output) {
    ITypedIngredient<ItemStack> item = output.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return outputItemCache.filterRecipes(item.getIngredient());
    }
    return List.of();
  }
}
