package slimeknights.tconstruct.library.recipe.modifiers.severing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for entity melting recipes */
@RequiredArgsConstructor(staticName = "severing")
public class SeveringRecipeBuilder extends AbstractRecipeBuilder<SeveringRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final ItemOutput output;
  private boolean isAgeable = false;
  private ItemOutput childOutput = ItemOutput.EMPTY;

  /** Creates a new builder from an item */
  public static SeveringRecipeBuilder severing(EntityIngredient ingredient, ItemLike output) {
    return SeveringRecipeBuilder.severing(ingredient, ItemOutput.fromItem(output));
  }

  /**
   * Makes this an ageable severing recipe
   * @param childOutput  Output when a child, if empty just does no output for children
   * @return  Builder instance
   */
  public SeveringRecipeBuilder setChildOutput(ItemOutput childOutput) {
    this.isAgeable = true;
    this.childOutput = childOutput;
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
    save(consumer, Registry.ITEM.getKey(output.get().getItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "severing");
    consumer.accept(new Finished(id, advancementId));
  }

  private class Finished extends AbstractFinishedRecipe {
    public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("entity", ingredient.serialize());
      if (isAgeable) {
        json.add("adult_result", output.serialize(true));
        if (childOutput != ItemOutput.EMPTY) {
          json.add("child_result", childOutput.serialize(true));
        }
      } else {
        json.add("result", output.serialize(true));
      }
    }

    @Override
    public RecipeSerializer<?> getType() {
      return isAgeable ? TinkerModifiers.ageableSeveringSerializer.get() : TinkerModifiers.severingSerializer.get();
    }
  }
}
