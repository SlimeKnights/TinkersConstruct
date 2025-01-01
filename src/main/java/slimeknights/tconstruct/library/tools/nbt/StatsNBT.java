package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Generic container for tool stats, allows addons to select which stats they wish to use
 */
@SuppressWarnings("ClassCanBeRecord")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
public class StatsNBT {
  /** Set of all tool stat IDs that failed to parse, to reduce log spam as they get parsed many times in UIs when dumb mods don't call proper methods */
  static final Set<String> ERRORED_IDS = new HashSet<>();
  /** Empty stats */
  public static final StatsNBT EMPTY = new StatsNBT(ImmutableMap.of());

  /** All currently contained stats */
  private final Map<IToolStat<?>, Object> stats;

  /** Creates a new stats builder */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Gets a set of all stats contained
   * @return  Stat type set
   */
  public Set<IToolStat<?>> getContainedStats() {
    return stats.keySet();
  }

  /**
   * Checks if the NBT contains the given stat
   * @param stat  Stat to check for
   * @return  True if the stat is contained
   */
  public boolean hasStat(IToolStat<?> stat) {
    return stats.containsKey(stat);
  }

  /**
   * Gets the given tool stat as a float
   * @param stat  Stat
   * @return  Value, or default if the stat is missing
   */
  @SuppressWarnings("unchecked")
  public <T> T get(IToolStat<T> stat) {
    return (T)stats.getOrDefault(stat, stat.getDefaultValue());
  }

  /**
   * Gets the given tool stat as an int
   * @param stat  Stat
   * @return  Value, or default if the stat is missing
   */
  public int getInt(IToolStat<? extends Number> stat) {
    return get(stat).intValue();
  }


  /* NBT parsing */

  /** Reads a tool stat ID from a NBT string */
  @Nullable
  static IToolStat<?> readStatIdFromNBT(String name) {
    ToolStatId statName = ToolStatId.tryParse(name);
    if (statName != null) {
      IToolStat<?> stat = ToolStats.getToolStat(statName);
      if (stat != null) {
        return stat;
      }
    }
    if (!StatsNBT.ERRORED_IDS.contains(name)) {
      StatsNBT.ERRORED_IDS.add(name);
      TConstruct.LOG.error("Ignoring unknown stat " + name + " in tool stat NBT");
    }
    return null;
  }

  /** Reads the stat from NBT */
  public static StatsNBT readFromNBT(@Nullable Tag inbt) {
    if (inbt == null || inbt.getId() != Tag.TAG_COMPOUND) {
      return EMPTY;
    }

    ImmutableMap.Builder<IToolStat<?>, Object> builder = ImmutableMap.builder();

    // simply try each key as a tool stat
    CompoundTag nbt = (CompoundTag)inbt;
    for (String key : nbt.getAllKeys()) {
      Tag tag = nbt.get(key);
      if (tag != null) {
        IToolStat<?> stat = readStatIdFromNBT(key);
        if (stat != null) {
          Object value = stat.read(tag);
          if (value != null) {
            builder.put(stat, value);
          }
        }
      }
    }
    return new StatsNBT(builder.build());
  }

  /** Serializes a stat to NBT, method done to help with generics */
  @SuppressWarnings("unchecked")
  @Nullable
  private static <T> Tag serialize(IToolStat<T> stat, Object value) {
    return stat.write((T) value);
  }

  /** Writes these stats to NBT */
  public CompoundTag serializeToNBT() {
    CompoundTag nbt = new CompoundTag();
    for (Entry<IToolStat<?>,Object> entry : stats.entrySet()) {
      IToolStat<?> stat = entry.getKey();
      Tag serialized = serialize(stat, entry.getValue());
      if (serialized != null) {
        nbt.put(stat.getName().toString(), serialized);
      }
    }
    return nbt;
  }


  /* Network */

  /** Generic helper to write to network */
  @SuppressWarnings("unchecked")
  private static <T> void toNetwork(FriendlyByteBuf buffer, IToolStat<T> stat, Object value) {
    stat.toNetwork(buffer, (T) value);
  }

  /** Writes this to a packet buffer */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeVarInt(stats.size());
    for (Entry<IToolStat<?>,Object> entry : stats.entrySet()) {
      IToolStat<?> stat = entry.getKey();
      buffer.writeUtf(stat.getName().toString());
      toNetwork(buffer, stat, entry.getValue());
    }
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static StatsNBT fromNetwork(FriendlyByteBuf buffer) {
    ImmutableMap.Builder<IToolStat<?>, Object> builder = ImmutableMap.builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      IToolStat<?> stat = ToolStats.LOADER.decode(buffer);
      builder.put(stat, stat.fromNetwork(buffer));
    }
    return new StatsNBT(builder.build());
  }

  /** Create a builder for stats, really just safety checks on the generics for the set method */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ImmutableMap.Builder<IToolStat<?>, Object> builder = ImmutableMap.builder();

    /** Sets the given stat in the builder */
    public <T> Builder set(IToolStat<T> stat, T value) {
      builder.put(stat, stat.clamp(value));
      return this;
    }

    /** Sets the given stat in the builder */
    public Builder set(INumericToolStat<Float> stat, float value) {
      return set(stat, (Float)value);
    }

    /** Builds the stats from the given values */
    public StatsNBT build() {
      Map<IToolStat<?>,Object> map = builder.build();
      if (map.isEmpty()) {
        return EMPTY;
      }
      return new StatsNBT(map);
    }
  }

  public static final RecordLoadable<StatsNBT> LOADABLE = new RecordLoadable<>() {
    @Override
    public StatsNBT deserialize(JsonObject json, TypedMap context) {
      ImmutableMap.Builder<IToolStat<?>,Object> builder = ImmutableMap.builder();
      for (Entry<String,JsonElement> entry : json.entrySet()) {
        IToolStat<?> stat = ToolStats.LOADER.parseString(entry.getKey(), "[map key]");
        builder.put(stat, stat.deserialize(entry.getValue()));
      }
      return new StatsNBT(builder.build());
    }

    /** Serializes a stat to NBT */
    @SuppressWarnings("unchecked")
    private static <T> JsonElement serialize(IToolStat<T> stat, Object value) {
      return stat.serialize((T) value);
    }

    @Override
    public void serialize(StatsNBT stats, JsonObject json) {
      for (Entry<IToolStat<?>,Object> entry : stats.stats.entrySet()) {
        IToolStat<?> stat = entry.getKey();
        json.add(stat.getName().toString(), serialize(stat, entry.getValue()));
      }
    }

    @Override
    public StatsNBT decode(FriendlyByteBuf buffer, TypedMap context) {
      return StatsNBT.fromNetwork(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, StatsNBT stats) {
      stats.toNetwork(buffer);
    }
  };
}
