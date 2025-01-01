package slimeknights.tconstruct.tools.recipe;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.recipe.worktable.AbstractSizedIngredientRecipeBuilder;

import java.util.function.Consumer;

/** Builder for modifier sorting recipes */
@RequiredArgsConstructor(staticName = "sorting")
public class ModifierSortingRecipeBuilder extends AbstractSizedIngredientRecipeBuilder<ModifierSortingRecipeBuilder> {

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, BuiltInRegistries.ITEM.getKey((inputs.get(0).getMatchingStacks().get(0).getItem())));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least one ingredient");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierSortingRecipe(id, inputs), ModifierSortingRecipe.LOADER, advancementId));
  }
}
