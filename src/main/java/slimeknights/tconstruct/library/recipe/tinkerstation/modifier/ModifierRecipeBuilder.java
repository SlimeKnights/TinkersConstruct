package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModifierRecipeBuilder extends AbstractModifierRecipeBuilder<ModifierRecipeBuilder> {
  private final List<SizedIngredient> inputs = new ArrayList<>();
  protected ModifierRecipeBuilder(ModifierEntry result) {
    super(result);
  }

  /**
   * Creates a new recipe for multiple levels of a modifier
   * @param modifier  Modifier
   * @return  Recipe for multiple levels of the modifier
   */
  public static ModifierRecipeBuilder modifier(ModifierEntry modifier) {
    return new ModifierRecipeBuilder(modifier);
  }

  /**
   * Creates a new recipe for 1 level of a modifier
   * @param modifier  Modifier
   * @return  Recipe for 1 level of the modifier
   */
  public static ModifierRecipeBuilder modifier(Modifier modifier) {
    return modifier(new ModifierEntry(modifier, 1));
  }


  /* Inputs */

  /**
   * Adds an input to the recipe
   * @param ingredient  Input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(SizedIngredient ingredient) {
    this.inputs.add(ingredient);
    return this;
  }

  /**
   * Adds an input to the recipe
   * @param item    Item input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ItemConvertible item, int amount) {
    return addInput(SizedIngredient.fromItems(amount, item));
  }

  /**
   * Adds an input to the recipe
   * @param item    Item input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ItemConvertible item) {
    return addInput(item, 1);
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(Tag<Item> tag, int amount) {
    return addInput(SizedIngredient.fromTag(tag, amount));
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(Tag<Item> tag) {
    return addInput(tag, 1);
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 input");
    }
    Identifier advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  private class FinishedRecipe extends ModifierFinishedRecipe {
    public FinishedRecipe(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      JsonArray array = new JsonArray();
      for (SizedIngredient ingredient : inputs) {
        array.add(ingredient.serialize());
      }
      json.add("inputs", array);
      super.serialize(json);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerModifiers.modifierSerializer.get();
    }
  }
}
