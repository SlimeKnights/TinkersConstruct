package slimeknights.tconstruct.library.modifiers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.lang.reflect.Type;

/**
 * Data class holding a modifier with a level
 * TODO: convert to record
 */
@Data
@EqualsAndHashCode
public class ModifierEntry implements Comparable<ModifierEntry> {
  /** JSON serializer instance for GSON */
  public static final Serializer SERIALIZER = new Serializer();

  private final Modifier modifier;
  private final int level;

  @Override
  public int compareTo(ModifierEntry other) {
    Modifier mod1 = this.getModifier(), mod2 = other.getModifier();
    int priority1 = mod1.getPriority(), priority2 = mod2.getPriority();
    // sort by priority first if different
    if (priority1 != priority2) {
      // reversed order so higher goes first
      return Integer.compare(priority2, priority1);
    }
    // fallback to ID path, approximates localized name so we get mostly alphabetical sort in the tooltip
    return mod1.getId().getPath().compareTo(mod2.getId().getPath());
  }

  /** Deserializes a modifier from JSON */
  public static Modifier deserializeModifier(JsonObject parent, String key) {
    return JsonUtils.getAsEntry(TinkerRegistries.MODIFIERS, parent, key);
  }

  /**
   * Parses a modifier entry from JSON
   * @param json  JSON object
   * @return  Parsed JSON
   */
  public static ModifierEntry fromJson(JsonObject json) {
    return new ModifierEntry(deserializeModifier(json, "name"), GsonHelper.getAsInt(json, "level", 1));
  }

  /**
   * Converts this entry to JSON
   * @return  Json object of entry
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("name", modifier.getId().toString());
    json.addProperty("level", level);
    return json;
  }

  /**
   * Reads this modifier entry from the packet buffer
   * @param buffer  Buffer instance
   * @return  Read entry
   */
  public static ModifierEntry read(FriendlyByteBuf buffer) {
    return new ModifierEntry(buffer.readRegistryIdUnsafe(TinkerRegistries.MODIFIERS), buffer.readVarInt());
  }

  /**
   * Writes this modifier entry to the packet buffer
   * @param buffer  Buffer instance
   */
  public void write(FriendlyByteBuf buffer) {
    buffer.writeRegistryIdUnsafe(TinkerRegistries.MODIFIERS, modifier);
    buffer.writeVarInt(level);
  }

  private static class Serializer implements JsonDeserializer<ModifierEntry>, JsonSerializer<ModifierEntry> {
    @Override
    public ModifierEntry deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
      return fromJson(GsonHelper.convertToJsonObject(json, "modifier"));
    }

    @Override
    public JsonElement serialize(ModifierEntry entry, Type type, JsonSerializationContext context) {
      return entry.toJson();
    }
  }
}
