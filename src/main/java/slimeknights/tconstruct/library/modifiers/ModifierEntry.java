package slimeknights.tconstruct.library.modifiers;

import lombok.Data;

/**
 * Data class holding a modifier with a level
 */
@Data
public class ModifierEntry {
  private final Modifier modifier;
  private final int level;
}
