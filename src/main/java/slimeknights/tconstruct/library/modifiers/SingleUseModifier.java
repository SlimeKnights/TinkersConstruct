package slimeknights.tconstruct.library.modifiers;

import net.minecraft.network.chat.Component;

/**
 * Extension of modifier simply to remove level from the display name, intended for modifiers that do not do anything beyond level 1.
 *
 * If the modifier is only single level by design, {@link SingleLevelModifier} is better.
 */
public class SingleUseModifier extends Modifier {
  public SingleUseModifier(int color) {
    super(color);
  }

  @Override
  public Component getDisplayName(int level) {
    // display name without the level
    return super.getDisplayName();
  }
}
