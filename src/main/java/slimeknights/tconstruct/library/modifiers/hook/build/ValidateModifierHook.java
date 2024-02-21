package slimeknights.tconstruct.library.modifiers.hook.build;

import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Hook used to validate modifier data after tool build. Can return an error message if the new state is not valid.
 */
public interface ValidateModifierHook {
  /**
   * Called when modifiers or tool materials change to validate the tool. You are free to modify persistent data in this hook if needed.
   * This hook will not be called when a modifier is removed, to validate if a removal can be done, use {@link ModifierRemovalHook}.
   * Do not validate max level here, simply ignore levels over max if needed.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link ModifierRemovalHook}: Called when the last level of a modifier is removed.</li>
   *   <li>{@link RawDataModifierHook#removeRawData(IToolStackView, Modifier, RestrictedCompoundTag)}: Called before the modifier is actually removed</li>
   * </ul>
   * @param tool      Current tool instance
   * @param modifier  Modifier being validated
   * @return  null if no validation errors, text component with error message if there was an error
   */
  @Nullable
  Component validate(IToolStackView tool, ModifierEntry modifier);

  /** Merger that returns the first erroring submodule */
  record AllMerger(Collection<ValidateModifierHook> modules) implements ValidateModifierHook {
    @Nullable
    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
      for (ValidateModifierHook module : modules) {
        Component error = module.validate(tool, modifier);
        if (error != null) {
          return error;
        }
      }
      return null;
    }
  }
}
