package slimeknights.tconstruct.library.recipe.modifiers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/** Builder for entity melting recipes */
@RequiredArgsConstructor(staticName = "severing")
public class SeveringRecipeBuilder extends AbstractRecipeBuilder<SeveringRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final ItemOutput output;
  private boolean isAgeable = false;
  private ItemOutput childOutput = null;

  /** Creates a new builder from an item */
  public static SeveringRecipeBuilder severing(EntityIngredient ingredient, IItemProvider output) {
    return SeveringRecipeBuilder.severing(ingredient, ItemOutput.fromItem(output));
  }

  /**
   * Makes this an ageable severing recipe
   * @param childOutput  Output when a child, if null just does no output for children
   * @return  Builder instance
   */
  public SeveringRecipeBuilder setChildOutput(@Nullable ItemOutput childOutput) {
    this.isAgeable = true;
    this.childOutput = childOutput;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.get().getItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "severing");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  private class FinishedRecipe extends AbstractFinishedRecipe {
    public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("entity", ingredient.serialize());
      if (isAgeable) {
        json.add("adult_result", output.serialize());
        if (childOutput != null) {
          json.add("child_result", childOutput.serialize());
        }
      } else {
        json.add("result", output.serialize());
      }
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return isAgeable ? TinkerModifiers.ageableSeveringSerializer.get() : TinkerModifiers.severingSerializer.get();
    }
  }
}
