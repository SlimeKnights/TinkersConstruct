package slimeknights.tconstruct.library.json.predicate;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Loader for AND and OR predicates */
@RequiredArgsConstructor
public class NestedJsonPredicateLoader<I, T extends IJsonPredicate<I>> implements IGenericLoader<T> {
  private final GenericLoaderRegistry<IJsonPredicate<I>> loader;
  private final InvertedJsonPredicate.Loader<I> inverter;
  private final BiFunction<NestedJsonPredicateLoader<I,T>,List<IJsonPredicate<I>>,T> constructor;
  private final Function<T,List<IJsonPredicate<I>>> getter;

  /** Creates a new instance of the relevant predicate */
  @SafeVarargs
  public final T create(IJsonPredicate<I>... children) {
    if (children.length < 2) {
      throw new IllegalStateException("Too few children for nested predicate loader");
    }
    return constructor.apply(this, ImmutableList.copyOf(children));
  }

  /** Inverts the given nested predicate condition */
  InvertedJsonPredicate<I> invert(T instance) {
    return inverter.create(instance);
  }

  @Override
  public T deserialize(JsonObject json) {
    return constructor.apply(this, JsonHelper.parseList(json, "predicates", (e, s) -> loader.deserialize(e)));
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    int max = buffer.readVarInt();
    ImmutableList.Builder<IJsonPredicate<I>> builder = ImmutableList.builder();
    for (int i = 0; i < max; i++) {
      builder.add(loader.fromNetwork(buffer));
    }
    return constructor.apply(this, builder.build());
  }

  @Override
  public void serialize(T object, JsonObject json) {
    JsonArray array = new JsonArray();
    for (IJsonPredicate<I> predicate : getter.apply(object)) {
      array.add(loader.serialize(predicate));
    }
    json.add("predicates", array);
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    List<IJsonPredicate<I>> list = getter.apply(object);
    buffer.writeVarInt(list.size());
    for (IJsonPredicate<I> predicate : list) {
      loader.toNetwork(predicate, buffer);
    }
  }
}
