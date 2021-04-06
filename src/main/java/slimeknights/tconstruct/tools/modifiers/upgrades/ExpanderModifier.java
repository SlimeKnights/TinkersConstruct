package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.library.modifiers.Modifier;

/** Just a fancy class to change the color. Logic is hardcoded to reduce complexity */
public class ExpanderModifier extends Modifier {
  private final Lazy<ITextComponent> LEVEL_1_TITLE = Lazy.of(() -> new TranslationTextComponent(getTranslationKey())
    .appendString(" ")
    .append(new TranslationTextComponent(KEY_LEVEL + 1))
    .modifyStyle(style -> style.setColor(Color.fromInt(0xff9f50))));

  public ExpanderModifier() {
    super(0xd37cff);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    if (level == 1) {
      return LEVEL_1_TITLE.get();
    }
    return super.getDisplayName(level);
  }
}
