package slimeknights.tconstruct.library.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.lang.reflect.Type;

/** Serializer for a forge condition */
public class ConditionSerializer implements JsonDeserializer<ICondition>, JsonSerializer<ICondition> {
  public static final ConditionSerializer INSTANCE = new ConditionSerializer();

  private ConditionSerializer() {}

  @Override
  public ICondition deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
    return CraftingHelper.getCondition(GsonHelper.convertToJsonObject(json, "condition"));
  }

  @Override
  public JsonElement serialize(ICondition condition, Type type, JsonSerializationContext context) {
    return CraftingHelper.serialize(condition);
  }
}
