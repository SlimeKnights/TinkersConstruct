package slimeknights.tconstruct.library.modifiers.hook.behavior;

import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook that checks if the tool can perform the given action
 */
public interface ToolActionModifierHook {
  /**
   * Checks if the tool can perform the given tool action. If any modifier returns true, the action is assumed to be present
   * @param tool        Tool to check, will never be broken
   * @param modifier    Modifier level
   * @param toolAction  Action to check
   * @return  True if the tool can perform the action.
   */
  boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction);

  /** Merger that returns true if any of the nested modules returns true */
  record AnyMerger(Collection<ToolActionModifierHook> modules) implements ToolActionModifierHook {
    @Override
    public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
      for (ToolActionModifierHook module : modules) {
        if (module.canPerformAction(tool, modifier, toolAction)) {
          return true;
        }
      }
      return false;
    }
  }
}
