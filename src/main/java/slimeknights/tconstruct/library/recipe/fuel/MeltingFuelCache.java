package slimeknights.tconstruct.library.recipe.fuel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.recipe.RecipeTypes;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class handling a recipe cache for fuel recipes, since any given entity type has one recipe
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeltingFuelCache {
  private static final Map<Fluid,MeltingFuel> CACHE = new HashMap<>();

  static {
    // register listener to reset on cache clear
    RecipeCacheInvalidator.addReloadListener(client -> CACHE.clear());
  }

  /**
   * Gets the recipe for the given type
   * @param manager  Recipe manager
   * @param fluid   Fluid found
   * @return  Recipe, or null if no recipe for this type
   */
  @Nullable
  public static MeltingFuel findRecipe(RecipeManager manager, Fluid fluid) {
    if (CACHE.containsKey(fluid)) {
      return CACHE.get(fluid);
    }

    // find a recipe if none exist
    for (MeltingFuel recipe : RecipeHelper.getRecipes(manager, RecipeTypes.FUEL, MeltingFuel.class)) {
      if (recipe.matches(fluid)) {
        CACHE.put(fluid, recipe);
        return recipe;
      }
    }

    // cache nothing was found
    CACHE.put(fluid, null);
    return null;
  }
}
