package slimeknights.tconstruct.library.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.util.JSONUtils;

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
}
