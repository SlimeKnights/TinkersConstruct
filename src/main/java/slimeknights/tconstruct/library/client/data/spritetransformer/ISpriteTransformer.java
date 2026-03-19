package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import slimeknights.mantle.data.gson.GenericRegisteredSerializer;
import slimeknights.mantle.data.gson.GenericRegisteredSerializer.IJsonSerializable;

import javax.annotation.Nullable;

/**
 * Interface for a function that transforms a sprite into another sprite
 */
public interface ISpriteTransformer extends IJsonSerializable {
  /** Serializer used for this transformer, can register your deserializers with it */
  GenericRegisteredSerializer<ISpriteTransformer> SERIALIZER = new GenericRegisteredSerializer<>();

  /**
   * Transforms the given sprite
   * @param image          Image to transform, should be modified
   * @param allowAnimated  If true, the sprite transformer is allowed to generate an animated sprite. If false, the input image cannot be animated
   */
  void transform(NativeImage image, boolean allowAnimated);

  /** Gets the default color to use in tinting for this transformer in AABBGGRR format, for the case where the texture is missing. Most commonly caused by one addon adding a tool and a different one adding a material */
  default int getFallbackColor() {
    return -1;
  }

  /**
   * Copies the image. Overridable version of {@link #copyImage(NativeImage)} to make it easier to wrap a transformer.
   * @param image          Image to copy
   * @param allowAnimated  If true, the image may be animated
   * @return  Copy of the passed image with the first frame filled.
   */
  default NativeImage copyImage(NativeImage image, boolean allowAnimated) {
    return copyImage(image);
  }

  /**
   * Creates a copy of the given sprite and applies the transform to it
   * @param image          Image to transform, do not modify directly
   * @param allowAnimated  If true, the sprite transformer is allowed to generate an animated sprite. If false, the input image cannot be animated
   * @return  Transformed copy
   */
  @NonExtendable
  default NativeImage transformCopy(NativeImage image, boolean allowAnimated) {
    NativeImage copy = copyImage(image, allowAnimated);
    transform(copy, allowAnimated);
    return copy;
  }

  /**
   * Generates the animation metadata for this image
   * @param image  Image getting transformed
   */
  @Nullable
  default JsonObject animationMeta(NativeImage image) {
    return null;
  }

  /** Gets the number of animation frames represented by this transformer, used to make nesting transformers possible */
  default int getFrames() {
    return 1;
  }

  /** Copies the given native image */
  static NativeImage copyImage(NativeImage image) {
    return copyImage(image, 1);
  }

  /** Copies the given native image with the number of frames */
  static NativeImage copyImage(NativeImage image, int frames) {
    NativeImage copy = new NativeImage(image.getWidth(), image.getHeight() * frames, true);
    copy.copyFrom(image); // note this only fills in the first frame
    return copy;
  }
}
