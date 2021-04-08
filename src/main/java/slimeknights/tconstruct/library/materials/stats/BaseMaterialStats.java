package slimeknights.tconstruct.library.materials.stats;

import lombok.EqualsAndHashCode;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.library.Util;

/**
 * A simple material class without stats.
 * This class is meant to be extended with custom stats added to it for your use.
 */
@EqualsAndHashCode
public abstract class BaseMaterialStats implements IMaterialStats {

  @Override
  public MutableText getLocalizedName() {
    return new TranslatableText(String.format("stat.tconstruct.%s", this.getIdentifier().getPath()));
  }

  public static Text formatNumber(String loc, TextColor color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  public static Text formatNumber(String loc, TextColor color, float number) {
    return new TranslatableText(loc)
      .append(new LiteralText(Util.df.format(number)).styled(style -> style.withColor(color)));
  }

  public static Text formatNumberPercent(String loc, TextColor color, float number) {
    return new TranslatableText(loc)
      .append(new LiteralText(Util.dfPercent.format(number)).styled(style -> style.withColor(color)));
  }

  /**
   * Formats a multiplier with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @return  Colored percent with prefix
   */
  public static Text formatColoredMultiplier(String loc, float number) {
    // 0.5 is red, 1.0 should be roughly green, 1.5 is blue
    float hue = MathHelper.floorMod(number - 0.5f, 2f);
    return new TranslatableText(loc).append(new LiteralText(Util.dfMultiplier.format(number)).styled(style -> style.withColor(TextColor.fromRgb(MathHelper.hsvToRgb(hue / 1.5f, 1.0f, 1.0f)))));
  }

  /**
   * Helper to make a translation key for the given name
   * @param name  name
   * @return  Text component
   */
  protected static String makeTooltipKey(String name) {
    return Util.makeTranslationKey("stat", name);
  }

  /**
   * Helper to make a text component for the given name
   * @param name  name
   * @return  Text component
   */
  protected static Text makeTooltip(String name) {
    return new TranslatableText(makeTooltipKey(name));
  }

}
