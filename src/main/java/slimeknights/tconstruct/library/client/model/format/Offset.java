package slimeknights.tconstruct.library.client.model.format;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Offset {

  public int x;
  public int y;

  /**
   * Deseralizes a json in the format of { "offset": { "x": 1, "y": 2 }}
   * Ignores all invalid json
   */
  public static class OffsetDeserializer implements JsonDeserializer<Offset> {

    public static final OffsetDeserializer INSTANCE = new OffsetDeserializer();
    public static final Type TYPE = new TypeToken<Offset>() {}.getType();

    private static final Gson GSON = new Gson();

    @Override
    public Offset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

      JsonObject obj = json.getAsJsonObject();
      JsonElement texElem = obj.get("offset");

      if(texElem == null) {
        return new Offset();
      }

      return GSON.fromJson(texElem, TYPE);
    }
  }
}
