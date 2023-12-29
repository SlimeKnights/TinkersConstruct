package slimeknights.tconstruct.library.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

import java.util.function.IntPredicate;

/**
 * Predicate for checking if an integer is between the given values, inclusive.
 * This object is setup to simplify JSON parsing by creating an instance representing the minimum and maximum range values, then using that object to parse from JSON.
 */
public record IntRange(int min, int max) implements IntPredicate {
  @Override
  public boolean test(int value) {
    return min <= value && value <= max;
  }

  /* Creating */

  /**
   * Reads an integer within this range
   * @param value  Value to validate
   * @throws IllegalArgumentException if the key is not an int or below the min
   */
  private void validateArgument(String key, int value) {
    if (!test(value)) {
      throw new IllegalArgumentException(key + " must be between " + min + " and " + max + " inclusive");
    }
  }

  /** Creates an int range matching a single value, validated by this range */
  public IntRange exactly(int value) {
    validateArgument("value", value);
    return new IntRange(value, value);
  }

  /** Creates an int range matching a range, validated by this range */
  public IntRange range(int min, int max) {
    validateArgument("min", min);
    validateArgument("max", max);
    if (min > max) {
      throw new IllegalArgumentException("min must be less than or equal to max");
    }
    return new IntRange(min, max);
  }

  /** Creates an int range with the passed minimum and this object's maximum */
  public IntRange min(int min) {
    validateArgument("min", min);
    return new IntRange(min, this.max);
  }

  /** Creates an int range with the passed maximum and this object's minimum */
  public IntRange max(int max) {
    validateArgument("max", max);
    return new IntRange(this.min, max);
  }


  /* JSON */

  /**
   * Reads an integer within this range
   * @param key    Key to read
   * @param value  Value to validate
   * @throws JsonSyntaxException if the key is not an int or below the min
   */
  private void validateJsonInt(String key, int value) {
    if (!test(value)) {
      throw new JsonSyntaxException(key + " must be between " + min + " and " + max + " inclusive");
    }
  }

  /**
   * Reads an int range from JSON, the current object is used as minimum and maximum ranges.
   * @param element  Json element to parse
   * @return new instance parsed
   * @throws JsonSyntaxException if unable to parse
   */
  public IntRange deserialize(JsonElement element) {
    // if an object, find min and max
    if (element.isJsonObject()) {
      JsonObject json = element.getAsJsonObject();
      int min = GsonHelper.getAsInt(json, "min", this.min);
      int max = GsonHelper.getAsInt(json, "max", this.max);
      validateJsonInt("min", min);
      validateJsonInt("max", max);
      if (min > max) {
        throw new JsonSyntaxException("min must be smaller than or equal to max");
      }
      return new IntRange(min, max);
    }
    // if an int, treat as a single value
    int value = GsonHelper.convertToInt(element, "integer");
    validateJsonInt("integer", value);
    return new IntRange(value, value);
  }

  /**
   * Reads an int range from JSON, the current object is used as minimum and maximum ranges.
   * @param parent    Object containing the int range
   * @param key       Key in the object for the int range
   * @throws JsonSyntaxException if unable to parse
   */
  public IntRange getAndDeserialize(JsonObject parent, String key) {
    if (parent.has(key)) {
      return deserialize(parent.get(key));
    }
    return this;
  }

  /**
   * Serializes the passed range, using this range as bounds to skip setting unneeded fields.
   * @param range  Range to serialize
   * @return  Serialized object
   */
  public JsonElement serialize(IntRange range) {
    // if the range is exact, return an integer
    if (range.min == range.max) {
      validateArgument("value", range.min);
      return new JsonPrimitive(range.min);
    }
    JsonObject object = new JsonObject();
    validateArgument("min", range.min);
    validateArgument("max", range.max);
    if (range.min > this.min) {
      object.addProperty("min", range.min);
    }
    if (range.max < this.max) {
      object.addProperty("max", range.max);
    }
    return object;
  }

  /** Serializes this range into the given object parent */
  public void serializeInto(JsonObject parent, String key, IntRange range) {
    if (!this.equals(range)) {
      parent.add(key, serialize(range));
    }
  }


  /* Network */

  /** Reads a range from the buffer */
  public static IntRange fromNetwork(FriendlyByteBuf buffer) {
    int min = buffer.readVarInt();
    int max = buffer.readVarInt();
    return new IntRange(min, max);
  }

  /** Writes this range to the buffer */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeVarInt(min);
    buffer.writeVarInt(max);
  }
}
