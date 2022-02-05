package slimeknights.tconstruct.library.tools.stat;

import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Stat builder for modifiers, allows more fine control over just setting the value
 */
@NoArgsConstructor(staticName = "builder")
public class ModifierStatsBuilder {
  /** If true, a change was made */
  private boolean dirty = false;

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
    dirty = true;
  }

  /** Sets the given stat into the builder from the base NBT, method to help with generics */
  private <T> void setStat(StatsNBT.Builder builder, IToolStat<T> stat, StatsNBT base) {
    if (map.containsKey(stat)) {
      builder.set(stat, stat.build(map.get(stat), base.get(stat)));
    } else {
      builder.set(stat, base.get(stat));
    }
  }

  /** Multiplies the given multiplier value by the parameter */
  public void multiplier(INumericToolStat<?> stat, double value) {
    multipliers.put(stat, (float)(multipliers.getOrDefault(stat, 1f) * value));
  }

  /** Builds the given stat, method exists to make generic easier */
  private <T> void buildStat(StatsNBT.Builder builder, IToolStat<T> stat) {
    builder.set(stat, stat.build(map.get(stat), stat.getDefaultValue()));
  }

  /**
   * Builds the stats
   * @param base  Base stats
   * @return  Built stats
   */
  public StatsNBT build(StatsNBT base) {
    if (!dirty) {
      return base;
    }

    StatsNBT.Builder builder = StatsNBT.builder();

    // first, iterate all stats in the base set
    Set<IToolStat<?>> existing = base.getContainedStats();
    for (IToolStat<?> stat : existing) {
      setStat(builder, stat, base);
    }

    // next, iterate any stats we have that are not in base
    for (IToolStat<?> stat : map.keySet()) {
      if (!existing.contains(stat)) {
       buildStat(builder, stat);
      }
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
      builder.set(entry.getKey(), entry.getValue());
    }
    return builder.build();
  }
}
