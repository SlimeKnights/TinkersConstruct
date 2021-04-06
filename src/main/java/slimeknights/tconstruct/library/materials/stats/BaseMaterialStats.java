package slimeknights.tconstruct.library.materials.stats;

import lombok.EqualsAndHashCode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;

/**
 * A simple material class without stats.
 * This class is meant to be extended with custom stats added to it for your use.
 */
@EqualsAndHashCode
public abstract class BaseMaterialStats implements IMaterialStats {

  @Override
  public IFormattableTextComponent getLocalizedName() {
    return new TranslationTextComponent(String.format("stat.tconstruct.%s", this.getIdentifier().getPath()));
  }

  public static ITextComponent formatNumber(String loc, Color color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  public static ITextComponent formatNumber(String loc, Color color, float number) {
    return new TranslationTextComponent(loc)
      .append(new StringTextComponent(Util.df.format(number)).modifyStyle(style -> style.setColor(color)));
  }

  public static ITextComponent formatNumberPercent(String loc, Color color, float number) {
    return new TranslationTextComponent(loc)
      .append(new StringTextComponent(Util.dfPercent.format(number)).modifyStyle(style -> style.setColor(color)));
  }

  /**
   * Formats a multiplier with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @return  Colored percent with prefix
   */
  public static ITextComponent formatColoredMultiplier(String loc, float number) {
    // 0.5 is red, 1.0 should be roughly green, 1.5 is blue
    float hue = MathHelper.positiveModulo(number - 0.5f, 2f);
    return new TranslationTextComponent(loc).append(new StringTextComponent(Util.dfMultiplier.format(number)).modifyStyle(style -> style.setColor(Color.fromInt(MathHelper.hsvToRGB(hue / 1.5f, 1.0f, 1.0f)))));
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
  protected static ITextComponent makeTooltip(String name) {
    return new TranslationTextComponent(makeTooltipKey(name));
  }

}
