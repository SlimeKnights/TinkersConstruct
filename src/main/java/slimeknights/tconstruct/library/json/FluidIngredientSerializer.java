package slimeknights.tconstruct.library.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

import java.lang.reflect.Type;

/** GSON serializer for fluid ingredients */
public class FluidIngredientSerializer implements JsonDeserializer<FluidIngredient>, JsonSerializer<FluidIngredient> {
  public static FluidIngredientSerializer INSTANCE = new FluidIngredientSerializer();

  private FluidIngredientSerializer() {}

  @Override
  public FluidIngredient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return FluidIngredient.deserialize(json, "ingredient");
  }

  @Override
  public JsonElement serialize(FluidIngredient src, Type typeOfSrc, JsonSerializationContext context) {
    return src.serialize();
  }
}
