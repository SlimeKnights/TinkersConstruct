package slimeknights.tconstruct.library.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.function.Function;
import java.util.function.IntPredicate;

/**
 * Predicate for checking if an integer is between the given values, inclusive.
 * This object is setup to simplify JSON parsing by creating an instance representing the minimum and maximum range values, then using that object to parse from JSON.
 */
public record IntRange(int min, int max) implements IntPredicate, Loadable<IntRange> {
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

  @Override
  public IntRange convert(JsonElement element, String key, TypedMap context) {
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
    int value = GsonHelper.convertToInt(element, key);
    validateJsonInt(key, value);
    return new IntRange(value, value);
  }

  @Override
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
  @Override
  public IntRange decode(FriendlyByteBuf buffer, TypedMap context) {
    return fromNetwork(buffer);
  }

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

  @Override
  public void encode(FriendlyByteBuf buffer, IntRange object) {
    object.toNetwork(buffer);
  }


  /* Fields */

  /**
   * Gets the value from the given parent, or returns this if missing
   * @param parent  Parent instance
   * @param key     Key to fetch
   * @return  Valid int range
   */
  public IntRange getOrDefault(JsonObject parent, String key) {
    return Loadable.super.getOrDefault(parent, key, this);
  }

  /** Creates a default field using this as the default. */
  public <P> LoadableField<IntRange,P> defaultField(String key, boolean serializeDefault, Function<P,IntRange> getter) {
    return defaultField(key, this, serializeDefault, getter);
  }

  /** Creates a default field using this as the default, skipping serializing if its default. */
  public <P> LoadableField<IntRange,P> defaultField(String key, Function<P,IntRange> getter) {
    return defaultField(key, false, getter);
  }
}
