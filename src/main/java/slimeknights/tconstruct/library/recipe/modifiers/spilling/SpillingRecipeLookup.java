package slimeknights.tconstruct.library.recipe.modifiers.spilling;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class handling a recipe cache for fluid spilling recipes, since any given fluid has one recipe
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpillingRecipeLookup {
  private static final Map<Fluid,SpillingRecipe> CACHE = new HashMap<>();
  static {
    RecipeCacheInvalidator.addReloadListener(client -> CACHE.clear());
  }

  /**
   * Gets the recipe for the given fluid
   * @param manager  Recipe manager
   * @param fluid    Fluid
   * @return  Recipe, or null if no recipe for this type
   */
  @Nullable
  public static SpillingRecipe findRecipe(RecipeManager manager, Fluid fluid) {
    if (CACHE.containsKey(fluid)) {
      return CACHE.get(fluid);
    }

    // find all severing recipes for the entity
    for (SpillingRecipe recipe : RecipeHelper.getRecipes(manager, TinkerRecipeTypes.SPILLING.get(), SpillingRecipe.class)) {
      if (recipe.matches(fluid)) {
        CACHE.put(fluid, recipe);
        return recipe;
      }
    }
    // cache null if nothing
    CACHE.put(fluid, null);
    return null;
  }
}
