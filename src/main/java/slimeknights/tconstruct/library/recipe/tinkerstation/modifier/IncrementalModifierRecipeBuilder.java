package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/** Recipe that supports not just adding multiple of an item, but also adding a partial amount */
@Accessors(chain = true)
public class IncrementalModifierRecipeBuilder extends AbstractModifierRecipeBuilder<IncrementalModifierRecipeBuilder> {
  private Ingredient input = Ingredient.EMPTY;
  private int amountPerItem;
  private int neededPerLevel;
  @Setter
  private ItemStack leftover = ItemStack.EMPTY;
  protected IncrementalModifierRecipeBuilder(ModifierEntry result) {
    super(result);
  }

  /**
   * Creates a new recipe for multiple levels of a modifier
   * @param modifier  Modifier
   * @return  Recipe for multiple levels of the modifier
   */
  public static IncrementalModifierRecipeBuilder modifier(ModifierEntry modifier) {
    return new IncrementalModifierRecipeBuilder(modifier);
  }

  /**
   * Creates a new recipe for 1 level of a modifier
   * @param modifier  Modifier
   * @return  Recipe for 1 level of the modifier
   */
  public static IncrementalModifierRecipeBuilder modifier(Modifier modifier) {
    return modifier(new ModifierEntry(modifier, 1));
  }

  /* Inputs */

  /**
   * Adds an input to the recipe
   * @param input          Input
   * @param amountPerItem  Amount each item matches
   * @param neededPerLevel Total number needed for this modifier
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setInput(Ingredient input, int amountPerItem, int neededPerLevel) {
    if (amountPerItem < 1) {
      throw new IllegalArgumentException("Amount per item must be at least 1");
    }
    if (neededPerLevel <= amountPerItem) {
      throw new IllegalArgumentException("Needed per level must be greater than amount per item");
    }
    this.input = input;
    this.amountPerItem = amountPerItem;
    this.neededPerLevel = neededPerLevel;
    return this;
  }

  /**
   * Adds an input to the recipe
   * @param item           Item input
   * @param amountPerItem  Amount each item matches
   * @param neededPerLevel Total number needed for this modifier
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setInput(ItemConvertible item, int amountPerItem, int neededPerLevel) {
    return setInput(Ingredient.ofItems(item), amountPerItem, neededPerLevel);
  }

  /**
   * Adds an input to the recipe
   * @param tag            Tag input
   * @param amountPerItem  Amount each item matches
   * @param neededPerLevel Total number needed for this modifier
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setInput(Tag<Item> tag, int amountPerItem, int neededPerLevel) {
    return setInput(Ingredient.fromTag(tag), amountPerItem, neededPerLevel);
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    if (input == Ingredient.EMPTY) {
      throw new IllegalStateException("Must set input");
    }
    Identifier advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  /**
   * Serializes the given result to JSON
   * @param result  Result
   * @return  JSON element
   */
  public static JsonElement serializeResult(ItemStack result) {
    // if the item has NBT, write both, else write just the name
    String itemName = Objects.requireNonNull(result.getItem().getRegistryName()).toString();
    if (result.hasTag()) {
      JsonObject jsonResult = new JsonObject();
      jsonResult.addProperty("item", itemName);
      int count = result.getCount();
      if (count > 1) {
        jsonResult.addProperty("count", count);
      }
      jsonResult.addProperty("nbt", Objects.requireNonNull(result.getTag()).toString());
      return jsonResult;
    } else {
      return new JsonPrimitive(itemName);
    }
  }

  private class FinishedRecipe extends ModifierFinishedRecipe {
    public FinishedRecipe(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("input", input.toJson());
      json.addProperty("amount_per_item", amountPerItem);
      json.addProperty("needed_per_level", neededPerLevel);
      if (leftover != ItemStack.EMPTY) {
        json.add("leftover", serializeResult(leftover));
      }
      super.serialize(json);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerModifiers.incrementalModifierSerializer.get();
    }
  }
}
