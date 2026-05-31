package slimeknights.tconstruct.library.recipe.modifiers.adding;

import slimeknights.mantle.compat.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;

import java.util.function.Consumer;

/** Recipe that supports not just adding multiple of an item, but also adding a partial amount */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class IncrementalModifierRecipeBuilder extends AbstractModifierRecipeBuilder<IncrementalModifierRecipeBuilder> {
  private Ingredient input = Ingredient.EMPTY;
  private int amountPerItem;
  private int neededPerLevel;
  private ItemOutput leftover = ItemOutput.EMPTY;

  protected IncrementalModifierRecipeBuilder(ModifierId result) {
    super(result);
  }

  /**
   * Creates a new recipe for 1 level of a modifier
   * @param modifier  Modifier
   * @return  Recipe for 1 level of the modifier
   */
  public static IncrementalModifierRecipeBuilder modifier(ModifierId modifier) {
    return new IncrementalModifierRecipeBuilder(modifier);
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


  /* Leftover */

  /** Sets the leftover to the given output */
  public IncrementalModifierRecipeBuilder setLeftover(ItemOutput leftover) {
    this.leftover = leftover;
    return this;
  }

  /** Sets the leftover to the given stack */
  public IncrementalModifierRecipeBuilder setLeftover(ItemStack stack) {
    return setLeftover(ItemOutput.fromStack(stack));
  }

  /** Sets the leftover to the given item */
  public IncrementalModifierRecipeBuilder setLeftover(ItemLike item) {
    return setLeftover(ItemOutput.fromItem(item));
  }


  /* Building */

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (input == Ingredient.EMPTY) {
      throw new IllegalStateException("Must set input");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new LoadableFinishedRecipe<>(new IncrementalModifierRecipe(id, input, amountPerItem, neededPerLevel, tools, maxToolSize, result, ModifierEntry.VALID_LEVEL.range(minLevel, maxLevel), slots, leftover, allowCrystal, checkTraitLevel), IncrementalModifierRecipe.LOADER, advancementId));
  }
}
