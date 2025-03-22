package slimeknights.tconstruct.library.tools.stat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import slimeknights.tconstruct.library.utils.Util;

/** Extension of {@link FloatToolStat} that formats the displayed value */
public class CapacityStat extends FloatToolStat {
  private final String formatKey;
  public CapacityStat(ToolStatId name, int color, float defaultValue, float maxValue, String formatKey) {
    super(name, color, defaultValue, 0, maxValue);
    this.formatKey = formatKey;
  }

  public CapacityStat(ToolStatId name, int color, String formatKey) {
    this(name, color, 0, Integer.MAX_VALUE, formatKey);
  }

  @Override
  public Component formatValue(float value) {
    return Component.translatable(getTranslationKey())
                    .append(Component.translatable(formatKey, Util.COMMA_FORMAT.format(value))
                                     .withStyle(style -> style.withColor(getColor())));
  }

  /** Formats the contents of this stat as "#,### / #,### Unit" using the format key and appropriate color. */
  public MutableComponent formatContents(int current, int max) {
    return Component.literal(Util.COMMA_FORMAT.format(current) + " / ")
                    .append(Component.translatable(formatKey, Util.COMMA_FORMAT.format(max)))
                    .withStyle(style -> style.withColor(getColor()));
  }
}
