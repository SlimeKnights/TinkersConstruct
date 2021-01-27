package slimeknights.tconstruct.smeltery.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.client.IEarlySafeManagerReloadListener;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class handling a recipe cache for entity melting recipes
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityMeltingRecipeCache implements IEarlySafeManagerReloadListener {
  private static final Map<EntityType<?>,EntityMeltingRecipe> CACHE = new HashMap<>();
  private static final EntityMeltingRecipeCache INSTANCE = new EntityMeltingRecipeCache();

  /**
   * Gets the recipe for the given type
   * @param manager  Recipe manager
   * @param type     Entity type
   * @return  Recipe, or null if no recipe for this type
   */
  @Nullable
  public static EntityMeltingRecipe findRecipe(RecipeManager manager, EntityType<?> type) {
    if (CACHE.containsKey(type)) {
      return CACHE.get(type);
    }

    // find a recipe if none exist
    for (EntityMeltingRecipe recipe : RecipeHelper.getRecipes(manager, RecipeTypes.ENTITY_MELTING, EntityMeltingRecipe.class)) {
      if (recipe.matches(type)) {
        CACHE.put(type, recipe);
        return recipe;
      }
    }

    // cache nothing was found
    CACHE.put(type, null);
    return null;
  }

  @Override
  public void onReloadSafe(IResourceManager resourceManager) {
    CACHE.clear();
  }

  /**
   * Called when resource managers reload
   * @param event  Reload event
   */
  public static void onReloadListenerReload(AddReloadListenerEvent event) {
    event.addListener(INSTANCE);
  }
}
