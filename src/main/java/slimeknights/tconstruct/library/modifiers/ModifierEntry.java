package slimeknights.tconstruct.library.modifiers;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data class holding a modifier with a level
 */
@Data
@EqualsAndHashCode
public class ModifierEntry {
  private final Modifier modifier;
  private final int level;
}
