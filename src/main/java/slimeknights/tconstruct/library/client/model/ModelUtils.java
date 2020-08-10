package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3f;

import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public final class ModelUtils {
  private static final BlockModel.Deserializer DESERIALIZER = new BlockModel.Deserializer();

  /**
   * Deserializes a BlockModel from the given context
   * @param context  Serialization context
   * @param json     JSON data
   * @return  BlockModel instance
   */
  public static BlockModel deserialize(JsonDeserializationContext context, JsonObject json) {
    // the middle parameter is not used in the BlockModel deserializer
    return DESERIALIZER.deserialize(json, null, context);
  }

  /**
   * Converts a JSON array with 3 elements into a Vector3f
   * @param json  JSON object
   * @param name  Name of the array in the object to fetch
   * @return  Vector3f of data
   * @throws JsonParseException  If there is no array or the length is wrong
   */
  public static Vector3f arrayToVector(JsonObject json, String name) {
    JsonArray array = JSONUtils.getJsonArray(json, name);
    if (array.size() != 3) {
      throw new JsonParseException("Expected 3 " + name + " values, found: " + array.size());
    }
    float[] vec = new float[3];
    for(int i = 0; i < vec.length; ++i) {
      vec[i] = JSONUtils.getFloat(array.get(i), name + "[" + i + "]");
    }

    return new Vector3f(vec[0], vec[1], vec[2]);
  }

  /**
   * Gets a rotation from JSON
   * @param json  JSON parent
   * @return  Integer of 0, 90, 180, or 270
   */
  public static int getRotation(JsonObject json, String key) {
    int i = JSONUtils.getInt(json, key, 0);
    if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
      return i;
    } else {
      throw new JsonParseException("Invalid '" + key + "' " + i + " found, only 0/90/180/270 allowed");
    }
  }

  /**
   * Parses a list from an JsonArray
   * @param array   Json array
   * @param mapper  Mapper from JsonObject to new object
   * @param name    Json key of the array
   * @param <T>     Output type
   * @return  List of output objects
   */
  public static <T> List<T> parseList(JsonArray array, Function<JsonObject, T> mapper, String name) {
    if (array.size() == 0) {
      throw new JsonSyntaxException(name + " must have at least 1 element");
    }
    // build the list
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (int i = 0; i < array.size(); i++) {
      builder.add(mapper.apply(JSONUtils.getJsonObject(array.get(i), name + "[" + i + "]")));
    }
    return builder.build();
  }
}
