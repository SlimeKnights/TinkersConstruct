package slimeknights.tconstruct.library.recipe.modifiers;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/** Builder for entity melting recipes */
@RequiredArgsConstructor(staticName = "beheading")
public class BeheadingRecipeBuilder extends AbstractRecipeBuilder<BeheadingRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final ItemOutput output;

  /** Creates a new builder from an item */
  public static BeheadingRecipeBuilder beheading(EntityIngredient ingredient, ItemConvertible output) {
    return beheading(ingredient, ItemOutput.fromItem(output));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer) {
    build(consumer, Objects.requireNonNull(output.get().getItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    Identifier advancementId = this.buildOptionalAdvancement(id, "beheading");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  private class FinishedRecipe extends AbstractFinishedRecipe {
    public FinishedRecipe(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("entity", ingredient.serialize());
      json.add("result", output.serialize());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerModifiers.beheadingSerializer.get();
    }
  }
}
