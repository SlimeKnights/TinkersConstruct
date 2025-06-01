package slimeknights.tconstruct.library.recipe.modifiers.adding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/** Recipe instance to return in JEI from recipes that contain multiple display recipes */
@AllArgsConstructor
public class DisplayModifierRecipe implements IDisplayModifierRecipe {
  private final List<SizedIngredient> inputs;
  @Getter
  private final List<ItemStack> toolWithoutModifier;
  @Getter
  private final List<ItemStack> toolWithModifier;
  /** Error message to display if the requirements do not match */
  @Getter
  private final ModifierEntry displayResult;
  @Getter
  private final IntRange level;
  @Nullable
  @Getter
  private final SlotCount slots;
  @Getter
  private final List<SlotCount> resultSlots;

  /** @deprecated use {@link #DisplayModifierRecipe(List, List, List, ModifierEntry, IntRange, SlotCount, List)} */
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
      return inputs.get(slot).getMatchingStacks();
    }
    return Collections.emptyList();
  }
}
