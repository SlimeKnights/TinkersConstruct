package slimeknights.tconstruct.library.recipe.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaterialRecipeCache {
  private static final List<MaterialRecipe> RECIPES = new ArrayList<>();
  private static final Map<Item, MaterialRecipe> CACHE = new ConcurrentHashMap<>();

  /** Listener for clearing the cache */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    RECIPES.clear();
    CACHE.clear();
  });

  /** Registers a recipe with the cache */
  public static void registerRecipe(MaterialRecipe recipe) {
    if (recipe.getValue() > 0) {
      LISTENER.checkClear();
      RECIPES.add(recipe);
    }
  }

  /**
   * Locates a recipe by stack
   * @param stack  Stack to check
   * @return Recipe, or {@link MaterialRecipe#EMPTY} if no match.
   */
  public static MaterialRecipe findRecipe(ItemStack stack) {
    if (stack.isEmpty()) {
      return MaterialRecipe.EMPTY;
    }
    return CACHE.computeIfAbsent(stack.getItem(), item -> {
      for (MaterialRecipe recipe : RECIPES) {
        if (recipe.getIngredient().test(stack)) {
          return recipe;
        }
      }
      return MaterialRecipe.EMPTY;
    });
  }

  public static Collection<MaterialRecipe> getAllRecipes() {
    return RECIPES;
  }
}
