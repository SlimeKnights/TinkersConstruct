package slimeknights.tconstruct.library.materials.stats;

import lombok.EqualsAndHashCode;
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
  public String getLocalizedName() {
    return String.format("stat.%s.name", this.getIdentifier().getPath());
  }

  public static String formatNumber(String loc, String color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  public static String formatNumber(String loc, String color, float number) {
    return String.format("%s: %s%s%s",
      new TranslationTextComponent(loc).getFormattedText(),
      color,
      Util.df.format(number),
      TextFormatting.RESET);
  }

  public static String formatNumberPercent(String loc, String color, float number) {
    return String.format("%s: %s%s%s",
      new TranslationTextComponent(loc).getFormattedText(),
      color,
      Util.dfPercent.format(number),
      TextFormatting.RESET);
  }
}
