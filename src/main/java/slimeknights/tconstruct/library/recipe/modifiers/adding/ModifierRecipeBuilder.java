package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.RandomItem;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModifierRecipeBuilder extends AbstractModifierRecipeBuilder<ModifierRecipeBuilder> {
  private final List<SizedIngredient> inputs = new ArrayList<>();
  private final List<RandomItem> salvage = new ArrayList<>();
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
   * Adds an input with the given amount, does not affect the salvage builder
   * @param item    Item
   * @param amount  Amount
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(IItemProvider item, int amount) {
    return addInput(SizedIngredient.fromItems(amount, item));
  }

  /**
   * Adds an input with a size of 1, does not affect the salvage builder
   * @param item    Item
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(IItemProvider item) {
    return addInput(item, 1);
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ITag<Item> tag, int amount) {
    return addInput(SizedIngredient.fromTag(tag, amount));
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ITag<Item> tag) {
    return addInput(tag, 1);
  }

  /* Salvage */

  /**
   * Adds a salvage item to the builder
   * @param item  Salvage item
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addSalvage(RandomItem item) {
    salvage.add(item);
    return this;
  }

  /**
   * Adds a salvage item to the builder with a chance to salvage
   * @param item    Salvage item
   * @param chance  Salvage chance
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addSalvage(IItemProvider item, float chance) {
    return addSalvage(RandomItem.chance(ItemOutput.fromItem(item), chance));
  }

  /**
   * Adds a salvage item to the builder
   * @param item       Salvage item
   * @param minAmount  Min amount to salvage
   * @param maxAmount  Max amount to salvage
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addSalvage(IItemProvider item, int minAmount, int maxAmount) {
    return addSalvage(RandomItem.range(ItemOutput.fromStack(new ItemStack(item, maxAmount)), minAmount));
  }

  /**
   * Adds a salvage item to the builder with a min amount of 0
   * @param item       Salvage item
   * @param maxAmount  Max amount to salvage
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addSalvage(IItemProvider item, int maxAmount) {
    return addSalvage(item, 0, maxAmount);
  }


  /**
   * Adds a salvage item to the builder
   * @param tag  Salvage item
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addSalvage(ITag<Item> tag, int minAmount, int maxAmount) {
    return addSalvage(RandomItem.range(ItemOutput.fromTag(tag, maxAmount), minAmount));
  }


  /* Both */

  /**
   * Adds an input to the recipe and a salvage
   * @param item       Item input
   * @param minAmount  Minimum salvage amount
   * @param maxAmount  Maximum salvage amount and recipe cost
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInputSalvage(IItemProvider item, int minAmount, int maxAmount) {
    addInput(item, maxAmount);
    addSalvage(item, minAmount, maxAmount);
    return this;
  }

  /**
   * Adds an input to the recipe, and a salvage with a chance from 0 to amount
   * @param item    Item input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInputSalvage(IItemProvider item, int amount) {
    return addInputSalvage(item, 0, amount);
  }

  /**
   * Adds an input to the recipe with a chance of salvage
   * @param item    Item input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInputSalvage(IItemProvider item, float salvageChance) {
    addInput(item);
    addSalvage(item, salvageChance);
    return this;
  }

  /**
   * Adds an input to the recipe
   * @param tag        Tag input
   * @param minAmount  Min amount for salvage
   * @param maxAmount  Max amount for salvage
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInputSalvage(ITag<Item> tag, int minAmount, int maxAmount) {
    addInput(SizedIngredient.fromTag(tag, maxAmount));
    addSalvage(tag, minAmount, maxAmount);
    return this;
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInputSalvage(ITag<Item> tag, int amount) {
    return addInputSalvage(tag, 0, amount);
  }

  /**
   * Adds an input to the recipe
   * @param tag            Tag input
   * @param salvageChance  Chance of the input to be salvaged
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInputSalvage(ITag<Item> tag, float salvageChance) {
    addInput(SizedIngredient.fromTag(tag, 1));
    addSalvage(RandomItem.chance(ItemOutput.fromTag(tag, 1), salvageChance));
    return this;
  }


  /* Building */

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 input");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedRecipe(id, advancementId, false));
    if (includeUnarmed) {
      if (requirements != ModifierMatch.ALWAYS) {
        throw new IllegalStateException("Cannot use includeUnarmed with requirements");
      }
      consumer.accept(new FinishedRecipe(new ResourceLocation(id.getNamespace(), id.getPath() + "_unarmed"), null, true));
    }
  }

  @Override
  public ModifierRecipeBuilder buildSalvage(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (salvageMaxLevel != 0 && salvageMaxLevel < salvageMinLevel) {
      throw new IllegalStateException("Max level must be greater than min level");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedSalvage(id, advancementId));
    return this;
  }

  private class FinishedRecipe extends ModifierFinishedRecipe {
    public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID, boolean withUnarmed) {
      super(ID, advancementID, withUnarmed);
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
    public IRecipeSerializer<?> getSerializer() {
      return TinkerModifiers.modifierSerializer.get();
    }
  }

  private class FinishedSalvage extends SalvageFinishedRecipe {
    public FinishedSalvage(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      super.serialize(json);
      if (!salvage.isEmpty()) {
        JsonArray array = new JsonArray();
        for (RandomItem randomItem : salvage) {
          array.add(randomItem.serialize());
        }
        json.add("salvage", array);
      }
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerModifiers.modifierSalvageSerializer.get();
    }
  }
}
