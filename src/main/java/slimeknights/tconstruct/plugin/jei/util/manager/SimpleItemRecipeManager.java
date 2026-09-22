package slimeknights.tconstruct.plugin.jei.util.manager;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import slimeknights.tconstruct.library.recipe.display.FilteredItemRecipe;
import slimeknights.tconstruct.library.recipe.display.FilteredRecipe;

import java.util.List;

/** Recipe manager handling filtering recipes that use just items for inputs and outputs. Used notably in {@link slimeknights.tconstruct.plugin.jei.modifiers.ToolTinkeringCategory}. */
public class SimpleItemRecipeManager<T extends FilteredItemRecipe> implements ISimpleRecipeManagerPlugin<T> {
  @Getter
  private final List<T> allRecipes;
  private final ItemRecipeCache<T> inputItemCache, outputItemCache;

  public SimpleItemRecipeManager(IIngredientManager ingredientManager, List<T> recipes) {
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
  public List<T> getRecipesForInput(ITypedIngredient<?> input) {
    ITypedIngredient<ItemStack> item = input.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return inputItemCache.filterRecipes(item.getIngredient());
    }
    return List.of();
  }

  @Override
  public List<T> getRecipesForOutput(ITypedIngredient<?> output) {
    ITypedIngredient<ItemStack> item = output.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return outputItemCache.filterRecipes(item.getIngredient());
    }
    return List.of();
  }

  /** Creates an instance of the category for the crafting table. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T extends CraftingRecipe & FilteredItemRecipe> ISimpleRecipeManagerPlugin<CraftingRecipe> createCrafting(IIngredientManager ingredientManager, List<T> recipes) {
    return (ISimpleRecipeManagerPlugin<CraftingRecipe>) new SimpleItemRecipeManager(ingredientManager, recipes);
  }
}
