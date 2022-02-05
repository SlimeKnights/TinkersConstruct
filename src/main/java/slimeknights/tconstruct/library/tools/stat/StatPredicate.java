package slimeknights.tconstruct.library.tools.stat;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.function.Predicate;

/**
 * Predicate to check if a tool has the given stat
 */
public record StatPredicate(INumericToolStat<?> stat, float min, float max) implements Predicate<StatsNBT> {

  /**
   * Creates a predicate matching the exact value
   * @param stat  Stat
   * @param value Value to match
   * @return Predicate
   */
  public static StatPredicate match(INumericToolStat<?> stat, float value) {
    return new StatPredicate(stat, value, value);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param min  Min value
   * @return Predicate
   */
  public static StatPredicate min(INumericToolStat<?> stat, float min) {
    return new StatPredicate(stat, min, Float.POSITIVE_INFINITY);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param max  Max value
   * @return Predicate
   */
  public static StatPredicate max(INumericToolStat<?> stat, float max) {
    return new StatPredicate(stat, Float.NEGATIVE_INFINITY, max);
  }

  @Override
  public boolean test(StatsNBT statsNBT) {
    float value = statsNBT.get(stat).floatValue();
    return value >= min && value <= max;
  }

  /**
   * Deserializes the predicate from JSON
   * @param json JSON
   * @return Predicate
   */
  public static StatPredicate deserialize(JsonObject json) {
    return new StatPredicate(
      ToolStats.numericFromJson(GsonHelper.getAsString(json, "stat")),
      GsonHelper.getAsFloat(json, "min", Float.NEGATIVE_INFINITY),
      GsonHelper.getAsFloat(json, "max", Float.NEGATIVE_INFINITY)
    );
  }

  /**
   * Serializes this to JSON
   */
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("stat", stat.getName().toString());
    if (min > Float.NEGATIVE_INFINITY) {
      json.addProperty("min", min);
    }
    if (max < Float.POSITIVE_INFINITY) {
      json.addProperty("max", max);
    }
    return json;
  }
}
