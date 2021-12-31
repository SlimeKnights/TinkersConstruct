package slimeknights.tconstruct.library.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IHaveLoader;

import java.lang.reflect.Type;

/** Generic registry for an object that can both be sent over a friendly byte buffer and serialized into JSON */
public class GenericLoaderRegistry<T extends IHaveLoader> implements JsonSerializer<T>, JsonDeserializer<T> {
  /** Map of all serializers for implementations */
  private final BiMap<ResourceLocation,IGenericLoader<? extends T>> loaders = HashBiMap.create();

  /** Registers a deserializer by name */
  public void register(ResourceLocation name, IGenericLoader<? extends T> loader) {
    if (loaders.putIfAbsent(name, loader) != null) {
      throw new IllegalArgumentException("Duplicate loader with name " + name);
    }
  }

  /**
   * Deserializes the object from JSON
   * @param object  JSON object
   * @return  Deserialized object
   */
  public T deserialize(JsonObject object) {
    ResourceLocation type = JsonHelper.getResourceLocation(object, "type");
    IGenericLoader<? extends T> loader = loaders.get(type);
    if (loader == null) {
      throw new JsonSyntaxException("Unknown loader " + type);
    }
    return loader.deserialize(object);
  }

  @Override
  public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return deserialize(GsonHelper.convertToJsonObject(json, "object"));
  }

  /** Serializes the object to json, fighting generics */
  @SuppressWarnings("unchecked")
  private <L extends IHaveLoader> JsonObject serialize(IGenericLoader<L> loader, T src) {
    JsonObject json = new JsonObject();
    ResourceLocation id = loaders.inverse().get(loader);
    if (id == null) {
      throw new IllegalStateException("Unregistered loader " + loader);
    }
    json.addProperty("type", id.toString());
    loader.serialize((L)src, json);
    return json;
  }

  /** Serializes the object to JSON */
  public JsonObject serialize(T src) {
    return serialize(src.getLoader(), src);
  }

  @Override
  public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
    return serialize(src);
  }

  /** Writes the object to the network, fighting generics */
  @SuppressWarnings("unchecked")
  private <L extends IHaveLoader> void toNetwork(IGenericLoader<L> loader, Object src, FriendlyByteBuf buffer) {
    ResourceLocation id = loaders.inverse().get(loader);
    if (id == null) {
      throw new IllegalStateException("Unregistered loader " + loader);
    }
    buffer.writeResourceLocation(id);
    loader.toNetwork((L)src, buffer);
  }

  /** Writes the object to the network */
  public void toNetwork(T src, FriendlyByteBuf buffer) {
    toNetwork(src.getLoader(), src, buffer);
  }

  /**
   * Reads the object from the buffer
   * @param buffer  Buffer instance
   * @return  Read object
   */
  public T fromNetwork(FriendlyByteBuf buffer) {
    ResourceLocation type = buffer.readResourceLocation();
    IGenericLoader<? extends T> loader = loaders.get(type);
    if (loader == null) {
      throw new DecoderException("Unknown loader " + type);
    }
    return loader.fromNetwork(buffer);
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
