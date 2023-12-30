package slimeknights.tconstruct.library.json;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.TConstruct;

/**
 * Represents a value that scales with level with a random component
 * @param flat         Base value that is always applied
 * @param perLevel     Bonus value applied for every level
 * @param randomBonus  A bonus between 0 and this is randomly applied every level
 * @see LevelingValue
 */
public record RandomLevelingValue(float flat, float perLevel, float randomBonus) {
  /** Gets the value at the given level. Due to the random nature, value may change each time. */
  public float computeValue(float level) {
    float value = this.flat + this.perLevel * level;
    if (randomBonus > 0 && level > 0) {
      value += TConstruct.RANDOM.nextFloat() * level * randomBonus;
    }
    return value;
  }

  /** Serializes this to JSON */
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    if (flat != 0) {
      json.addProperty("flat", flat);
    }
    if (perLevel != 0) {
      json.addProperty("per_level", perLevel);
    }
    if (randomBonus != 0) {
      json.addProperty("random", randomBonus);
    }
    return json;
  }

  /** Deserializes this from JSON */
  public static RandomLevelingValue deserialize(JsonObject json) {
    float flat = GsonHelper.getAsFloat(json, "flat", 0);
    float leveling = GsonHelper.getAsFloat(json, "per_level", 0);
    float random = GsonHelper.getAsFloat(json, "random", 0);
    return new RandomLevelingValue(flat, leveling, random);
  }

  /** Gets and deserializes this from a parent JSON */
  public static RandomLevelingValue get(JsonObject parent, String key) {
    return deserialize(GsonHelper.getAsJsonObject(parent, key));
  }

  /** Writes this to the network */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeFloat(flat);
    buffer.writeFloat(perLevel);
    buffer.writeFloat(randomBonus);
  }

  /** Reads this from teh network */
  public static RandomLevelingValue fromNetwork(FriendlyByteBuf buffer) {
    float flat = buffer.readFloat();
    float leveling = buffer.readFloat();
    float random = buffer.readFloat();
    return new RandomLevelingValue(flat, leveling, random);
  }


  /* Helpers */

  public static RandomLevelingValue flat(float value) {
    return new RandomLevelingValue(value, 0, 0);
  }

  public static RandomLevelingValue perLevel(float flat, float leveling) {
    return new RandomLevelingValue(flat, leveling, 0);
  }

  public static RandomLevelingValue random(float flat, float random) {
    return new RandomLevelingValue(flat, 0, random);
  }
}
