package slimeknights.tconstruct.library.modifiers;

import net.minecraft.text.Text;

/**
 * Extension of modifier simply to remove level from the display name
 */
public class SingleUseModifier extends Modifier {
  public SingleUseModifier(int color) {
    super(color);
  }

  @Override
  public Text getDisplayName(int level) {
    // display name without the level
    return super.getDisplayName();
  }
}
