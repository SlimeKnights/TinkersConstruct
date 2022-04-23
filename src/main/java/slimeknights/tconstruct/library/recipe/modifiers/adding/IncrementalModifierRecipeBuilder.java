package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/** Recipe that supports not just adding multiple of an item, but also adding a partial amount */
@SuppressWarnings({"unused", "UnusedReturnValue"})
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
  public static IncrementalModifierRecipeBuilder modifier(ModifierId modifier) {
    return modifier(new ModifierEntry(modifier, 1));
  }

  /**
   * Creates a new recipe for 1 level of a modifier
   * @param modifier  Modifier
   * @return  Recipe for 1 level of the modifier
   */
  public static IncrementalModifierRecipeBuilder modifier(LazyModifier modifier) {
    return modifier(modifier.getId());
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
  public IncrementalModifierRecipeBuilder setInput(ItemLike item, int amountPerItem, int neededPerLevel) {
    return setInput(Ingredient.of(item), amountPerItem, neededPerLevel);
  }

  /**
   * Adds an input to the recipe
   * @param tag            Tag input
   * @param amountPerItem  Amount each item matches
   * @param neededPerLevel Total number needed for this modifier
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setInput(TagKey<Item> tag, int amountPerItem, int neededPerLevel) {
    return setInput(Ingredient.of(tag), amountPerItem, neededPerLevel);
  }


  /* Building */

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (input == Ingredient.EMPTY) {
      throw new IllegalStateException("Must set input");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedAdding(id, advancementId, false));
    if (includeUnarmed) {
      if (requirements != ModifierMatch.ALWAYS) {
        throw new IllegalStateException("Cannot use includeUnarmed with requirements");
      }
      consumer.accept(new FinishedAdding(new ResourceLocation(id.getNamespace(), id.getPath() + "_unarmed"), null, true));
    }
  }

  @Override
  public IncrementalModifierRecipeBuilder saveSalvage(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (salvageMaxLevel != 0 && salvageMaxLevel < salvageMinLevel) {
      throw new IllegalStateException("Max level must be greater than min level");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new SalvageFinishedRecipe(id, advancementId));
    return this;
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

  private class FinishedAdding extends ModifierFinishedRecipe {
    public FinishedAdding(ResourceLocation ID, @Nullable ResourceLocation advancementID, boolean withUnarmed) {
      super(ID, advancementID, withUnarmed);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("input", input.toJson());
      json.addProperty("amount_per_item", amountPerItem);
      json.addProperty("needed_per_level", neededPerLevel);
      if (leftover != ItemStack.EMPTY) {
        json.add("leftover", serializeResult(leftover));
      }
      super.serializeRecipeData(json);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.incrementalModifierSerializer.get();
    }
  }
}
