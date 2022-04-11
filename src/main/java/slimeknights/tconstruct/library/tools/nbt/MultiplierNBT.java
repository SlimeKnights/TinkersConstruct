package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
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
public class MultiplierNBT {
  /** Serializer to parse this from JSON */
  public static Serializer SERIALIZER = new Serializer();
  /** Empty stats */
  public static final MultiplierNBT EMPTY = new MultiplierNBT(ImmutableMap.of());

  /** All currently contained multipliers */
  private final Map<INumericToolStat<?>, Float> stats;

  /** Creates a new builder */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Gets a set of all stats contained
   * @return  Stat type set
   */
  public Set<INumericToolStat<?>> getContainedStats() {
    return stats.keySet();
  }

  /**
   * Checks if the NBT contains the given stat
   * @param stat  Stat to check for
   * @return  True if the stat is contained
   */
  public boolean hasStat(INumericToolStat<?> stat) {
    return stats.containsKey(stat);
  }

  /**
   * Gets the given multiplier
   * @param stat  Stat
   * @return  Value, or default if the stat is missing
   */
  public float get(INumericToolStat<?> stat) {
    return stats.getOrDefault(stat, 1f);
  }


  /* NBT */

  /** Reads the multipliers from NBT */
  public static MultiplierNBT readFromNBT(@Nullable Tag inbt) {
    if (inbt == null || inbt.getId() != Tag.TAG_COMPOUND) {
      return EMPTY;
    }
    // simply try each key as a tool stat
    Builder builder = builder();
    CompoundTag nbt = (CompoundTag)inbt;
    for (String key : nbt.getAllKeys()) {
      if (nbt.contains(key, Tag.TAG_ANY_NUMERIC) && StatsNBT.readStatIdFromNBT(key) instanceof INumericToolStat<?> stat) {
        builder.set(stat, nbt.getFloat(key));
      }
    }
    return builder.build();
  }

  /** Writes these stats to NBT */
  public CompoundTag serializeToNBT() {
    CompoundTag nbt = new CompoundTag();
    for (Entry<INumericToolStat<?>,Float> entry : stats.entrySet()) {
      nbt.putFloat(entry.getKey().getName().toString(), entry.getValue());
    }
    return nbt;
  }


  /* Network */

  /** Writes this to a packet buffer */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeVarInt(stats.size());
    for (Entry<INumericToolStat<?>,Float> entry : stats.entrySet()) {
      buffer.writeUtf(entry.getKey().getName().toString());
      buffer.writeFloat(entry.getValue());
    }
  }

  /** Reads this object from the network */
  public static MultiplierNBT fromNetwork(FriendlyByteBuf buffer) {
    Builder builder = builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      IToolStat<?> stat = ToolStats.fromNetwork(buffer);
      float value = buffer.readFloat();
      if (stat instanceof INumericToolStat<?> numericStat) {
        builder.set(numericStat, value);
      }
    }
    return builder.build();
  }

  /** Builder for a multiplier, mostly prevents nulls from being added */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ImmutableMap.Builder<INumericToolStat<?>, Float> builder = ImmutableMap.builder();

    /** Sets the given stat in the builder */
    public Builder set(INumericToolStat<?> stat, float value) {
      if (value != 1f) {
        builder.put(stat, Math.max(value, 0));
      }
      return this;
    }

    /** Builds the stats from the given values */
    public MultiplierNBT build() {
      Map<INumericToolStat<?>,Float> map = builder.build();
      if (map.isEmpty()) {
        return EMPTY;
      }
      return new MultiplierNBT(map);
    }
  }

  /** Serializes and deserializes from JSON */
  protected static class Serializer implements JsonDeserializer<MultiplierNBT>, JsonSerializer<MultiplierNBT> {
    @Override
    public MultiplierNBT deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = GsonHelper.convertToJsonObject(json, "stats");
      Builder builder = builder();
      for (Entry<String,JsonElement> entry : object.entrySet()) {
        String key = entry.getKey();
        builder.set(ToolStats.numericFromJson(key), GsonHelper.convertToFloat(entry.getValue(), key));
      }
      return builder.build();
    }

    @Override
    public JsonElement serialize(MultiplierNBT stats, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      for (Entry<INumericToolStat<?>,Float> entry : stats.stats.entrySet()) {
        json.addProperty(entry.getKey().getName().toString(), entry.getValue());
      }
      return json;
    }
  }
}
