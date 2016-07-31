package slimeknights.tconstruct.library.client.model.format;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Deseralizes a json in the format of { "textures": { "foo": "texture",... }}
 * Ignores all invalid json
 */
public class ModelTextureDeserializer implements JsonDeserializer<Map<String, String>> {

  public static final ModelTextureDeserializer INSTANCE = new ModelTextureDeserializer();
  public static final Type TYPE = new TypeToken<Map<String, String>>() {}.getType();

  private static final Gson GSON = new Gson();

  @Override
  public Map<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    JsonObject obj = json.getAsJsonObject();
    JsonElement texElem = obj.get("textures");

    if(texElem == null) {
      throw new JsonParseException("Missing textures entry in json");
    }

    return GSON.fromJson(texElem, TYPE);
  }
}
