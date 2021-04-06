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
@RequiredArgsConstructor(staticName = "beheading")
public class BeheadingRecipeBuilder extends AbstractRecipeBuilder<BeheadingRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final ItemOutput output;

  /** Creates a new builder from an item */
  public static BeheadingRecipeBuilder beheading(EntityIngredient ingredient, IItemProvider output) {
    return beheading(ingredient, ItemOutput.fromItem(output));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.get().getItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "beheading");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  private class FinishedRecipe extends AbstractFinishedRecipe {
    public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("entity", ingredient.serialize());
      json.add("result", output.serialize());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerModifiers.beheadingSerializer.get();
    }
  }
}
