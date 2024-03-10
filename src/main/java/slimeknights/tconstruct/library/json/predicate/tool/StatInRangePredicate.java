package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
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
    return new StatInRangePredicate(
      ToolStats.numericFromJson(GsonHelper.getAsString(json, "stat")),
      GsonHelper.getAsFloat(json, "min", Float.NEGATIVE_INFINITY),
      GsonHelper.getAsFloat(json, "max", Float.POSITIVE_INFINITY)
    );
  }

  /**
   * Serializes this to JSON
   * @param json  Json instance to fill
   */
  public JsonObject serialize(JsonObject json) {
    json.addProperty("stat", stat.getName().toString());
    if (min > Float.NEGATIVE_INFINITY) {
      json.addProperty("min", min);
    }
    if (max < Float.POSITIVE_INFINITY) {
      json.addProperty("max", max);
    }
    return json;
  }

  /** Serializes this to JSON */
  public JsonObject serialize() {
    return serialize(new JsonObject());
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<StatInRangePredicate> LOADER = new IGenericLoader<>() {
    @Override
    public StatInRangePredicate deserialize(JsonObject json) {
      return StatInRangePredicate.deserialize(json);
    }

    @Override
    public void serialize(StatInRangePredicate object, JsonObject json) {
      object.serialize(json);
    }

    @Override
    public StatInRangePredicate fromNetwork(FriendlyByteBuf buffer) {
      INumericToolStat<?> stat = ToolStats.numericFromNetwork(buffer);
      float min = buffer.readFloat();
      float max = buffer.readFloat();
      return new StatInRangePredicate(stat, min, max);
    }

    @Override
    public void toNetwork(StatInRangePredicate object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.stat.getName().toString());
      buffer.writeFloat(object.min);
      buffer.writeFloat(object.max);
    }
  };
}
