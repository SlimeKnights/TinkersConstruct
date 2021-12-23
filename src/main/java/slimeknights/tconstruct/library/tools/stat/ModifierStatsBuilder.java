package slimeknights.tconstruct.library.tools.stat;

import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat.FloatBuilder;

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

  /**
   * Updates the given stat in the builder
   * @param stat   New value
   * @param <B>  Stat builder
   */
  @SuppressWarnings("unchecked")
  public <B> void updateStat(IToolStat<B> stat, Consumer<B> consumer) {
    consumer.accept((B)map.computeIfAbsent(stat, IToolStat::makeBuilder));
    dirty = true;
  }

  /** Builds the given stat, method exists to make generic easier */
  @SuppressWarnings("unchecked")
  private <B> float buildStat(IToolStat<B> stat, float value) {
    return stat.build((B)map.get(stat), value);
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
      if (map.containsKey(stat)) {
        builder.set(stat, buildStat(stat, base.getFloat(stat)));
      } else {
        builder.set(stat, base.getFloat(stat));
      }
    }

    // next, iterate any stats we have that are not in base
    for (IToolStat<?> stat : map.keySet()) {
      if (!existing.contains(stat)) {
        builder.set(stat, buildStat(stat, stat.getDefaultValue()));
      }
    }

    return builder.build();
  }

  /**
   * Builds the stat multiplier object for global stat multipliers
   * @return  Multipliers stats
   */
  public StatsNBT buildMultipliers() {
    StatsNBT.Builder builder = StatsNBT.builder();
    for (Entry<IToolStat<?>,Object> entry : map.entrySet()) {
      // yeah I know, super hardcoded, if we need something less hardcoded just request and I'll make it an interface
      if (entry.getValue() instanceof FloatToolStat.FloatBuilder) {
        float multiplier = ((FloatBuilder) entry.getValue()).modifierMultiplier;
        if (multiplier != 1.0f) {
          builder.set(entry.getKey(), multiplier);
        }
      }
    }
    return builder.build();
  }
}
