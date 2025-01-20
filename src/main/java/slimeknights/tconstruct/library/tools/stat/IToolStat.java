package slimeknights.tconstruct.library.tools.stat;

import com.google.gson.JsonElement;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

/**
 * Interface for all tool stats, can implement to determine the behavior of stats in the modifier stat builder
 * @param <T>
 */
public interface IToolStat<T> {

  /** Gets the name of this stat for serializing to NBT */
  ToolStatId getName();

  /** Gets the default value for this stat */
  T getDefaultValue();

  /** Clamps the value into a valid range */
  default T clamp(T value) {
    return value;
  }

  /**
   * Checks if the given item supports this stat for display in tooltips or specific stat usages. Typically, is just a tag check.
   * TODO: reconsider the place of this method. We should either be calling supports in more places or ditch it in favor of direct tag usage.
   * @param item  Item to validate
   * @return  True if the stat is supported
   */
  default boolean supports(Item item) {
    return true;
  }


  /* Modifier stat builder */

  /**
   * Creates a builder instance for this stat
   * @return  Stating value
   */
  Object makeBuilder();

  /**
   * Builds this stat using the given builder
   *
   * @param parent   Builder parent, allows fetching properties from teh parent
   * @param builder  Builder object, will be the same object you returned in {@link #makeBuilder()} so unchecked casting is safe
   * @return  Final float value
   */
  T build(ModifierStatsBuilder parent, Object builder);

  /**
   * Updates the stat with a new value. The stat can determine how to merge that with existing values
   * @param builder  Builder instance
   * @param value    Amount to add
   */
  void update(ModifierStatsBuilder builder, T value);


  /* Storing and parsing */

  /** Parses this stat from NBT, return null if the type is invalid */
  @Nullable
  T read(Tag tag);

  /** Writes this stat to NBT */
  @Nullable
  Tag write(T value);

  /** Parses this stat from JSON */
  T deserialize(JsonElement json);

  /** Serializes this stat to JSON */
  JsonElement serialize(T value);

  /** Parses this stat from from the network */
  T fromNetwork(FriendlyByteBuf buffer);

  /** Writes this stat to the network */
  void toNetwork(FriendlyByteBuf buffer, T value);


  /* Display */

  /** Gets the prefix translation key for displaying stats */
  default String getTranslationKey() {
    return Util.makeTranslationKey("tool_stat", getName());
  }

  /** Gets the prefix for this tool stat */
  default MutableComponent getPrefix() {
    return Component.translatable(getTranslationKey());
  }

  /** Gets the description for this tool stat */
  default MutableComponent getDescription() {
    return Component.translatable(getTranslationKey() + ".description");
  }

  /** Formats the value using this tool stat */
  Component formatValue(T value);


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
    return Component.translatable(loc)
      .append(Component.literal(Util.COMMA_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
  }

  /**
   * Creates a text component, coloring the number as a percentage
   * @param loc     Translation key
   * @param color   Color
   * @param number  Number
   * @return  Text component
   */
  static Component formatNumberPercent(String loc, TextColor color, float number) {
    return Component.translatable(loc)
      .append(Component.literal(Util.PERCENT_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
  }

  /**
   * Formats with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @param format  Number formatter
   * @return  Colored percent with prefix
   */
  static Component formatColored(String loc, float number, float offset, DecimalFormat format) {
    float hue = Mth.positiveModulo(offset + number, 2f);
    return Component.translatable(loc).append(Component.literal(format.format(number)).withStyle(style -> style.withColor(TextColor.fromRgb(Mth.hsvToRgb(hue / 1.5f, 1.0f, 0.75f)))));
  }

  /**
   * Formats a multiplier with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @return  Colored percent with prefix
   */
  static Component formatColoredMultiplier(String loc, float number) {
    // 0.5 is red, 1.0 should be roughly green, 1.5 is blue
    return formatColored(loc, number, -0.5f, Util.MULTIPLIER_FORMAT);
  }

  /**
   * Formats an additive bonus with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @return  Colored percent with prefix
   */
  static Component formatColoredBonus(String loc, float number) {
    // -0.5 is red, 0 should be roughly green, +0.5 is blue
    return formatColored(loc, number, 0.5f, Util.BONUS_FORMAT);
  }

  /**
   * Formats a percent boost with hue shifting
   * @param loc     Prefix location
   * @param number  Percentage
   * @return  Colored percent with prefix
   */
  static Component formatColoredPercentBoost(String loc, float number) {
    // -0.5 is red, 0 should be roughly green, +0.5 is blue
    return formatColored(loc, number, 0.5f, Util.PERCENT_BOOST_FORMAT);
  }
}
