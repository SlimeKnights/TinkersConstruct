package slimeknights.tconstruct.library.modifiers.impl;

import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;


/**
 * Extension of modifier simply to remove level from the display name at level 1, intended for modifiers that are single level by design
 *
 * If the modifier is only single level by design, {@link NoLevelsModifier} is better.
 */
public class SingleLevelModifier extends Modifier {
  @Override
  public Component getDisplayName(int level) {
    if (level == 1) {
      return super.getDisplayName();
    }
    return super.getDisplayName(level);
  }
}
