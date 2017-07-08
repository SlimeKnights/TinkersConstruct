package slimeknights.tconstruct.library.client.model.format;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;

import java.lang.reflect.Type;

import slimeknights.tconstruct.library.client.model.ModelHelper;

public class TransformDeserializer
    implements JsonDeserializer<ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation>> {

  public static final TransformDeserializer INSTANCE = new TransformDeserializer();
  public static final Type TYPE = new TypeToken<ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation>>() {}.getType();

  public static String tag;

  @Override
  public ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject obj = json.getAsJsonObject();
    JsonElement texElem = obj.get(tag);

    if(texElem != null && texElem.isJsonObject()) {
      ItemCameraTransforms itemCameraTransforms = context.deserialize(texElem.getAsJsonObject(), ItemCameraTransforms.class);
      return PerspectiveMapWrapper.getTransforms(itemCameraTransforms);
    }

    return ImmutableMap.of();
  }
}
