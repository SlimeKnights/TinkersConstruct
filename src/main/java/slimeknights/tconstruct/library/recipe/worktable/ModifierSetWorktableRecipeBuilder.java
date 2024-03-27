package slimeknights.tconstruct.library.recipe.worktable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.function.Consumer;

/** Builder for recipes to add or remove a modifier from a set in persistent data */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifierSetWorktableRecipeBuilder extends AbstractSizedIngredientRecipeBuilder<ModifierSetWorktableRecipeBuilder> {
  private final ResourceLocation dataKey;
  @Setter @Accessors(fluent = true)
  private IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.ANY;
  private final boolean addToSet;
  private Ingredient tools = AbstractWorktableRecipe.DEFAULT_TOOLS;
  private boolean allowTraits = false;

  /** Creates a new recipe for adding to a set */
  public static ModifierSetWorktableRecipeBuilder setAdding(ResourceLocation dataKey) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, true);
  }

  /** Creates a new recipe for removing from a set */
  public static ModifierSetWorktableRecipeBuilder setRemoving(ResourceLocation dataKey) {
    return new ModifierSetWorktableRecipeBuilder(dataKey, false);
  }

  /** Sets the tool requirement for this recipe */
  public ModifierSetWorktableRecipeBuilder setTools(Ingredient ingredient) {
    this.tools = ingredient;
    return this;
  }

  /** Sets the tool requirement for this recipe */
  public ModifierSetWorktableRecipeBuilder setTools(TagKey<Item> tag) {
    return this.setTools(Ingredient.of(tag));
  }

  /** Sets the recipe to allow traits */
  public ModifierSetWorktableRecipeBuilder allowTraits() {
    allowTraits = true;
    return this;
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
    if (tools == Ingredient.EMPTY) {
      throw new IllegalStateException("Tools cannot be empty");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierSetWorktableRecipe(id, dataKey, inputs, tools, modifierPredicate, addToSet, allowTraits), ModifierSetWorktableRecipe.LOADER, advancementId));
  }
}
