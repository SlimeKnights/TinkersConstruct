package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import lombok.RequiredArgsConstructor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.lang.reflect.Type;

/** Sprite transformer that applies the given color mapping to recolor each pixel */
@RequiredArgsConstructor
public class RecolorSpriteTransformer implements ISpriteTransformer {
  public static final ResourceLocation NAME = TConstruct.getResource("recolor_sprite");
  public static final Deserializer DESERIALIZER = new Deserializer();

  /** Color mapping to apply */
  private final IColorMapping colorMapping;

  @Override
  public void transform(NativeImage image) {
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        image.setPixelRGBA(x, y, colorMapping.mapColor(image.getPixelRGBA(x, y)));
      }
    }
  }

  @Override
  public int getFallbackColor() {
    return colorMapping.mapColor(0xFFD8D8D8); // 216 on the greyscale, second color in most of our palettes
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("type", NAME.toString());
    object.add("color_mapping", context.serialize(colorMapping));
    return object;
  }

  /** Serializer for a recolor sprite transformer */
  protected static class Deserializer implements JsonDeserializer<RecolorSpriteTransformer> {
    @Override
    public RecolorSpriteTransformer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      IColorMapping colorMapping = context.deserialize(JsonHelper.getElement(object, "color_mapping"), IColorMapping.class);
      return new RecolorSpriteTransformer(colorMapping);
    }
  }
}
