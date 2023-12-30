package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.function.Function;

/** Helper to parse variants of resource locations */
public record IdParser<T extends ResourceLocation>(Function<String, T> constructor, String name) {
  /**
   * Creates a new ID from the given string
   * @param string  String
   * @return  ID, or null if invalid
   */
  @Nullable
  public T tryParse(String string) {
    try {
      return constructor.apply(string);
    } catch (ResourceLocationException resourcelocationexception) {
      return null;
    }
  }

  /**
   * Gets an ID from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public T getFromJson(JsonObject json, String key) {
    String text = GsonHelper.getAsString(json, key);
    T location = tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a " + name + " ID, was '" + text + "'");
    }
    return location;
  }

  /**
   * Gets an ID from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public T convertFromJson(JsonElement json, String key) {
    String text = GsonHelper.convertToString(json, key);
    T location = tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a " + name + " ID, was '" + text + "'");
    }
    return location;
  }

  /** Reads an ID from the packet buffer */
  public T fromNetwork(FriendlyByteBuf buf) {
    return constructor.apply(buf.readUtf(Short.MAX_VALUE));
  }
}
