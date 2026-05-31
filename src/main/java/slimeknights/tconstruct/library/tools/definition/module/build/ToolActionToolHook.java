package slimeknights.tconstruct.library.tools.definition.module.build;

import net.neoforged.neoforge.common.ItemAbility;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook that checks if the tool can perform the given action */
public interface ToolActionToolHook {
  /**
   * Checks if the tool can perform the given tool action. If any modifier returns true, the action is assumed to be present
   * @param tool        Tool to check, will never be broken
   * @param toolAction  Action to check
   * @return  True if the tool can perform the action.
   */
  boolean canPerformAction(IToolStackView tool, ItemAbility toolAction);

  /** Merger that returns true if any of the nested modules returns true */
  record AnyMerger(Collection<ToolActionToolHook> modules) implements ToolActionToolHook {
    @Override
    public boolean canPerformAction(IToolStackView tool, ItemAbility toolAction) {
      for (ToolActionToolHook module : modules) {
        if (module.canPerformAction(tool, toolAction)) {
          return true;
        }
      }
      return false;
    }
  }
}
