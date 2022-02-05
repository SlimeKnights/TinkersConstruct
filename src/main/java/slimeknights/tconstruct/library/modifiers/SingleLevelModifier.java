package slimeknights.tconstruct.library.modifiers;

import net.minecraft.network.chat.Component;


/**
 * Extension of modifier simply to remove level from the display name at level 1, intended for modifiers that are single level by design
 *
 * If the modifier is only single level by design, {@link SingleUseModifier} is better.
 */
public class SingleLevelModifier extends Modifier {
  public SingleLevelModifier(int color) {
    super(color);
  }

  @Override
  public Component getDisplayName(int level) {
    if (level == 1) {
      return super.getDisplayName();
    }
    return super.getDisplayName(level);
  }
}
