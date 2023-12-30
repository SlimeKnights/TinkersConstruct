package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Set;

/**
 * Predicate which checks if a stat is in the given set of values
 * @param <T>     Stat type
 * @param stat    Stat to check
 * @param values  Set of values to match
 * @see slimeknights.tconstruct.library.tools.stat.StatPredicate
 */
public record StatInSetPredicate<T>(IToolStat<T> stat, Set<T> values) implements ToolContextPredicate {
  public StatInSetPredicate(IToolStat<T> stat, T value) {
    this(stat, Set.of(value));
  }

  @Override
  public boolean matches(IToolContext tool) {
    return values.contains(tool.getStats().get(stat));
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<StatInSetPredicate<?>> LOADER = new IGenericLoader<>() {
    @Override
    public StatInSetPredicate<?> deserialize(JsonObject json) {
      return deserialize(json, ToolStats.fromJson(GsonHelper.getAsString(json, "stat")));
    }

    /** Handles generics for the set parsing */
    private static <T> StatInSetPredicate<T> deserialize(JsonObject json, IToolStat<T> stat) {
      Set<T> values = ImmutableSet.copyOf(JsonHelper.parseList(json, "values", (element, key) -> stat.deserialize(element)));
      return new StatInSetPredicate<>(stat, values);
    }

    @Override
    public void serialize(StatInSetPredicate<?> object, JsonObject json) {
      json.addProperty("stat", object.stat.getName().toString());
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
    public StatInSetPredicate<?> fromNetwork(FriendlyByteBuf buffer) {
      return fromNetwork(buffer, ToolStats.fromNetwork(buffer));
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
    public void toNetwork(StatInSetPredicate<?> object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.stat.toString());
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
