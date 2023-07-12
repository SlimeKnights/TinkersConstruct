package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.TConstruct;

/**
 * Represents a value that scales with level.
 * @param flat         Base value that is always applied
 * @param leveling     Bonus value applied for every level
 * @param randomBonus  A bonus between 0 and this is randomly applied every level
 */
public record ScalingValue(float flat, float leveling, float randomBonus) {
  /** Gets the value at the given level. Due to the random nature, value may change each time. */
  public float computeValue(float level) {
    float value = this.flat + this.leveling * level;
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
    if (leveling != 0) {
      json.addProperty("leveling", leveling);
    }
    if (randomBonus != 0) {
      json.addProperty("random", randomBonus);
    }
    return json;
  }

  /** Deserializes this from JSON */
  public static ScalingValue deserialize(JsonObject json) {
    float flat = GsonHelper.getAsFloat(json, "flat", 0);
    float leveling = GsonHelper.getAsFloat(json, "leveling", 0);
    float random = GsonHelper.getAsFloat(json, "random", 0);
    return new ScalingValue(flat, leveling, random);
  }

  /** Gets and deserializes this from a parent JSON */
  public static ScalingValue get(JsonObject parent, String key) {
    return deserialize(GsonHelper.getAsJsonObject(parent, key));
  }

  /** Writes this to the network */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeFloat(flat);
    buffer.writeFloat(leveling);
    buffer.writeFloat(randomBonus);
  }

  /** Reads this from teh network */
  public static ScalingValue fromNetwork(FriendlyByteBuf buffer) {
    float flat = buffer.readFloat();
    float leveling = buffer.readFloat();
    float random = buffer.readFloat();
    return new ScalingValue(flat, leveling, random);
  }


  /* Helpers */

  public static ScalingValue flat(float value) {
    return new ScalingValue(value, 0, 0);
  }

  public static ScalingValue leveling(float flat, float leveling) {
    return new ScalingValue(flat, leveling, 0);
  }

  public static ScalingValue random(float flat, float random) {
    return new ScalingValue(flat, 0, random);
  }
}
