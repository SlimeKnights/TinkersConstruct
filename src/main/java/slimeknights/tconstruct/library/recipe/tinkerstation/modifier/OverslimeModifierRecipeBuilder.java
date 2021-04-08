package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.tools.TinkerModifiers;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for overslime recipes
 */
@RequiredArgsConstructor(staticName = "modifier")
public class OverslimeModifierRecipeBuilder extends AbstractRecipeBuilder<OverslimeModifierRecipeBuilder> {
  private final Ingredient ingredient;
  private final int restoreAmount;

  /** Creates a new builder for the given item */
  public static OverslimeModifierRecipeBuilder modifier(ItemConvertible item, int restoreAmount) {
    return modifier(Ingredient.ofItems(item), restoreAmount);
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer) {
    ItemStack[] stacks = ingredient.getMatchingStacksClient();
    if (stacks.length == 0) {
      throw new IllegalStateException("Empty ingredient not allowed");
    }
    build(consumer, Objects.requireNonNull(stacks[0].getItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    if (ingredient == Ingredient.EMPTY) {
      throw new IllegalStateException("Empty ingredient not allowed");
    }
    Identifier advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  private class FinishedRecipe extends AbstractFinishedRecipe {
    public FinishedRecipe(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("ingredient", ingredient.toJson());
      json.addProperty("restore_amount", restoreAmount);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerModifiers.overslimeSerializer.get();
    }
  }
}
