package slimeknights.tconstruct.library.modifiers.util;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

/**
 * Modifier loaded from JSON that does not log when missing. Usage:
 * <pre>
 *   new ModifierEntry(new OptionalModifier(id), level);
 * </pre>
 * Can also be used anywhere that accepts {@link LazyModifier} as an argument.
 */
public class OptionalModifier extends LazyModifier {
  public OptionalModifier(ModifierId id) {
    super(id);
  }

  @Override
  protected Modifier getUnchecked() {
    // same as super, except we don't log an error when its missing
    if (result == null) {
      result = ModifierManager.getValue(id);
      if (result == ModifierManager.INSTANCE.getDefaultValue() && !ModifierManager.EMPTY.equals(id)) {
        TConstruct.LOG.debug("Optional modifier with ID {} is absent. Returning the empty modifier.", id);
      }
    }
    return result;
  }
}
