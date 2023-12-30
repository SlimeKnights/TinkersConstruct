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
public record NestedLoader<T extends IHaveLoader<?>, N extends IHaveLoader<N>>(GenericLoaderRegistry<N> nestedLoader, Function<N, T> constructor, Function<T, N> getter, String typeKey) implements IGenericLoader<T> {
  @Override
  public T deserialize(JsonObject json) {
    // replace our type with the nested type, then run the nested loader
    json.addProperty("type", GsonHelper.getAsString(json, typeKey));
    json.remove(typeKey);
    return constructor.apply(nestedLoader.deserialize(json));
  }

  @Override
  public void serialize(T object, JsonObject json) {
    JsonElement element = nestedLoader.serialize(getter.apply(object));
    // if its an object, copy all the data over
    if (element.isJsonObject()) {
      JsonObject nestedObject = element.getAsJsonObject();
      for (Entry<String, JsonElement> entry : nestedObject.entrySet()) {
        String key = entry.getKey();
        if ("type".equals(key)) {
          key = this.typeKey;
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
  public T fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(nestedLoader.fromNetwork(buffer));
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    nestedLoader.toNetwork(getter.apply(object), buffer);
  }
}
