package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IHaveLoader;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/** Generic registry for an object that can both be sent over a friendly byte buffer and serialized into JSON */
@RequiredArgsConstructor
public class GenericLoaderRegistry<T extends IHaveLoader> implements JsonSerializer<T>, JsonDeserializer<T> {
  /** Map of all serializers for implementations */
  private final NamedComponentRegistry<IGenericLoader<? extends T>> loaders = new NamedComponentRegistry<>("Unknown loader");

  /** Default instance, used for null values instead of null */
  @Nullable
  private final T defaultInstance;

  public GenericLoaderRegistry() {
    this(null);
  }

  /** Registers a deserializer by name */
  public void register(ResourceLocation name, IGenericLoader<? extends T> loader) {
    loaders.register(name, loader);
  }

  /**
   * Deserializes the object from JSON
   * @param object  JSON object
   * @return  Deserialized object
   */
  public T deserialize(JsonObject object) {
    return loaders.deserialize(object, "type").deserialize(object);
  }

  @Override
  public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (defaultInstance != null && json.isJsonNull()) {
      return defaultInstance;
    }
    return deserialize(GsonHelper.convertToJsonObject(json, "object"));
  }

  /** Serializes the object to json, fighting generics */
  @SuppressWarnings("unchecked")
  private <L extends IHaveLoader> JsonObject serialize(IGenericLoader<L> loader, T src) {
    JsonObject json = new JsonObject();
    json.addProperty("type", loaders.getKey((IGenericLoader<? extends T>)loader).toString());
    loader.serialize((L)src, json);
    return json;
  }

  /** Serializes the object to JSON */
  public JsonObject serialize(T src) {
    return serialize(src.getLoader(), src);
  }

  @Override
  public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
    if (src == defaultInstance) {
      return JsonNull.INSTANCE;
    }
    return serialize(src);
  }

  /** Writes the object to the network, fighting generics */
  @SuppressWarnings("unchecked")
  private <L extends IHaveLoader> void toNetwork(IGenericLoader<L> loader, T src, FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(loaders.getKey((IGenericLoader<? extends T>)loader));
    loader.toNetwork((L)src, buffer);
  }

  /** Writes the object to the network */
  public void toNetwork(T src, FriendlyByteBuf buffer) {
    if (defaultInstance != null) {
      if (src == defaultInstance) {
        buffer.writeBoolean(false);
        return;
      }
      buffer.writeBoolean(true);
    }
    toNetwork(src.getLoader(), src, buffer);
  }

  /**
   * Reads the object from the buffer
   * @param buffer  Buffer instance
   * @return  Read object
   */
  public T fromNetwork(FriendlyByteBuf buffer) {
    if (defaultInstance != null) {
      if (!buffer.readBoolean()) {
        return defaultInstance;
      }
    }
    return loaders.fromNetwork(buffer).fromNetwork(buffer);
  }

  /** Interface for a loader */
  public interface IGenericLoader<T extends IHaveLoader> {
    /** Deserializes the object from json */
    T deserialize(JsonObject json);

    /** Reads the object from the packet buffer */
    T fromNetwork(FriendlyByteBuf buffer);

    /** Writes this object to json */
    void serialize(T object, JsonObject json);

    /** Writes this object to the packet buffer */
    void toNetwork(T object, FriendlyByteBuf buffer);
  }

  /** Interface for an object with a loader */
  public interface IHaveLoader {
    /** Gets the loader for the object */
    IGenericLoader<?> getLoader();
  }

  /** Loader instance for an object with only a single implementation */
  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  public static class SingletonLoader<T extends IHaveLoader> implements IGenericLoader<T> {
    private final T instance;

    @Override
    public T deserialize(JsonObject json) {
      return instance;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
      return instance;
    }

    @Override
    public void serialize(T object, JsonObject json) {}

    @Override
    public void toNetwork(T object, FriendlyByteBuf buffer) {}
  }
}
