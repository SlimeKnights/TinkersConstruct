package slimeknights.tconstruct.library.recipe.modifiers.adding;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeSlot;
import slimeknights.tconstruct.library.recipe.RecipeSlots;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

/** Extension of {@link IDisplayModifierRecipe} that dynamically updates the displayed tools in JEI based on the currently focused tool. */
public interface IDynamicModifierRecipe extends IDisplayModifierRecipe {
  @Override
  default boolean isSlotsDynamic() {
    return true;
  }

  /** Checks if we have enough slots */
  default boolean checkSlots(IToolStackView tool) {
    // ensure we have enough slots
    SlotCount count = getSlots();
    if (count != null) {
      return tool.getFreeSlots(count.type()) >= count.count();
    }
    return true;
  }

  /** Gets the matching entry on the tool */
  private ModifierEntry getEntry(IToolStackView tool) {
    return (checkTraitLevel() ? tool.getModifiers() : tool.getUpgrades()).getEntry(getDisplayResult().getId());
  }

  /** Checks if the given tool can receive this modifier. Called in {@link #onDisplayUpdate(RecipeSlot, RecipeSlots, RecipeSlot, ItemStack, boolean)} */
  default boolean canApply(IToolStackView tool) {
    // ensure this recipe applies at the given level range
    ModifierEntry entry = getEntry(tool);
    if (isIncremental()) {
      // if incremental, we care about the effective level, and only check slots if we have a full level
      int effective = entry.intEffectiveLevel();
      return getLevel().test(effective + 1) && (effective != entry.getLevel() || checkSlots(tool));
    }
    // for regular modifiers, want the level in range and the slots valid every application
    return getLevel().test(entry.getLevel() + 1) && checkSlots(tool);
  }

  /**
   * Adds the modifier to the given tool.
   */
  default void applyModifier(ToolStack tool) {
    ModifierId result = getDisplayResult().getId();
    if (isIncremental()) {
      ModifierEntry entry = getEntry(tool);
      // if we have a partial level, just make it a full level
      if (entry.intEffectiveLevel() < entry.getLevel()) {
        int needed = entry.getNeeded();
        tool.addModifierAmount(result, needed - entry.getAmount(0), needed);
        return;
      }
    }
    // apply the modifier
    tool.addModifier(result, 1);
    // spend the slots
    SlotCount slots = getSlots();
    if (slots != null) {
      ToolDataNBT persistentData = tool.getPersistentData();
      persistentData.addSlots(slots.type(), -slots.count());
    }
  }

  /** If true, skips running display modifier validation. Used on modifier recipes that don't have validation failures. */
  default boolean skipDisplayValidation() {
    return false;
  }

  /** Standard implementation of modifier display update. Only used if {@link #isSlotsDynamic()} is true as specific recipes may have different goals. */
  @Override
  default void onDisplayUpdate(RecipeSlot<ItemStack> toolSlot, RecipeSlots<ItemStack> inputs, RecipeSlot<ItemStack> output, ItemStack focus, boolean focusOutput) {
    // tools are only considered as input focuses, output wouldn't make a lot of sense
    // also only care about focuses that are a tool used in this recipe
    if (!focusOutput && !focus.isEmpty() && isTool(focus)) {
      if (Config.CLIENT.showToolInModifiers.get()) {
        // set the input to the passed tool
        toolSlot.set(focus);

        // show the tool with the modifier added as the result, but only if we have space
        ToolStack tool = ToolStack.from(focus);
        if (canApply(tool)) {
          // if adding this modifier leaves you at a valid level, show the focus
          tool = tool.copy();
          // allow easy overriding of applying
          applyModifier(tool);
          // validate modifier requirements
          if (skipDisplayValidation() || tool.tryValidate() == null) {
            output.set(tool.copyStack(focus));
            return;
          }
        }
        // recipe cannot apply, so show no tool
        output.set(List.of());
      } else {
        // TODO: this is only needed as long as singlepart tool is a thing, we can ditch if we remove it
        Item item = focus.getItem();
        toolSlot.set(getToolWithoutModifier().stream().filter(stack -> stack.is(item)).toList());
        output.set(getToolWithModifier().stream().filter(stack -> stack.is(item)).toList());
      }
    }
  }
}
