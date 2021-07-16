package slimeknights.tconstruct.library.tools.stat;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.utils.Util;

/**
 * Interface for all tool stats, can implement to determine the behavior of stats in the modifier stat builder
 * @param <B>
 */
public interface IToolStat<B> {

  /** Gets the name of this stat for serializing to NBT */
  ToolStatId getName();

  /** Gets the default value for this stat */
  float getDefaultValue();

  /** Clamps the value into a valid range */
  float clamp(float value);


  /* Modifier stat builder */

  /**
   * Gets the base value for the stat builder
   * @return  Stating value
   */
  B makeBuilder();

  /**
   * Builds this stat using the given builder
   * @param builder  Builder
   * @param value    Existing value of the stat
   * @return  Final float value
   */
  float build(B builder, float value);


  /* Display */

  /** Gets the prefix for this tool stat */
  default IFormattableTextComponent getPrefix() {
    return new TranslationTextComponent(Util.makeTranslationKey("tool_stat", getName()));
  }

  /** Gets the description for this tool stat */
  default IFormattableTextComponent getDescription() {
    return new TranslationTextComponent(Util.makeTranslationKey("tool_stat", getName()) + ".description");
  }

  /** Formats the value using this tool stat */
  ITextComponent formatValue(float number);


  /* Formatting helpers */

  /**
   * Creates a text component, coloring the number
   * @param loc     Translation key
   * @param color   Color
   * @param number  Number
   * @return  Text component
   */
  static ITextComponent formatNumber(String loc, Color color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  /**
   * Creates a text component, coloring the number
   * @param loc     Translation key
   * @param color   Color
   * @param number  Number
   * @return  Text component
   */
  static ITextComponent formatNumber(String loc, Color color, float number) {
    return new TranslationTextComponent(loc)
      .appendSibling(new StringTextComponent(Util.COMMA_FORMAT.format(number)).modifyStyle(style -> style.setColor(color)));
  }

  /**
   * Creates a text component, coloring the number as a percentage
   * @param loc     Translation key
   * @param color   Color
   * @param number  Number
   * @return  Text component
   */
  static ITextComponent formatNumberPercent(String loc, Color color, float number) {
    return new TranslationTextComponent(loc)
      .appendSibling(new StringTextComponent(Util.PERCENT_FORMAT.format(number)).modifyStyle(style -> style.setColor(color)));
  }

  /**
   * Formats a multiplier with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @return  Colored percent with prefix
   */
  static ITextComponent formatColoredMultiplier(String loc, float number) {
    // 0.5 is red, 1.0 should be roughly green, 1.5 is blue
    float hue = MathHelper.positiveModulo(number - 0.5f, 2f);
    return new TranslationTextComponent(loc).appendSibling(new StringTextComponent(Util.MULTIPLIER_FORMAT.format(number)).modifyStyle(style -> style.setColor(Color.fromInt(MathHelper.hsvToRGB(hue / 1.5f, 1.0f, 0.75f)))));
  }
}
