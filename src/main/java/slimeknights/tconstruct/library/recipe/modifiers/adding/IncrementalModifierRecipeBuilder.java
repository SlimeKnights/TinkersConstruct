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
import slimeknights.mantle.recipe.helper.ItemOutput;
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
  private ItemOutput salvage = null;
  private int amountPerItem;
  private int neededPerLevel;
  @Setter
  private ItemStack leftover = ItemStack.EMPTY;
  private boolean fullSalvage = false;

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


  /* Salvage */

  /**
   * Sets the salvage to the given ItemOutput
   * @param salvage     Salvage object
   * @param fullOutput  If true, salvaging returns the full item amount. If false, salvaging grants between 0 and the full amount
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setSalvage(ItemOutput salvage, boolean fullOutput) {
    this.salvage = salvage;
    this.fullSalvage = fullOutput;
    return this;
  }

  /**
   * Sets the salvage to the given item
   * @param item           Item
   * @param amountPerItem  Amount of item per level
   * @param fullOutput  If true, salvaging returns the full item amount. If false, salvaging grants between 0 and the full amount
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setSalvage(ItemLike item, int amountPerItem, boolean fullOutput) {
    return setSalvage(ItemOutput.fromStack(new ItemStack(item, amountPerItem)), fullOutput);
  }

  /**
   * Sets the salvage to the given item
   * @param item  Item
   * @param fullOutput  If true, salvaging returns the full item amount. If false, salvaging grants between 0 and the full amount
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setSalvage(ItemLike item, boolean fullOutput) {
    return setSalvage(item, 1, fullOutput);
  }

  /**
   * Adds an input to the recipe and sets the salvage item
   * @param item           Item input
   * @param amountPerItem  Amount each item matches and size of salvage
   * @param neededPerLevel Total number needed for this modifier
   * @param fullOutput  If true, salvaging returns the full item amount. If false, salvaging grants between 0 and the full amount
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setInputSalvage(ItemLike item, int amountPerItem, int neededPerLevel, boolean fullOutput) {
    setInput(item, amountPerItem, neededPerLevel);
    setSalvage(item, amountPerItem, fullOutput);
    return this;
  }

  /**
   * Adds an input to the recipe and sets the salvage item
   * @param tag            Tag input
   * @param amountPerItem  Amount each item matches and size of salvage
   * @param neededPerLevel Total number needed for this modifier
   * @param fullOutput  If true, salvaging returns the full item amount. If false, salvaging grants between 0 and the full amount
   * @return  Builder instance
   */
  public IncrementalModifierRecipeBuilder setInputSalvage(TagKey<Item> tag, int amountPerItem, int neededPerLevel, boolean fullOutput) {
    setInput(tag, amountPerItem, neededPerLevel);
    setSalvage(ItemOutput.fromTag(tag, amountPerItem), fullOutput);
    return this;
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
    consumer.accept(new FinishedSalvage(id, advancementId));
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

  private class FinishedSalvage extends SalvageFinishedRecipe {
    public FinishedSalvage(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      super.serializeRecipeData(json);
      if (salvage != null) {
        JsonElement salvageElement = salvage.serialize();
        JsonObject salvageObject;
        if (salvageElement.isJsonObject()) {
          salvageObject = salvageElement.getAsJsonObject();
        } else {
          salvageObject = new JsonObject();
          salvageObject.add("item", salvageElement);
          salvageObject.addProperty("full", fullSalvage);
        }
        json.add("salvage", salvageObject);
      }
    }

    @Override
    public RecipeSerializer<?> getType() {
      // incremental serializer does not support no salvage, but regular one does
      return salvage == null ? TinkerModifiers.modifierSalvageSerializer.get() : TinkerModifiers.incrementalModifierSalvageSerializer.get();
    }
  }
}
