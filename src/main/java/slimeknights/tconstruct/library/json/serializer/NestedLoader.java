package slimeknights.tconstruct.library.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;

import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Loader that loads from another loader
 * @param <T>  Object being loaded
 * @param <N>  Nested object type
 */
public record NestedLoader<T extends IHaveLoader<?>, N extends IHaveLoader<N>>(String typeKey, GenericLoaderRegistry<N> nestedLoader, Function<N, T> constructor, Function<T, N> getter) implements IGenericLoader<T> {
  /** Moves the passed type key to "type" */
  public static void mapType(JsonObject json, String typeKey) {
    // replace our type with the nested type, then run the nested loader
    json.addProperty("type", GsonHelper.getAsString(json, typeKey));
    json.remove(typeKey);
  }

  @Override
  public T deserialize(JsonObject json) {
    mapType(json, typeKey);
    return constructor.apply(nestedLoader.deserialize(json));
  }

  /**
   * Serializes the passed object into the passed JSON
   * @param json      JSON target for serializing
   * @param typeKey   Key to use for "type" in the serialized value
   * @param loader    Loader for serializing the value
   * @param value     Value to serialized
   * @param <N>  Type of value
   */
  public static <N extends IHaveLoader<N>> void serializeInto(JsonObject json, String typeKey, GenericLoaderRegistry<N> loader, N value) {
    JsonElement element = loader.serialize(value);
    // if its an object, copy all the data over
    if (element.isJsonObject()) {
      JsonObject nestedObject = element.getAsJsonObject();
      for (Entry<String, JsonElement> entry : nestedObject.entrySet()) {
        String key = entry.getKey();
        if ("type".equals(key)) {
          key = typeKey;
        }
        json.add(key, entry.getValue());
      }
    } else if (element.isJsonPrimitive()){
      // if its a primitive, its the type ID, add just that by itself
      json.add(typeKey, element);
    } else {
      throw new JsonIOException("Unable to serialize nested object, expected string or object");
    }
  }

  @Override
  public void serialize(T object, JsonObject json) {
    serializeInto(json, typeKey, nestedLoader, getter.apply(object));
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(nestedLoader.fromNetwork(buffer));
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    nestedLoader.toNetwork(getter.apply(object), buffer);
  }
}
