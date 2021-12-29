package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.library.tools.SlotType;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Container class holding starting slot counts for a tool definition */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DefinitionModifierSlots {
  protected static final DefinitionModifierSlots EMPTY = new DefinitionModifierSlots(Collections.emptyMap());

  /** JSON serializer instance */
  public static Serializer SERIALIZER = new Serializer();

  /** Starting slots for each slot type */
  private final Map<SlotType, Integer> slots;

  /**
   * Gets a value from the json
   * @param slotType   Slot type to get
   * @return  Value of the stat or the default
   */
  public int getSlots(SlotType slotType) {
    return slots.getOrDefault(slotType, 0);
  }

  /** Gets a list of all slots modified by this */
  public Set<SlotType> containedTypes() {
    return slots.keySet();
  }


  /* Packet buffers */

  /** Writes a tool definition stat object to a packet buffer */
  public void write(FriendlyByteBuf buffer) {
    buffer.writeVarInt(slots.size());
    for (Entry<SlotType, Integer> entry : slots.entrySet()) {
      buffer.writeUtf(entry.getKey().getName());
      buffer.writeVarInt(entry.getValue());
    }
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static DefinitionModifierSlots read(FriendlyByteBuf buffer) {
    Builder builder = builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      SlotType slotType = SlotType.getOrCreate(buffer.readUtf(Short.MAX_VALUE));
      builder.setSlots(slotType, buffer.readVarInt());
    }
    return builder.build();
  }

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Logic to build a tool definition stats JSON */
  public static class Builder {
    private final ImmutableMap.Builder<SlotType, Integer> builder = ImmutableMap.builder();

    protected Builder() {}

    /**
     * Adds a stat to the builder
     * @param slotType  Slot type
     * @param count     Number of slots
     * @return Builder instance
     */
    public Builder setSlots(SlotType slotType, int count) {
      builder.put(slotType, count);
      return this;
    }

    /**
     * Creates the instance
     * @return  Instance
     */
    public DefinitionModifierSlots build() {
      return new DefinitionModifierSlots(builder.build());
    }
  }

  /** Serializes and deserializes from JSON */
  protected static class Serializer implements JsonDeserializer<DefinitionModifierSlots>, JsonSerializer<DefinitionModifierSlots> {
    @Override
    public DefinitionModifierSlots deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = GsonHelper.convertToJsonObject(json, "stats");
      Builder builder = builder();
      for (Entry<String,JsonElement> entry : object.entrySet()) {
        int value = GsonHelper.convertToInt(entry.getValue(), entry.getKey());
        SlotType slotType = SlotType.getOrCreate(entry.getKey());
        builder.setSlots(slotType, value);
      }
      return builder.build();
    }

    @Override
    public JsonElement serialize(DefinitionModifierSlots stats, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      for (Entry<SlotType,Integer> entry : stats.slots.entrySet()) {
        json.addProperty(entry.getKey().getName(), entry.getValue());
      }
      return json;
    }
  }
}
