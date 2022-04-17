package slimeknights.tconstruct.library.modifiers.impl;

import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;

/**
 * Extension of modifier simply to remove level from the display name, intended for modifiers that do not do anything beyond level 1.
 *
 * If the modifier is only single level by design, {@link SingleLevelModifier} is better.
 */
public class NoLevelsModifier extends Modifier {
  @Override
  public Component getDisplayName(int level) {
    // display name without the level
    return super.getDisplayName();
  }
}
