package slimeknights.tconstruct.library.client.data.spritetransformer;

import net.minecraft.client.renderer.texture.NativeImage;
import slimeknights.mantle.util.GenericRegisteredSerializer;
import slimeknights.tconstruct.library.utils.GenericRegisteredSerializer.IJsonSerializable;

/**
 * Interface for a function that transforms a sprite into another sprite
 */
@FunctionalInterface
public interface ISpriteTransformer extends IJsonSerializable {
  /** Serializer used for this transformer, can register your deserializers with it */
  GenericRegisteredSerializer<ISpriteTransformer> SERIALIZER = new GenericRegisteredSerializer<>();

  /**
   * Transforms the given sprite
   * @param image  Image to transform
   */
  void transform(NativeImage image);

  /** Gets the default color to use in tinting for this transformer, for the case where the texture is missing. Most commonly caused by one addon adding a tool and a different one adding a material */
  default int getFallbackColor() {
    return -1;
  }

  /**
   * Creates a copy of the given sprite and applies the transform to it
   * @param image  Image to transform
   * @return  Transformed copy
   */
  default NativeImage transformCopy(NativeImage image) {
    NativeImage copy = copyImage(image);
    transform(copy);
    return copy;
  }

  /** Copies the given native image */
  static NativeImage copyImage(NativeImage image) {
    NativeImage copy = new NativeImage(image.getWidth(), image.getHeight(), true);
    copy.copyImageData(image);
    return copy;
  }
}
