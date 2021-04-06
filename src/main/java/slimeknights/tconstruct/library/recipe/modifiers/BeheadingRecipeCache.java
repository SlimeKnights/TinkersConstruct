package slimeknights.tconstruct.library.recipe.modifiers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.RecipeManager;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.recipe.RecipeTypes;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class handling a recipe cache for entity melting recipes, since any given entity type has one recipe
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeheadingRecipeCache {
  private static final Map<EntityType<?>,BeheadingRecipe> CACHE = new HashMap<>();

  static {
    RecipeCacheInvalidator.addReloadListener(client -> CACHE.clear());
  }

  /**
   * Gets the recipe for the given type
   * @param manager  Recipe manager
   * @param type     Entity type
   * @return  Recipe, or null if no recipe for this type
   */
  @Nullable
  public static BeheadingRecipe findRecipe(RecipeManager manager, EntityType<?> type) {
    if (CACHE.containsKey(type)) {
      return CACHE.get(type);
    }

    // find a recipe if none exist
    for (BeheadingRecipe recipe : RecipeHelper.getRecipes(manager, RecipeTypes.BEHEADING, BeheadingRecipe.class)) {
      if (recipe.matches(type)) {
        CACHE.put(type, recipe);
        return recipe;
      }
    }

    // cache nothing was found
    CACHE.put(type, null);
    return null;
  }
}
