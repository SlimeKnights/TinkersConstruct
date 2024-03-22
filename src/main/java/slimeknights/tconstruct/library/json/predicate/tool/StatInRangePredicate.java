package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.gson.JsonObject;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.function.Predicate;

/**
 * Predicate to check if a tool has the given stat within the range.
 * @see slimeknights.tconstruct.library.json.predicate.tool.StatInSetPredicate
 */
public record StatInRangePredicate(INumericToolStat<?> stat, float min, float max) implements Predicate<StatsNBT>, ToolContextPredicate {
  public static final RecordLoadable<StatInRangePredicate> LOADER = RecordLoadable.create(
    ToolStats.NUMERIC_LOADER.field("stat", StatInRangePredicate::stat),
    FloatLoadable.ANY.defaultField("min", Float.NEGATIVE_INFINITY, StatInRangePredicate::min),
    FloatLoadable.ANY.defaultField("max", Float.POSITIVE_INFINITY, StatInRangePredicate::max),
    StatInRangePredicate::new);

  /**
   * Creates a predicate matching the exact value
   * @param stat  Stat
   * @param value Value to match
   * @return Predicate
   */
  public static StatInRangePredicate match(INumericToolStat<?> stat, float value) {
    return new StatInRangePredicate(stat, value, value);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param min  Min value
   * @return Predicate
   */
  public static StatInRangePredicate min(INumericToolStat<?> stat, float min) {
    return new StatInRangePredicate(stat, min, Float.POSITIVE_INFINITY);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param max  Max value
   * @return Predicate
   */
  public static StatInRangePredicate max(INumericToolStat<?> stat, float max) {
    return new StatInRangePredicate(stat, Float.NEGATIVE_INFINITY, max);
  }

  @Override
  public boolean test(StatsNBT statsNBT) {
    float value = statsNBT.get(stat).floatValue();
    return value >= min && value <= max;
  }

  @Override
  public boolean matches(IToolContext tool) {
    return test(tool.getStats());
  }

  /**
   * Deserializes the predicate from JSON
   * @param json JSON
   * @return Predicate
   */
  public static StatInRangePredicate deserialize(JsonObject json) {
    return LOADER.deserialize(json);
  }

  /**
   * Serializes this to JSON
   * @param json  Json instance to fill
   */
  public JsonObject serialize(JsonObject json) {
    LOADER.serialize(this, json);
    return json;
  }

  /** Serializes this to JSON */
  public JsonObject serialize() {
    return serialize(new JsonObject());
  }

  @Override
  public IGenericLoader<? extends ToolContextPredicate> getLoader() {
    return LOADER;
  }
}
