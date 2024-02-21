package slimeknights.tconstruct.library.json.variable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.data.loader.NestedLoader;
import slimeknights.tconstruct.library.json.variable.NestedFallbackLoader.NestedFallback;

import java.util.function.BiFunction;

/**
 * Represents a nested loader with a single float argument. Common loader for variables.
 * @param <T>  Type being loaded
 * @param <N>  Nested type
 */
public record NestedFallbackLoader<T extends IHaveLoader<?> & NestedFallback<N>, N extends IHaveLoader<N>>(String typeKey, GenericLoaderRegistry<N> nestedLoader, BiFunction<N, Float, T> constructor) implements IGenericLoader<T> {
  @Override
  public T deserialize(JsonObject json) {
    NestedLoader.mapType(json, typeKey);
    return constructor.apply(nestedLoader.deserialize(json), GsonHelper.getAsFloat(json, "fallback"));
  }

  @Override
  public void serialize(T object, JsonObject json) {
    NestedLoader.serializeInto(json, typeKey, nestedLoader, object.nested());
    json.addProperty("fallback", object.fallback());
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(nestedLoader.fromNetwork(buffer), buffer.readFloat());
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    nestedLoader.toNetwork(object.nested(), buffer);
    buffer.writeFloat(object.fallback());
  }

  /** Interface for objects using this loader */
  public interface NestedFallback<T> {
    T nested();
    float fallback();
  }
}
