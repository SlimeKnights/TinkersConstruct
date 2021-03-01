package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;

/** Common interface for modifier recipes that can show in JEI */
public interface IDisplayModifierRecipe {
  /**
   * Gets a list of ingredients to display in JEI. First entry is the tool, then next 1-5 are modifier inputs
   * @return  Display inputs list
   */
  List<List<ItemStack>> getDisplayInputs();

  /** Gets the tools to display */
  List<List<ItemStack>> getDisplayOutput();

  /** Gets the modifier output of this recipe */
  ModifierEntry getDisplayResult();

  /**
   * Gets the max level of this modifier
   * @return modifier max level, 0 if no max level
   */
  default int getMaxLevel() {
    return 0;
  }

  /** Gets the number of upgrade slots required for this modifier */
  default int getUpgradeSlots() {
    return 0;
  }

  /** Gets the number of ability slots required for this modifier */
  default int getAbilitySlots() {
    return 0;
  }

  /** If true, this recipe has additional requirements */
  default boolean hasRequirements() {
    return false;
  }

  /** Gets the message to display when requirements do not match, or empty if no requirements */
  default String getRequirementsError() {
    return "";
  }


  /* Helpers */

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, @Nullable ModifierMatch match, @Nullable ModifierEntry newModifier) {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    if (match != null) {
      match.apply(builder);
    }
    if (newModifier != null) {
      builder.add(newModifier);
    }
    ItemStack output = stack.copy();
    output.getOrCreateTag().put(ToolStack.TAG_MODIFIERS, builder.build().serializeToNBT());
    return output;
  }
}
