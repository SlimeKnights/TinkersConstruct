package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Generic container for tool stats, allows addons to select which stats they wish to use
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
public class StatsNBT {
  /** Set of all tool stat IDs that failed to parse, to reduce log spam as they get parsed many times in UIs when dumb mods don't call proper methods */
  private static final Set<ToolStatId> ERRORED_IDS = new HashSet<>();
  /** Empty stats */
  public static final StatsNBT EMPTY = new StatsNBT(ImmutableMap.of());

  /** All currently contained stats */
  private final ImmutableMap<IToolStat<?>, Float> stats;

  /**
   * Gets a set of all stats contained
   * @return  Stat type set
   */
  public Set<IToolStat<?>> getContainedStats() {
    return stats.keySet();
  }

  /**
   * Gets the given tool stat as a float
   * @param stat  Stat
   * @return  Value, or default if the stat is missing
   */
  public float getFloat(IToolStat<?> stat) {
    return stats.getOrDefault(stat, stat.getDefaultValue());
  }

  /**
   * Gets the given tool stat as an int
   * @param stat  Stat
   * @return  Value, or default if the stat is missing
   */
  public int getInt(IToolStat<?> stat) {
    return stats.getOrDefault(stat, stat.getDefaultValue()).intValue();
  }

  /**
   * Reads the stat from NBT */
  public static StatsNBT readFromNBT(@Nullable INBT inbt) {
    if (inbt == null || inbt.getId() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    ImmutableMap.Builder<IToolStat<?>, Float> builder = ImmutableMap.builder();

    // simply try each key as a tool stat
    CompoundNBT nbt = (CompoundNBT)inbt;
    for (String key : nbt.keySet()) {
      if (nbt.contains(key, NBT.TAG_ANY_NUMERIC)) {
        ToolStatId statName = ToolStatId.tryCreate(key);
        if (statName != null) {
          IToolStat<?> stat = ToolStats.getToolStat(statName);
          if (stat != null) {
            builder.put(stat, nbt.getFloat(key));
          } else if (!ERRORED_IDS.contains(statName)) {
            ERRORED_IDS.add(statName);
            TConstruct.log.error("Ignoring unknown stat " + statName + " in tool stat NBT");
          }
        }
      }
    }
    return new StatsNBT(builder.build());
  }

  /** Writes these stats to NBT */
  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    for (Entry<IToolStat<?>,Float> entry : stats.entrySet()) {
      nbt.putFloat(entry.getKey().getName().toString(), entry.getValue());
    }
    return nbt;
  }

  /** Creates a new stats builder */
  public static Builder builder() {
    return new Builder();
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ImmutableMap.Builder<IToolStat<?>, Float> builder = ImmutableMap.builder();

    /** Sets the given stat in the builder */
    public Builder set(IToolStat<?> stat, float value) {
      builder.put(stat, stat.clamp(value));
      return this;
    }

    /** Builds the stats from the given values */
    public StatsNBT build() {
      return new StatsNBT(builder.build());
    }
  }
}
