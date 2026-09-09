package slimeknights.tconstruct.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Simple recipe for serializing a recipe with a loadable.
 * TODO: move to Mantle
 */
public record LoadableFinishedRecipe<T extends Recipe<?>>(RecordLoadable<T> loader, T recipe) implements FinishedRecipe {
  @Override
  public ResourceLocation getId() {
    return recipe.getId();
  }

  @Override
  public void serializeRecipeData(JsonObject json) {
    loader.serialize(recipe, json);
  }

  @Override
  public RecipeSerializer<?> getType() {
    return recipe.getSerializer();
  }

  @Nullable
  @Override
  public JsonObject serializeAdvancement() {
    return null;
  }

  @Nullable
  @Override
  public ResourceLocation getAdvancementId() {
    return null;
  }

  /** One-liner to accept a new recipe */
  public static <T extends Recipe<?>> void save(Consumer<FinishedRecipe> consumer, RecordLoadable<T> loader, T recipe) {
    consumer.accept(new LoadableFinishedRecipe<>(loader, recipe));
  }
}
