package slimeknights.tconstruct.tools.data;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.function.Supplier;

/**
 * Extension of modifier entry to hold a supplier
 */
public class DataModifierEntry extends ModifierEntry {
  private final Supplier<? extends Modifier> modifier;
  public DataModifierEntry(Supplier<? extends Modifier> modifier, int level) {
    super(null, level);
    this.modifier = modifier;
  }

  @Override
  public Modifier getModifier() {
    return modifier.get();
  }
}
