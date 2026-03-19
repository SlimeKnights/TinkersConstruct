package slimeknights.tconstruct.library.recipe.modifiers.adding;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/** Recipe instance to return in JEI from recipes that contain multiple display recipes */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayModifierRecipe implements IDisplayModifierRecipe {
  @Getter
  @Nullable
  private final ResourceLocation recipeId;
  private final List<List<ItemStack>> inputs;
  @Getter
  private final List<ItemStack> toolWithoutModifier;
  @Getter
  private final List<ItemStack> toolWithModifier;
  @Getter
  private final ModifierEntry displayResult;
  @Getter
  private final IntRange level;
  @Nullable
  @Getter
  private final SlotCount slots;
  @Getter
  private final List<SlotCount> resultSlots;
  @Getter
  private final boolean incremental;

  /** @deprecated use {@link #builder()} */
  @Deprecated(forRemoval = true)
  public DisplayModifierRecipe(@Nullable ResourceLocation id, List<SizedIngredient> inputs, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, ModifierEntry displayResult, IntRange level, @Nullable SlotCount slots, List<SlotCount> resultSlots) {
    this(id, resolve(inputs), toolWithoutModifier, toolWithModifier, displayResult, level, slots, resultSlots, false);
  }

  /** @deprecated use {@link #builder()} */
  @Deprecated(forRemoval = true)
  public DisplayModifierRecipe(List<SizedIngredient> inputs, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, ModifierEntry displayResult, IntRange level, @Nullable SlotCount slots, List<SlotCount> resultSlots) {
    this(null, inputs, toolWithoutModifier, toolWithModifier, displayResult, level, slots, resultSlots);
  }

  /** @deprecated use {@link #builder()} */
  @Deprecated(forRemoval = true)
  public DisplayModifierRecipe(List<SizedIngredient> inputs, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, ModifierEntry displayResult, IntRange level, @Nullable SlotCount slots) {
    this(inputs, toolWithoutModifier, toolWithModifier, displayResult, level, slots, List.of());
  }

  @Override
  public int getInputCount() {
    return inputs.size();
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    if (slot >= 0 && slot < inputs.size()) {
      return inputs.get(slot);
    }
    return Collections.emptyList();
  }

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }


  /** Resolves a list of sized ingredients into a list of item stack lists */
  private static List<List<ItemStack>> resolve(List<SizedIngredient> ingredients) {
    return ingredients.stream().map(SizedIngredient::getMatchingStacks).toList();
  }

  /** Builder for creating a display recipe */
  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private ModifierEntry result = ModifierEntry.EMPTY;
    @Nullable
    private ResourceLocation id = null;
    private List<List<ItemStack>> inputs = List.of();
    private List<ItemStack> toolWithoutModifier = List.of();
    private List<ItemStack> toolWithModifier = List.of();
    private IntRange level = ModifierEntry.VALID_LEVEL;
    @Nullable
    private SlotCount slots = null;
    private List<SlotCount> resultSlots = List.of();
    private boolean incremental = false;

    /** Creates a copy of this builder with the same properties */
    public Builder copy() {
      Builder copy = new Builder();
      copy.id = this.id;
      copy.inputs = this.inputs;
      copy.toolWithoutModifier = this.toolWithoutModifier;
      copy.toolWithModifier = this.toolWithModifier;
      copy.level = this.level;
      copy.slots = this.slots;
      copy.result = this.result;
      copy.resultSlots = this.resultSlots;
      copy.incremental = this.incremental;
      return copy;
    }

    /** Sets the inputs to a list of ingredients */
    public Builder ingredients(List<SizedIngredient> inputs) {
      return inputs(resolve(inputs));
    }

    /** Sets this display recipe to be incremental */
    public Builder incremental() {
      return incremental(true);
    }

    /** Builds the final recipe */
    public DisplayModifierRecipe build() {
      if (result == ModifierEntry.EMPTY) {
        throw new IllegalStateException("Must set result");
      }
      if (inputs.isEmpty()) {
        throw new IllegalStateException("Must set inputs");
      }
      if (toolWithoutModifier.isEmpty()) {
        throw new IllegalStateException("Must set tools without modifier");
      }
      if (toolWithModifier.isEmpty()) {
        throw new IllegalStateException("Must set tools with modifier");
      }
      return new DisplayModifierRecipe(id, inputs, toolWithoutModifier, toolWithModifier, result, level, slots, resultSlots, incremental);
    }
  }
}
