package slimeknights.tconstruct.library.tools.stat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import slimeknights.tconstruct.library.utils.Util;

/**
 * Interface for all tool stats, can implement to determine the behavior of stats in the modifier stat builder
 * TODO: tool stats need to support strings instead of just numbers
 * @param <B>
 */
public interface IToolStat<B> {

  /** Gets the name of this stat for serializing to NBT */
  ToolStatId getName();

  /** Gets the default value for this stat */
  float getDefaultValue();

  /** Clamps the value into a valid range */
  float clamp(float value);

  /**
   * Applies the given float value as a bonus, used primarily for the stat override modifier
   * Typically better to directly call one of the named methods such as {@link FloatToolStat#add(ModifierStatsBuilder, double)} or {@link TierToolStat#set(ModifierStatsBuilder, int)}
   */
  default void applyBonus(ModifierStatsBuilder builder, double value) {}


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
  default MutableComponent getPrefix() {
    return new TranslatableComponent(Util.makeTranslationKey("tool_stat", getName()));
  }

  /** Gets the description for this tool stat */
  default MutableComponent getDescription() {
    return new TranslatableComponent(Util.makeTranslationKey("tool_stat", getName()) + ".description");
  }

  /** Formats the value using this tool stat */
  Component formatValue(float number);


  /* Formatting helpers */

  /**
   * Creates a text component, coloring the number
   * @param loc     Translation key
   * @param color   Color
   * @param number  Number
   * @return  Text component
   */
  static Component formatNumber(String loc, TextColor color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  /**
   * Creates a text component, coloring the number
   * @param loc     Translation key
   * @param color   Color
   * @param number  Number
   * @return  Text component
   */
  static Component formatNumber(String loc, TextColor color, float number) {
    return new TranslatableComponent(loc)
      .append(new TextComponent(Util.COMMA_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
  }

  /**
   * Creates a text component, coloring the number as a percentage
   * @param loc     Translation key
   * @param color   Color
   * @param number  Number
   * @return  Text component
   */
  static Component formatNumberPercent(String loc, TextColor color, float number) {
    return new TranslatableComponent(loc)
      .append(new TextComponent(Util.PERCENT_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
  }

  /**
   * Formats a multiplier with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @return  Colored percent with prefix
   */
  static Component formatColoredMultiplier(String loc, float number) {
    // 0.5 is red, 1.0 should be roughly green, 1.5 is blue
    float hue = Mth.positiveModulo(number - 0.5f, 2f);
    return new TranslatableComponent(loc).append(new TextComponent(Util.MULTIPLIER_FORMAT.format(number)).withStyle(style -> style.withColor(TextColor.fromRgb(Mth.hsvToRgb(hue / 1.5f, 1.0f, 0.75f)))));
  }
}
