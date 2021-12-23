package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;

/** Helpers for a few JSON related tasks */
public class JsonUtils {
  private JsonUtils() {}

  /**
   * Reads an integer with a minimum value
   * @param json  Json
   * @param key   Key to read
   * @param min   Minimum and default value
   * @return  Read int
   * @throws JsonSyntaxException if the key is not an int or below the min
   */
  public static int getIntMin(JsonObject json, String key, int min) {
    int value = JSONUtils.getInt(json, key, min);
    if (value < min) {
      throw new JsonSyntaxException(key + " must be at least " + min);
    }
    return value;
  }

  /**
   * Reads an integer with a minimum value
   * @param json  Json element to parse as an integer
   * @param key   Key to read
   * @param min   Minimum
   * @return  Read int
   * @throws JsonSyntaxException if the key is not an int or below the min
   */
  public static int getIntMin(JsonElement json, String key, int min) {
    int value = JSONUtils.getInt(json, key);
    if (value < min) {
      throw new JsonSyntaxException(key + " must be at least " + min);
    }
    return value;
  }

  /** @deprecated use {@link slimeknights.mantle.util.JsonHelper#getResourceLocation(JsonObject, String)} */
  @Deprecated
  public static ResourceLocation getResourceLocation(JsonObject json, String key) {
    return JsonHelper.getResourceLocation(json, key);
  }

  /**
   * Gets a resource location from the given json element
   * @param json  Element
   * @param key   Key
   * @return  Resource location
   * @throws JsonSyntaxException  If the resource location is invalid
   */
  public static ResourceLocation getResourceLocation(JsonElement json, String key) {
    String text = JSONUtils.getString(json, key);
    ResourceLocation location = ResourceLocation.tryCreate(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a Resource location, was '" + text + "'");
    } else {
      return location;
    }
  }
}
