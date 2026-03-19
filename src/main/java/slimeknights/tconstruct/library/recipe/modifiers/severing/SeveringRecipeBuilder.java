package slimeknights.tconstruct.library.recipe.modifiers.severing;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for entity melting recipes */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "severing")
public class SeveringRecipeBuilder extends AbstractRecipeBuilder<SeveringRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final ItemOutput output;
  private float baseChance = 0.05f;
  private float lootingBonus = 0.01f;
  @Nullable
  private ItemOutput childOutput = null;

  /** Creates a new builder from an item */
  public static SeveringRecipeBuilder severing(EntityIngredient ingredient, ItemLike output) {
    return SeveringRecipeBuilder.severing(ingredient, ItemOutput.fromItem(output));
  }

  /** Doubles the drop chances for this rare mob */
  public SeveringRecipeBuilder rareMob() {
    baseChance = 0.1f;
    lootingBonus = 0.02f;
    return this;
  }

  /**
   * Makes this an ageable severing recipe with no child output
   * @return  Builder instance
   */
  public SeveringRecipeBuilder noChildOutput() {
    return setChildOutput(ItemOutput.EMPTY);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, BuiltInRegistries.ITEM.getKey(output.get().getItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "severing");
    if (childOutput != null) {
      consumer.accept(new LoadableFinishedRecipe<>(new AgeableSeveringRecipe(id, ingredient, output, childOutput, baseChance, lootingBonus), AgeableSeveringRecipe.LOADER, advancementId));
    } else {
      consumer.accept(new LoadableFinishedRecipe<>(new SeveringRecipe(id, ingredient, output, baseChance, lootingBonus), SeveringRecipe.LOADER, advancementId));
    }
  }
}
