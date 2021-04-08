package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * Extension to Resource Location serializer to change the default mod ID
 */
public class ModResourceLocationSerializer extends Identifier.Serializer {

  private final String modId;
  public ModResourceLocationSerializer(String modId) {
    this.modId = modId;
  }

  @Override
  public Identifier deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
    String loc = JsonHelper.asString(element, "location");
    if (!loc.contains(":")) {
      loc = modId + ":" + loc;
    }
    return new Identifier(loc);
  }
}
