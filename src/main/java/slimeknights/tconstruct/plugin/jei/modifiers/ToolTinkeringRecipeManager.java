package slimeknights.tconstruct.plugin.jei.modifiers;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import slimeknights.tconstruct.library.recipe.display.FilteredRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayCraftingTinkering;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolTinkering;
import slimeknights.tconstruct.plugin.jei.util.manager.ItemRecipeCache;

import java.util.List;

/** Category handling tool modification recipes. */
public class ToolTinkeringRecipeManager implements ISimpleRecipeManagerPlugin<IDisplayToolTinkering> {
  @Getter
  private final List<IDisplayToolTinkering> allRecipes;
  private final ItemRecipeCache<IDisplayToolTinkering> inputItemCache, outputItemCache;

  public ToolTinkeringRecipeManager(IIngredientManager ingredientManager, List<IDisplayToolTinkering> recipes) {
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
  public List<IDisplayToolTinkering> getRecipesForInput(ITypedIngredient<?> input) {
    ITypedIngredient<ItemStack> item = input.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return inputItemCache.filterRecipes(item.getIngredient());
    }
    return List.of();
  }

  @Override
  public List<IDisplayToolTinkering> getRecipesForOutput(ITypedIngredient<?> output) {
    ITypedIngredient<ItemStack> item = output.cast(VanillaTypes.ITEM_STACK);
    if (item != null) {
      return outputItemCache.filterRecipes(item.getIngredient());
    }
    return List.of();
  }

  /** Creates an instance of the category for the crafting table. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static ISimpleRecipeManagerPlugin<CraftingRecipe> createCrafting(IIngredientManager ingredientManager, List<IDisplayCraftingTinkering> recipes) {
    return (ISimpleRecipeManagerPlugin<CraftingRecipe>)(ISimpleRecipeManagerPlugin) new ToolTinkeringRecipeManager(ingredientManager, (List<IDisplayToolTinkering>)(List)recipes);
  }
}
