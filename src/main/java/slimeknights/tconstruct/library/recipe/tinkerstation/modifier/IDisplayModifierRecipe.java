package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/** Common interface for modifier recipes that can show in JEI */
public interface IDisplayModifierRecipe {
  /**
   * Gets a list of ingredients to display in JEI. First entry is the tool with the modifier, second is the tool without the modifier, then next 1-5 are items to add the modifier
   * @return  Display item list
   */
  List<List<ItemStack>> getDisplayItems();

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

  /** If true, this recipe can be applied incrementally */
  default boolean isIncremental() {
    return false;
  }


  /* Helpers */

  /** Gets a stream of all modifiable items for display */
  static Stream<Item> getAllModifiable() {
    return TinkerTags.Items.MODIFIABLE.getAllElements().stream();
  }

  /** Maps the stream from tool items to applicable tool stacks */
  Function<Item,ItemStack> MAP_TOOL_FOR_RENDERING = item -> {
    if (item instanceof ToolCore) {
      return ((ToolCore)item).buildToolForRendering();
    }
    return new ItemStack(item);
  };

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
