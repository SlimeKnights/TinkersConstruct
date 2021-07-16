package slimeknights.tconstruct.library.tools.stat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.tools.stat.TierToolStat.TierBuilder;
import slimeknights.tconstruct.library.utils.Util;

import java.util.function.IntFunction;

/**
 * Tool stat that works on tiers, keeping the largest given value
 */
@AllArgsConstructor
public class TierToolStat implements IToolStat<TierBuilder> {
  @Getter
  private final ToolStatId name;
  private final int defaultValue;
  private final IntFunction<ITextComponent> displayName;

  @Override
  public float getDefaultValue() {
    return defaultValue;
  }

  @Override
  public float clamp(float value) {
    return MathHelper.clamp(value, 0, Integer.MAX_VALUE);
  }

  @Override
  public TierBuilder makeBuilder() {
    return new TierBuilder();
  }

  /**
   * Sets the tier to the new tier, keeping the largest
   * @param builder  Builder
   * @param value    Value
   */
  public void set(ModifierStatsBuilder builder, int value) {
    builder.updateStat(this, b -> b.tier = Math.max(b.tier, value));
  }

  @Override
  public float build(TierBuilder builder, float value) {
    return Math.max(value, builder.tier);
  }

  @Override
  public ITextComponent formatValue(float number) {
    return new TranslationTextComponent(Util.makeTranslationKey("tool_stat", getName())).appendSibling(displayName.apply((int) number));
  }

  @Override
  public String toString() {
    return "TierToolStat{" + name + '}';
  }

  /** Internal int storage, basically just a int wrapper */
  protected static class TierBuilder {
    private int tier = 0;
  }
}
