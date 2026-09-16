package slimeknights.tconstruct.plugin.jei.util.manager;

import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.recipe.display.FilteredItemRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Cache for searching for recipes from a list of {@link FilteredItemRecipe} */
public class ItemRecipeCache<T extends FilteredItemRecipe> extends AbstractRecipeCache<T, ItemStack> {
  private final boolean output;
  public ItemRecipeCache(IIngredientHelper<ItemStack> helper, List<? extends T> recipes, boolean output) {
    super(helper, recipes);
    this.output = output;
  }

  @Override
  protected boolean matches(T recipe, Predicate<ItemStack> predicate) {
    return recipe.matchesItem(predicate, output);
  }

  @Override
  public List<T> filterRecipes(ItemStack focus) {
    List<T> recipes = matchingRecipes(focus);
    if (recipes.isEmpty()) {
      return List.of();
    }
    List<T> filtered = new ArrayList<>(recipes.size());
    for (T recipe : recipes) {
      if (recipe.isVisibleFromItem(focus, output)) {
        filtered.add(recipe);
      }
    }
    return filtered;
  }
}
