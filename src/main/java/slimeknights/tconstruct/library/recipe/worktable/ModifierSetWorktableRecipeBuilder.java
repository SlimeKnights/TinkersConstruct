package slimeknights.tconstruct.library.recipe.worktable;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for recipes to add or remove a modifier from a set in persistent data */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifierSetWorktableRecipeBuilder extends AbstractSizedIngredientRecipeBuilder<ModifierSetWorktableRecipeBuilder> {
  private final ResourceLocation dataKey;
  private final TagKey<Modifier> blacklist;
  private final boolean addToSet;

  /** Creates a new recipe for adding to a set */
  public static ModifierSetWorktableRecipeBuilder setAdding(ResourceLocation dataKey, TagKey<Modifier> blacklist) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, blacklist, true);
  }

  /** Creates a new recipe for removing from a set */
  public static ModifierSetWorktableRecipeBuilder setRemoving(ResourceLocation dataKey, TagKey<Modifier> blacklist) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, blacklist, false);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, dataKey);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least one ingredient");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new Finished(id, advancementId));
  }

  private class Finished extends SizedFinishedRecipe {
    public Finished(ResourceLocation id, @Nullable ResourceLocation advancementId) {
      super(id, advancementId);
    }


    @Override
    public void serializeRecipeData(JsonObject json) {
      json.addProperty("data_key", dataKey.toString());
      super.serializeRecipeData(json);
      json.addProperty("blacklist", blacklist.location().toString());
      json.addProperty("add_to_set", addToSet);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.modifierSetWorktableSerializer.get();
    }
  }
}
