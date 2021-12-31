package slimeknights.tconstruct.library.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;

/** Generic registry of a component named by a resource location */

public class NamedComponentRegistry<T> {
  /** Registered box expansion types */
  private final BiMap<ResourceLocation,T> values = HashBiMap.create();
  /** Name to make exceptions clearer */
  private final String errorText;

  public NamedComponentRegistry(String errorText) {
    this.errorText = errorText + " ";
  }

  /** Registers the value with the given name */
  public <V extends T> V register(ResourceLocation name, V value) {
    if (values.putIfAbsent(name, value) != null) {
      throw new IllegalArgumentException("Duplicate registration " + name);
    }
    return value;
  }

  /** Gets a value or null if missing */
  @Nullable
  public T getValue(ResourceLocation name) {
    return values.get(name);
  }

  /** Gets the key associated with a value */
  @Nullable
  public ResourceLocation getOptionalKey(T value) {
    return values.inverse().get(value);
  }

  /** Gets the key associated with a value */
  public ResourceLocation getKey(T value) {
    ResourceLocation key = getOptionalKey(value);
    if (key == null) {
      throw new IllegalStateException(errorText + value);
    }
    return key;
  }


  /* Json */

  /** Parse the value from JSON */
  public T deserialize(JsonObject parent, String key) {
    ResourceLocation name = JsonHelper.getResourceLocation(parent, key);
    T value = getValue(name);
    if (value == null) {
      throw new JsonSyntaxException(errorText + name);
    }
    return value;
  }


  /* Network */

  /** Writes the value to the buffer */
  public void toNetwork(T value, FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(getKey(value));
  }

  /** Writes the value to the buffer */
  public void toNetworkOptional(@Nullable T value, FriendlyByteBuf buffer) {
    if (value != null) {
      buffer.writeBoolean(true);
      buffer.writeResourceLocation(getKey(value));
    } else {
      buffer.writeBoolean(false);
    }
  }

  /** Parse the value from JSON */
  public T fromNetwork(FriendlyByteBuf buffer) {
    ResourceLocation name = buffer.readResourceLocation();
    T value = getValue(name);
    if (value == null) {
      throw new DecoderException(errorText + name);
    }
    return value;
  }

  /** Parse the value from JSON */
  @Nullable
  public T fromNetworkOptional(FriendlyByteBuf buffer) {
    if (buffer.readBoolean()) {
      return fromNetwork(buffer);
    }
    return null;
  }
}
