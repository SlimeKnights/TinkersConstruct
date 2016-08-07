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

  public Float[] pos;
  public Float[] rot; // in degree

  /** Returns a new ammopos whith all missing ammo positions filled out by the backup */
  public AmmoPosition combine(AmmoPosition backup) {
    AmmoPosition combined = new AmmoPosition();
    combined.pos = new Float[3];
    combined.rot = new Float[3];

    for(int i = 0; i < 3; i++) {
      copyEntry(pos, backup.pos, combined.pos, i);
      copyEntry(rot, backup.rot, combined.rot, i);
    }

    return combined;
  }

  private void copyEntry(Float[] in1, Float[] in2, Float[] out, int i) {
    if(in1 != null && in1[i] != null) {
      out[i] = in1[i];
    }
    else if(in2 != null && in2[i] != null) {
      out[i] = in2[i];
    }
    else {
      out[i] = 0f;
    }
  }

  /**
   * Deseralizes a json in the format of { "offset": { "pos": [1,2,3], "rot": [0,90,0] }}
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
