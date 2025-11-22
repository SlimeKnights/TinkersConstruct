package slimeknights.tconstruct.library.modifiers.hook.build;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook for when a tool is crafted.
 * @see ValidateModifierHook
 */
public interface CraftCountModifierHook {
  /**
   * Called when a tool is crafted to let you change the amount crafted. This hook is also called on part swap and related places to prevent dupes, so applying on craft sideeffects is discouraged.
   * Note this hook is only called on crafting in the tinker station or anvil. Tools crafted by other means (such as casting) will not call it, nor will tool modifications.
   * Hook is really only intended to be used by ammo, such as arrows.
   * @param tool    Tool instance
   * @param entry   Modifier running the hook
   * @param amount  Amount crafted
   * @return  New amount crafted. Will be floored if a non-whole number. If 0, nothing will be crafted.
   */
  float modifyCraftCount(IToolStackView tool, ModifierEntry entry, float amount);

  /** Merger running each hook */
  record ComposeMerger(Collection<CraftCountModifierHook> modules) implements CraftCountModifierHook {
    @Override
    public float modifyCraftCount(IToolStackView tool, ModifierEntry entry, float amount) {
      for (CraftCountModifierHook module : modules) {
        amount = module.modifyCraftCount(tool, entry, amount);
        if (amount <= 0) {
          return 0;
        }
      }
      return amount;
    }
  }
}
