package slimeknights.tconstruct.library.recipe.worktable;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.TagModifierPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for recipes to add or remove a modifier from a set in persistent data */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifierSetWorktableRecipeBuilder extends AbstractSizedIngredientRecipeBuilder<ModifierSetWorktableRecipeBuilder> {
  private final ResourceLocation dataKey;
  @Setter @Accessors(fluent = true)
  private IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.ALWAYS;
  private final boolean addToSet;

  /** @deprecated use {@link #setAdding(ResourceLocation)} and {@link #modifierPredicate(IJsonPredicate)} */
  @Deprecated
  public static ModifierSetWorktableRecipeBuilder setAdding(ResourceLocation dataKey, TagKey<Modifier> blacklist) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, new TagModifierPredicate(blacklist).inverted(), true);
  }

  /** @deprecated use {@link #setRemoving(ResourceLocation)} and {@link #modifierPredicate(IJsonPredicate)} */
  @Deprecated
  public static ModifierSetWorktableRecipeBuilder setRemoving(ResourceLocation dataKey, TagKey<Modifier> blacklist) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, new TagModifierPredicate(blacklist).inverted(), false);
  }

  /** Creates a new recipe for adding to a set */
  public static ModifierSetWorktableRecipeBuilder setAdding(ResourceLocation dataKey) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, true);
  }

  /** Creates a new recipe for removing from a set */
  public static ModifierSetWorktableRecipeBuilder setRemoving(ResourceLocation dataKey) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, false);
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
      json.add("modifier_predicate", ModifierPredicate.LOADER.serialize(modifierPredicate));
      json.addProperty("add_to_set", addToSet);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.modifierSetWorktableSerializer.get();
    }
  }
}
