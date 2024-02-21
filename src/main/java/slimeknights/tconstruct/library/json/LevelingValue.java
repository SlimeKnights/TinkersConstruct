package slimeknights.tconstruct.library.json;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

/**
 * Represents a float value that has a part that scales with level and a part that does not scale.
 * @param flat      Value that does not change with level
 * @param eachLevel  Value that changes each level
 * @see RandomLevelingValue
 */
public record LevelingValue(float flat, float eachLevel) {
  /** Computes the value for the given level */
  public float compute(float level) {
    return this.flat + this.eachLevel * level;
  }

  /** Computes the value for the given tool and modifier entry */
  public float compute(IToolContext tool, ModifierEntry modifier) {
    // small optimization: if strictly flat, skip computing the effective level
    if (this.eachLevel != 0) {
      return this.flat + this.eachLevel * modifier.getEffectiveLevel(tool);
    }
    return this.flat;
  }


  /* JSON */

  /** Serializes this to JSON */
  public JsonObject serialize(JsonObject json) {
    if (flat != 0) {
      json.addProperty("flat", flat);
    }
    if (eachLevel != 0) {
      json.addProperty("each_level", eachLevel);
    }
    return json;
  }

  /** Deserializes this from JSON */
  public static LevelingValue deserialize(JsonObject json) {
    float flat = GsonHelper.getAsFloat(json, "flat", 0);
    float leveling = GsonHelper.getAsFloat(json, "each_level", 0);
    return new LevelingValue(flat, leveling);
  }


  /* Network */

  /** Writes this to the network */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeFloat(flat);
    buffer.writeFloat(eachLevel);
  }

  /** Reads this from teh network */
  public static LevelingValue fromNetwork(FriendlyByteBuf buffer) {
    float flat = buffer.readFloat();
    float leveling = buffer.readFloat();
    return new LevelingValue(flat, leveling);
  }


  /* Construction */

  /** Creates a value that stays constant each level */
  public static LevelingValue flat(float flat) {
    return new LevelingValue(flat, 0);
  }

  /** Creates a value that is multiplied by the level */
  public static LevelingValue eachLevel(float eachLevel) {
    return new LevelingValue(0, eachLevel);
  }

  /** Trait to mix into a builder using leveling values */
  public interface Builder<M> {
    /** Creates an instance with a flat value and a leveling value*/
    M amount(float flat, float eachLevel);

    /** Creates an instance with a value that ignores level */
    default M flat(float flat) {
      return amount(flat, 0);
    }

    /** Creates an instance with a value that increases each level */
    default M eachLevel(float eachLevel) {
      return amount(0, eachLevel);
    }
  }
}
