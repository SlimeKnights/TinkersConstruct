package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Set;

/**
 * Predicate which checks if a stat is in the given set of values
 * @param <T>     Stat type
 * @param stat    Stat to check
 * @param values  Set of values to match
 * @see StatInRangePredicate
 */
public record StatInSetPredicate<T>(IToolStat<T> stat, Set<T> values) implements ToolStackPredicate {
  public StatInSetPredicate(IToolStat<T> stat, T value) {
    this(stat, Set.of(value));
  }

  @Override
  public boolean matches(IToolStackView tool) {
    return values.contains(tool.getStats().get(stat));
  }

  @Override
  public RecordLoadable<StatInSetPredicate<?>> getLoader() {
    return LOADER;
  }

  /** Loader instance, manually created as the value parsing another value is difficult with the builder */
  public static final RecordLoadable<StatInSetPredicate<?>> LOADER = new RecordLoadable<>() {
    @Override
    public StatInSetPredicate<?> deserialize(JsonObject json, TypedMap context) {
      return deserialize(json, ToolStats.LOADER.getIfPresent(json, "stat", context));
    }

    /** Handles generics for the set parsing */
    private static <T> StatInSetPredicate<T> deserialize(JsonObject json, IToolStat<T> stat) {
      Set<T> values = ImmutableSet.copyOf(JsonHelper.parseList(json, "values", (element, key) -> stat.deserialize(element)));
      return new StatInSetPredicate<>(stat, values);
    }

    @Override
    public void serialize(StatInSetPredicate<?> object, JsonObject json) {
      json.add("stat", ToolStats.LOADER.serialize(object.stat));
      serializeSet(object, json);
    }

    /** Handles generics for the set serializing */
    private static <T> void serializeSet(StatInSetPredicate<T> object, JsonObject json) {
      JsonArray array = new JsonArray();
      for (T value : object.values) {
        array.add(object.stat.serialize(value));
      }
      json.add("values", array);
    }

    @Override
    public StatInSetPredicate<?> decode(FriendlyByteBuf buffer, TypedMap context) {
      return fromNetwork(buffer, ToolStats.LOADER.decode(buffer, context));
    }

    /** Handles generics for the set reading */
    private static <T> StatInSetPredicate<T> fromNetwork(FriendlyByteBuf buffer, IToolStat<T> stat) {
      ImmutableSet.Builder<T> builder = ImmutableSet.builder();
      int max = buffer.readVarInt();
      for (int i = 0; i < max; i++) {
        builder.add(stat.fromNetwork(buffer));
      }
      return new StatInSetPredicate<>(stat, builder.build());
    }

    @Override
    public void encode(FriendlyByteBuf buffer, StatInSetPredicate<?> object) {
      ToolStats.LOADER.encode(buffer, object.stat);
      setToNetwork(object, buffer);
    }

    /** Handles generics for the set writing */
    private static <T> void setToNetwork(StatInSetPredicate<T> object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.values.size());
      for (T value : object.values) {
        object.stat.toNetwork(buffer, value);
      }
    }
  };
}
