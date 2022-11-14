package slimeknights.tconstruct.library.recipe.worktable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Builder for a recipe that uses sized ingredients */
public abstract class AbstractSizedIngredientRecipeBuilder<T extends AbstractSizedIngredientRecipeBuilder<T>> extends AbstractRecipeBuilder<T> {
  protected final List<SizedIngredient> inputs = new ArrayList<>();

  /**
   * Adds an input to the recipe
   * @param ingredient  Input
   * @return  Builder instance
   */
  @SuppressWarnings("unchecked")
  public T addInput(SizedIngredient ingredient) {
    this.inputs.add(ingredient);
    return (T)this;
  }

  /**
   * Adds an input to the recipe
   * @param ingredient  Input
   * @return  Builder instance
   */
  public T addInput(Ingredient ingredient) {
    return addInput(SizedIngredient.of(ingredient));
  }

  /**
   * Adds an input with the given amount, does not affect the salvage builder
   * @param item    Item
   * @param amount  Amount
   * @return  Builder instance
   */
  public T addInput(ItemLike item, int amount) {
    return addInput(SizedIngredient.fromItems(amount, item));
  }

  /**
   * Adds an input with a size of 1, does not affect the salvage builder
   * @param item    Item
   * @return  Builder instance
   */
  public T addInput(ItemLike item) {
    return addInput(item, 1);
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public T addInput(TagKey<Item> tag, int amount) {
    return addInput(SizedIngredient.fromTag(tag, amount));
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @return  Builder instance
   */
  public T addInput(TagKey<Item> tag) {
    return addInput(tag, 1);
  }

  /** Serializes the ingredients by default */
  protected abstract class SizedFinishedRecipe extends AbstractFinishedRecipe {
    public SizedFinishedRecipe(ResourceLocation id, @Nullable ResourceLocation advancementId) {
      super(id, advancementId);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      JsonArray array = new JsonArray();
      for (SizedIngredient ingredient : inputs) {
        array.add(ingredient.serialize());
      }
      json.add("inputs", array);
    }
  }
}
