package slimeknights.tconstruct.library.tools.stat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat.FloatBuilder;
import slimeknights.tconstruct.library.utils.Util;

/**
 * Tool stat representing a float value, used for most numbers
 */
@Getter
public class FloatToolStat implements IToolStat<FloatBuilder> {
  /** Name of this tool stat */
  private final ToolStatId name;
  /** Color for this stat type */
  private final Color color;
  /** Gets the default value for this stat */
  private final float defaultValue;
  /** Min value for this stat */
  private final float minValue;
  /** Max value for this stat */
  private final float maxValue;

  public FloatToolStat(ToolStatId name, int color, float defaultValue, float minValue, float maxValue) {
    this.name = name;
    this.color = Color.fromInt(color);
    this.defaultValue = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  public float clamp(float value) {
    return MathHelper.clamp(value, getMinValue(), getMaxValue());
  }

  @Override
  public FloatBuilder makeBuilder() {
    return new FloatBuilder();
  }

  /**
   * Adds the stat by the given value
   * @param builder  Builder instance
   * @param value    Amount to add
   */
  public void add(ModifierStatsBuilder builder, double value) {
    builder.updateStat(this, b -> b.add += value);
  }

  /**
   * Multiplies the stat by the given value. Multiplication is applied after all addiiton
   * @param builder  Builder instance
   * @param factor   Amount to multiply
   */
  public void multiply(ModifierStatsBuilder builder, double factor) {
    builder.updateStat(this, b -> b.multiply *= factor);
  }

  /**
   * Multiplies the stat by the given value, both among current stats and all future modifiers.
   * Note that this can have an extreme effect on stats, so use very carefully.
   * @param builder  Builder instance
   * @param factor   Amount to multiply
   */
  public void multiplyAll(ModifierStatsBuilder builder, double factor) {
    builder.updateStat(this, b -> {
      b.multiply *= factor;
      b.modifierMultiplier *= factor;
    });
  }

  @Override
  public float build(FloatBuilder builder, float value) {
    return (value + builder.add) * builder.multiply;
  }

  @Override
  public ITextComponent formatValue(float number) {
    return IToolStat.formatNumber(Util.makeTranslationKey("tool_stat", getName()), getColor(), number);
  }

  @Override
  public String toString() {
    return "FloatToolStat{" + name + '}';
  }

  /** Internal builder to store the add and multiply value */
  @NoArgsConstructor
  protected static class FloatBuilder {
    /** Value summed with the base, applies first */
    private float add = 0;
    /** Value multiplied by the sum, applies second */
    private float multiply = 1;
    /** Value to multiply all modifier values by */
    protected float modifierMultiplier = 1;
  }
}
