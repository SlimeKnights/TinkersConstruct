package slimeknights.tconstruct.library.tools.stat;

import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Stat builder for modifiers, allows more fine control over just setting the value
 */
@NoArgsConstructor(staticName = "builder")
public class ModifierStatsBuilder {
  /** Map of all stats in the builder */
  private final Map<IToolStat<?>,Object> map = new HashMap<>();
  /** Map of multipliers set */
  private final Map<INumericToolStat<?>,Float> multipliers = new HashMap<>();

  /**
   * Updates the given stat in the builder
   * @param stat   New value
   * @param consumer  Consumer for your builder instance. Will be the same object type as the builder from {@link IToolStat#makeBuilder()}
   */
  @SuppressWarnings("unchecked")
  public <B> void updateStat(IToolStat<?> stat, Consumer<B> consumer) {
    consumer.accept((B)map.computeIfAbsent(stat, IToolStat::makeBuilder));
  }

  /** Multiplies the given multiplier value by the parameter */
  public void multiplier(INumericToolStat<?> stat, double value) {
    multipliers.put(stat, (float)(multipliers.getOrDefault(stat, 1f) * value));
  }


  /* Querying */

  /**
   * Gets the value of the given stat so far.
   * Note: unlike other methods on the builder, this one depends on priority, make sure you consider modifier order if you are going to use this method.
   */
  public <T> T getStat(IToolStat<T> stat) {
    Object builder = map.get(stat);
    if (builder == null) {
      return stat.getDefaultValue();
    }
    return stat.build(this, map.get(stat));
  }

  /** Builds the given stat, method exists to make generic easier */
  private <T> void buildStat(StatsNBT.Builder builder, IToolStat<T> stat) {
    T value = stat.build(this, map.get(stat));
    if (!value.equals(stat.getDefaultValue())) {
      builder.set(stat, value);
    }
  }

  /**
   * Gets the current value of the given multiplier.
   * Note: unlike other methods on the builder, this one depends on priority, make sure you consider modifier order if you are going to use this method.
   */
  public float getMultiplier(INumericToolStat<?> stat) {
    return multipliers.getOrDefault(stat, 1f);
  }

  /**
   * Builds the final stats
   * @return  Built stats
   */
  public StatsNBT build() {
    if (map.isEmpty()) {
      return StatsNBT.EMPTY;
    }

    // next, iterate any stats we have that are not in base
    StatsNBT.Builder builder = StatsNBT.builder();
    for (IToolStat<?> stat : map.keySet()) {
      buildStat(builder, stat);
    }

    return builder.build();
  }

  /**
   * Builds the stat multiplier object for global stat multipliers
   * @return  Multipliers stats
   */
  public MultiplierNBT buildMultipliers() {
    MultiplierNBT.Builder builder = MultiplierNBT.builder();
    for (Entry<INumericToolStat<?>,Float> entry : multipliers.entrySet()) {
      INumericToolStat<?> stat = entry.getKey();
      builder.set(stat, entry.getValue());
    }
    return builder.build();
  }
}
