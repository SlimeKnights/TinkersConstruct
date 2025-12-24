package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Sprite transformer that uses a different transformer for each frame */
public class FramesSpriteTransformer implements IRecolorSpriteTransformer {
  public static final ResourceLocation NAME = TConstruct.getResource("frames");
  private final List<IRecolorSpriteTransformer> frames;
  private final ResourceLocation metaPath;
  private JsonObject meta;

  public FramesSpriteTransformer(List<IRecolorSpriteTransformer> frames, ResourceLocation metaPath) {
    this.frames = frames;
    this.metaPath = metaPath;
  }

  public FramesSpriteTransformer(ResourceLocation metaPath, IRecolorSpriteTransformer... frames) {
    this(List.of(frames), metaPath);
  }


  @Override
  public int getFrames() {
    return frames.size();
  }

  @Override
  public int getNewColor(int color, int x, int y, int frame) {
    IRecolorSpriteTransformer transformer = frames.get(frame);
    return transformer.getNewColor(color, x, y, frame % transformer.getFrames());
  }

  @Override
  public void transform(NativeImage image, boolean allowAnimated) {
    // if no animation, just use the first frame transformer
    if (!allowAnimated) {
      this.frames.get(0).transform(image, false);
      return;
    }

    int width = image.getWidth();
    // if not animated, just act like we have just 1 frame, means frame data of later parts is ignored
    int frames = getFrames();
    int height = image.getHeight() / frames;
    // ensure we don't overwrite the first frame until we finished all other frames, its the only one with data
    for (int f = frames - 1; f >= 0; f--) {
      IRecolorSpriteTransformer transformer = this.frames.get(f);
      int maxFrame = transformer.getFrames();
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          // use first frame data to determine result, then save it to the proper frame location
          image.setPixelRGBA(x, y + f * height, transformer.getNewColor(image.getPixelRGBA(x, y), x, y, f % maxFrame));
        }
      }
    }
  }

  @Override
  public NativeImage copyImage(NativeImage image, boolean allowAnimated) {
    return ISpriteTransformer.copyImage(image, allowAnimated ? getFrames() : 1);
  }

  @Nullable
  @Override
  public JsonObject animationMeta(NativeImage image) {
    if (meta == null) {
      if (GreyToSpriteTransformer.READER == null) {
        throw new IllegalStateException("Cannot get image for a sprite without reader");
      }
      try {
        meta = GreyToSpriteTransformer.READER.readMetadata(metaPath);
      } catch (IOException ex) {
        throw new IllegalStateException("Failed to load required metadata from " + metaPath, ex);
      }
    }
    return meta;
  }

  @Override
  public int getFallbackColor() {
    return frames.get(0).getFallbackColor();
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(NAME);
    JsonArray frames = new JsonArray();
    for (IRecolorSpriteTransformer transformer : this.frames) {
      frames.add(transformer.serialize(context));
    }
    json.add("frames", frames);
    json.addProperty("meta", metaPath.toString());
    return json;
  }

  /** Deserializer instance */
  public static final JsonDeserializer<FramesSpriteTransformer> DESERIALIZER = (json, type, context) -> {
    JsonObject object = json.getAsJsonObject();
    JsonArray array = GsonHelper.getAsJsonArray(object, "frames");
    List<IRecolorSpriteTransformer> frames = new ArrayList<>();
    for (int i = 0; i < array.size(); i++) {
      JsonElement element = array.get(i);
      ISpriteTransformer frame = SERIALIZER.deserialize(element, ISpriteTransformer.class, context);
      if (!(frame instanceof IRecolorSpriteTransformer recoloring)) {
        throw new JsonSyntaxException("Expected frames[" + i + "] to be of type IRecolorSpriteTransformer");
      }
      frames.add(recoloring);
    }
    return new FramesSpriteTransformer(List.copyOf(frames), JsonHelper.getResourceLocation(object, "meta"));
  };
}
