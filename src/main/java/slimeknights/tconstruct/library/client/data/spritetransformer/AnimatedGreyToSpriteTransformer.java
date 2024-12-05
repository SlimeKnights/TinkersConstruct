package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static com.mojang.blaze3d.platform.NativeImage.getA;

/**
 * Supports including sprites as "part of the palette" which can produce animated textures.
 */
public class AnimatedGreyToSpriteTransformer extends GreyToSpriteTransformer {
  public static final ResourceLocation NAME = TConstruct.getResource("animated_sprite");
  /** Serializer instance */
  public static Deserializer<AnimatedGreyToSpriteTransformer> DESERIALIZER = new Deserializer<>((builder, json) ->
    builder.animated(JsonHelper.getResourceLocation(json, "meta"), IntLoadable.FROM_ONE.getIfPresent(json, "frames")));

  private final ResourceLocation metaPath;
  private final int frames;
  private JsonObject meta;
  protected AnimatedGreyToSpriteTransformer(List<SpriteMapping> sprites, ResourceLocation metaPath, int frames) {
    super(sprites);
    this.metaPath = metaPath;
    this.frames = frames;
  }

  @Override
  public int getFrames() {
    return frames;
  }

  /** Gets the color at the given location from its full color value */
  private int getNewColor(int color, int x, int y, int frame) {
    // if fully transparent, just return fully transparent
    // we do not do 0 alpha RGB values to save effort
    if (getA(color) == 0) {
      return 0x00000000;
    }
    int grey = GreyToColorMapping.getGrey(color);
    int newColor = getSpriteRange(grey).getColor(x, y, frame, grey);
    return GreyToColorMapping.scaleColor(color, newColor, grey);
  }

  @Override
  public void transform(NativeImage image, boolean allowAnimated) {
    int width = image.getWidth();
    // if not animated, just act like we have just 1 frame, means frame data of later parts is ignored
    int frames = allowAnimated ? this.frames : 1;
    int height = image.getHeight() / frames;
    // ensure we don't overwrite the first frame until we finished all other frames, its the only one with data
    for (int f = frames - 1; f >= 0; f--) {
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          // use first frame data to determine result, then save it to the proper frame location
          image.setPixelRGBA(x, y + f * height, getNewColor(image.getPixelRGBA(x, y), x, y, f));
        }
      }
    }
  }

  @Override
  public NativeImage copyImage(NativeImage image, boolean allowAnimated) {
    int frames = allowAnimated ? this.frames : 1;
    NativeImage copy = new NativeImage(image.getWidth(), image.getHeight() * frames, true);
    copy.copyFrom(image); // note this only fills in the first frame
    return copy;
  }

  @Nullable
  @Override
  public JsonObject animationMeta(NativeImage image) {
    if (meta == null) {
      if (READER == null) {
        throw new IllegalStateException("Cannot get image for a sprite without reader");
      }
      try {
        meta = READER.readMetadata(metaPath);
      } catch (IOException ex) {
        throw new IllegalStateException("Failed to load required image", ex);
      }
    }
    return meta;
  }

  /* Serializing */

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject object = super.serialize(context);
    object.addProperty("type", NAME.toString());
    object.addProperty("meta", metaPath.toString());
    object.addProperty("frames", frames);
    return object;
  }
}
