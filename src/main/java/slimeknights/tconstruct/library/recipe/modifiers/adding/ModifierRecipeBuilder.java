package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ModifierRecipeBuilder extends AbstractModifierRecipeBuilder<ModifierRecipeBuilder> {
  protected final List<SizedIngredient> inputs = new ArrayList<>();
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
  public static ModifierRecipeBuilder modifier(ModifierId modifier) {
    return modifier(new ModifierEntry(modifier, 1));
  }

  /**
   * Creates a new recipe for 1 level of a modifier
   * @param modifier  Modifier
   * @return  Recipe for 1 level of the modifier
   */
  public static ModifierRecipeBuilder modifier(LazyModifier modifier) {
    return modifier(modifier.getId());
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
   * @param ingredient  Input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(Ingredient ingredient) {
    return addInput(SizedIngredient.of(ingredient));
  }

  /**
   * Adds an input with the given amount, does not affect the salvage builder
   * @param item    Item
   * @param amount  Amount
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ItemLike item, int amount) {
    return addInput(SizedIngredient.fromItems(amount, item));
  }

  /**
   * Adds an input with a size of 1, does not affect the salvage builder
   * @param item    Item
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ItemLike item) {
    return addInput(item, 1);
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(TagKey<Item> tag, int amount) {
    return addInput(SizedIngredient.fromTag(tag, amount));
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(TagKey<Item> tag) {
    return addInput(tag, 1);
  }


  /* Building */

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 input");
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
  public ModifierRecipeBuilder saveSalvage(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (salvageMaxLevel != 0 && salvageMaxLevel < salvageMinLevel) {
      throw new IllegalStateException("Max level must be greater than min level");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new SalvageFinishedRecipe(id, advancementId));
    return this;
  }

  protected class FinishedAdding extends ModifierFinishedRecipe {
    public FinishedAdding(ResourceLocation ID, @Nullable ResourceLocation advancementID, boolean withUnarmed) {
      super(ID, advancementID, withUnarmed);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      JsonArray array = new JsonArray();
      for (SizedIngredient ingredient : inputs) {
        array.add(ingredient.serialize());
      }
      json.add("inputs", array);
      super.serializeRecipeData(json);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.modifierSerializer.get();
    }
  }
}
