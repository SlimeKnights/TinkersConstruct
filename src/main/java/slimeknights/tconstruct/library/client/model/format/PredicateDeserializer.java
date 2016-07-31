package slimeknights.tconstruct.library.client.model.format;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Type;
import java.util.Map;

public class PredicateDeserializer implements JsonDeserializer<ImmutableMap<ResourceLocation, Float>> {

  public static final PredicateDeserializer INSTANCE = new PredicateDeserializer();
  public static final Type TYPE = new TypeToken<ImmutableMap<ResourceLocation, Float>>() {}.getType();

  @Override
  public ImmutableMap<ResourceLocation, Float> deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {

    ImmutableMap.Builder<ResourceLocation, Float> builder = ImmutableMap.builder();

    for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().getAsJsonObject("predicate").entrySet()) {
      builder.put(new ResourceLocation(entry.getKey()), entry.getValue().getAsFloat());
    }

    return builder.build();
  }
}
