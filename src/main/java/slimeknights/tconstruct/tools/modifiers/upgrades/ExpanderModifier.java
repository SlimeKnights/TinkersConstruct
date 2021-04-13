package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import slimeknights.tconstruct.library.modifiers.Modifier;

/** Just a fancy class to change the color. Logic is hardcoded to reduce complexity */
public class ExpanderModifier extends Modifier {
  private final Lazy<Text> LEVEL_1_TITLE = new Lazy<>(() -> new TranslatableText(getTranslationKey())
    .append(" ")
    .append(new TranslatableText(KEY_LEVEL + 1))
    .styled(style -> style.withColor(TextColor.fromRgb(0xff9f50))));

  public ExpanderModifier() {
    super(0xd37cff);
  }

  @Override
  public Text getDisplayName(int level) {
    if (level == 1) {
      return LEVEL_1_TITLE.get();
    }
    return super.getDisplayName(level);
  }
}
