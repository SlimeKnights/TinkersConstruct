package slimeknights.tconstruct.library.json;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;

/**
 * Represents a value that scales with level with a random component
 * @param flat         Base value that is always applied
 * @param perLevel     Bonus value applied for every level
 * @param randomBonus  A bonus between 0 and this is randomly applied every level
 * @see LevelingValue
 */
public record RandomLevelingValue(float flat, float perLevel, float randomBonus) {
  /** Loadable instance for parsing */
  public static final RecordLoadable<RandomLevelingValue> LOADABLE = RecordLoadable.create(
    FloatLoadable.ANY.defaultField("flat", 0f, RandomLevelingValue::flat),
    FloatLoadable.ANY.defaultField("per_level", 0f, RandomLevelingValue::perLevel),
    FloatLoadable.ANY.defaultField("random", 0f, RandomLevelingValue::randomBonus),
    RandomLevelingValue::new);

  /** Gets the value at the given level. Due to the random nature, value may change each time. */
  public float computeValue(float level) {
    float value = this.flat + this.perLevel * level;
    if (randomBonus > 0 && level > 0) {
      value += TConstruct.RANDOM.nextFloat() * level * randomBonus;
    }
    return value;
  }

  /** @deprecated use {@link #LOADABLE} with {@link slimeknights.mantle.data.loadable.Loadable#serialize(Object)} or {@link RecordLoadable#serialize(Object)} */
  @Deprecated(forRemoval = true)
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    LOADABLE.serialize(this, json);
    return json;
  }

  /** @deprecated use {@link #LOADABLE} with {@link RecordLoadable#deserialize(JsonObject)} */
  @Deprecated(forRemoval = true)
  public static RandomLevelingValue deserialize(JsonObject json) {
    return LOADABLE.deserialize(json);
  }

  /** @deprecated use {@link #LOADABLE} with {@link slimeknights.mantle.data.loadable.Loadable#getIfPresent(JsonObject, String)} */
  @Deprecated(forRemoval = true)
  public static RandomLevelingValue get(JsonObject parent, String key) {
    return LOADABLE.getIfPresent(parent, key);
  }

  /** @deprecated use {@link #LOADABLE} with {@link slimeknights.mantle.data.loadable.Loadable#encode(FriendlyByteBuf, Object)} */
  @Deprecated(forRemoval = true)
  public void toNetwork(FriendlyByteBuf buffer) {
    LOADABLE.encode(buffer, this);
  }

  /** @deprecated use {@link #LOADABLE} with {@link slimeknights.mantle.data.loadable.Loadable#decode(FriendlyByteBuf)} */
  @Deprecated(forRemoval = true)
  public static RandomLevelingValue fromNetwork(FriendlyByteBuf buffer) {
    return LOADABLE.decode(buffer);
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
