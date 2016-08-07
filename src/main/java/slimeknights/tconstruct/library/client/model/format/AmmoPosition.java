package slimeknights.tconstruct.library.client.model.format;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AmmoPosition {

  public float x;
  public float y;
  public float z;
  public float r; // in degree

  /**
   * Deseralizes a json in the format of { "offset": { "x": 1, "y": 2, "z": 0, "r": 45 }}
   * Ignores all invalid json
   */
  public static class AmmoPositionDeserializer implements JsonDeserializer<AmmoPosition> {

    public static final AmmoPositionDeserializer INSTANCE = new AmmoPositionDeserializer();
    public static final Type TYPE = new TypeToken<AmmoPosition>() {}.getType();

    private static final Gson GSON = new Gson();

    @Override
    public AmmoPosition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

      JsonObject obj = json.getAsJsonObject();
      JsonElement texElem = obj.get("ammoPosition");

      if(texElem == null) {
        return null;
      }

      return GSON.fromJson(texElem, TYPE);
    }
  }
}
