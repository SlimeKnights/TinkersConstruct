package slimeknights.tconstruct.library.materials.stats;

import lombok.EqualsAndHashCode;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;

/**
 * A simple material class without stats.
 * This class is meant to be extended with custom stats added to it for your use.
 */
@EqualsAndHashCode
public abstract class BaseMaterialStats implements IMaterialStats {

  @Override
  public ITextComponent getLocalizedName() {
    return new TranslationTextComponent(String.format("stat.%s.name", this.getIdentifier().getPath()));
  }

  public static ITextComponent formatNumber(String loc, String color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  public static ITextComponent formatNumber(String loc, String color, float number) {
    return new TranslationTextComponent(loc)
      .appendText(color + Util.df.format(number));
  }

  public static ITextComponent formatNumberPercent(String loc, String color, float number) {
    return new TranslationTextComponent(loc)
      .appendText(color + Util.dfPercent.format(number));
  }
}
