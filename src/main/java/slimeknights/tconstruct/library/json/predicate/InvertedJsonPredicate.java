package slimeknights.tconstruct.library.json.predicate;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;

/** Predicate that inverts the condition */
@RequiredArgsConstructor
public class InvertedJsonPredicate<I> implements IJsonPredicate<I> {
  private final InvertedJsonPredicate.Loader<I> loader;
  private final IJsonPredicate<I> base;

  @Override
  public boolean matches(I input) {
    return !base.matches(input);
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<I>> getLoader() {
    return loader;
  }

  @Override
  public IJsonPredicate<I> inverted() {
    return base;
  }

  /** Loader for an inverted JSON predicate */
  @RequiredArgsConstructor
  public static class Loader<I> implements IGenericLoader<InvertedJsonPredicate<I>> {
    private final GenericLoaderRegistry<IJsonPredicate<I>> loader;

    /** Creates a new instance of an inverted predicate */
    public InvertedJsonPredicate<I> create(IJsonPredicate<I> predicate) {
      return new InvertedJsonPredicate<>(this, predicate);
    }

    @Override
    public InvertedJsonPredicate<I> deserialize(JsonObject json) {
      return create(loader.getAndDeserialize(json, "predicate"));
    }

    @Override
    public InvertedJsonPredicate<I> fromNetwork(FriendlyByteBuf buffer) {
      return create(loader.fromNetwork(buffer));
    }

    @Override
    public void serialize(InvertedJsonPredicate<I> object, JsonObject json) {
      json.add("predicate", loader.serialize(object.base));
    }

    @Override
    public void toNetwork(InvertedJsonPredicate<I> object, FriendlyByteBuf buffer) {
      loader.toNetwork(object.base, buffer);
    }
  };
}
