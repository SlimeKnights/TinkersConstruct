package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.netty.handler.codec.DecoderException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
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
  /** Serializer to parse this from JSON */
  public static Serializer SERIALIZER = new Serializer();
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
    ToolStatId statName = ToolStatId.tryCreate(name);
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

  /** Reads a stat from the network */
  static IToolStat<?> statIdFromNetwork(FriendlyByteBuf buffer) {
    ToolStatId id = new ToolStatId(buffer.readUtf(Short.MAX_VALUE));
    IToolStat<?> stat = ToolStats.getToolStat(id);
    if (stat == null) {
      throw new DecoderException("Invalid stat type name " + id);
    }
    return stat;
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static StatsNBT fromNetwork(FriendlyByteBuf buffer) {
    ImmutableMap.Builder<IToolStat<?>, Object> builder = ImmutableMap.builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      IToolStat<?> stat = statIdFromNetwork(buffer);
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

    /** Builds the stats from the given values */
    public StatsNBT build() {
      Map<IToolStat<?>,Object> map = builder.build();
      if (map.isEmpty()) {
        return EMPTY;
      }
      return new StatsNBT(map);
    }
  }

  /** Serializes and deserializes from JSON */
  protected static class Serializer implements JsonDeserializer<StatsNBT>, JsonSerializer<StatsNBT> {
    @Override
    public StatsNBT deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = GsonHelper.convertToJsonObject(json, "stats");
      ImmutableMap.Builder<IToolStat<?>,Object> builder = ImmutableMap.builder();
      for (Entry<String,JsonElement> entry : object.entrySet()) {
        IToolStat<?> stat = ToolStats.fromJson(entry.getKey());
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
    public JsonElement serialize(StatsNBT stats, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      for (Entry<IToolStat<?>,Object> entry : stats.stats.entrySet()) {
        IToolStat<?> stat = entry.getKey();
        json.add(stat.getName().toString(), serialize(stat, entry.getValue()));
      }
      return json;
    }
  }
}
