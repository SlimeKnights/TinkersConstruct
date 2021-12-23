package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Container class holding stats for a tool definition */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DefinitionToolStats {
  protected static final DefinitionToolStats EMPTY = new DefinitionToolStats(Collections.emptyMap());

  /** JSON serializer instance */
  public static Serializer SERIALIZER = new Serializer();

  private final Map<FloatToolStat, Float> values;

  /**
   * Gets a value from the json
   * @param stat          Stat to get
   * @param defaultValue  Default if the stat is missing
   * @return  Value of the stat or the default
   */
  public float getStat(FloatToolStat stat, float defaultValue) {
    return values.getOrDefault(stat, defaultValue);
  }

  /** Gets all stats contained in this object */
  public Set<FloatToolStat> containedStats() {
    return values.keySet();
  }

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }


  /* Packet buffers */

  /** Writes a tool definition stat object to a packet buffer */
  public void write(PacketBuffer buffer) {
    buffer.writeVarInt(values.size());
    for (Entry<FloatToolStat,Float> entry : values.entrySet()) {
      buffer.writeString(entry.getKey().getName().toString());
      buffer.writeFloat(entry.getValue());
    }
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static DefinitionToolStats read(PacketBuffer buffer) {
    Builder builder = builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      ToolStatId id = new ToolStatId(buffer.readString(Short.MAX_VALUE));
      IToolStat<?> stat = ToolStats.getToolStat(id);
      if (stat instanceof FloatToolStat) {
        builder.addStat((FloatToolStat) stat, buffer.readFloat());
      } else {
        throw new DecoderException("Invalid stat type name " + id);
      }
    }
    return builder.build();
  }

  /** Logic to build a tool definition stats JSON */
  public static class Builder {
    private final ImmutableMap.Builder<FloatToolStat, Float> builder = ImmutableMap.builder();

    protected Builder() {}

    /**
     * Adds a stat to the builder
     * @param stat   Stat to add
     * @param value  Value to add
     * @return Builder instance
     */
    public Builder addStat(FloatToolStat stat, float value) {
      builder.put(stat, value);
      return this;
    }

    /**
     * Creates the instance
     * @return  Instance
     */
    public DefinitionToolStats build() {
      return new DefinitionToolStats(builder.build());
    }
  }

  /** Serializes and deserializes from JSON */
  protected static class Serializer implements JsonDeserializer<DefinitionToolStats>, JsonSerializer<DefinitionToolStats> {
    @Override
    public DefinitionToolStats deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = JSONUtils.getJsonObject(json, "stats");
      Builder builder = builder();
      for (Entry<String,JsonElement> entry : object.entrySet()) {
        ResourceLocation location = ResourceLocation.tryCreate(entry.getKey());
        if (location != null) {
          IToolStat<?> stat = ToolStats.getToolStat(new ToolStatId(location));
          if (stat instanceof FloatToolStat) {
            builder.addStat((FloatToolStat)stat, JSONUtils.getFloat(entry.getValue(), entry.getKey()));
            continue;
          }
        }
        throw new JsonSyntaxException("Unknown stat type " + entry.getKey());
      }
      return builder.build();
    }

    @Override
    public JsonElement serialize(DefinitionToolStats stats, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      for (Entry<FloatToolStat,Float> entry : stats.values.entrySet()) {
        json.addProperty(entry.getKey().getName().toString(), entry.getValue());
      }
      return json;
    }
  }
}
