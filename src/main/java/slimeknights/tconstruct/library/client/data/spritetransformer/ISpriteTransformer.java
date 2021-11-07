package slimeknights.tconstruct.library.client.data.spritetransformer;

import net.minecraft.client.renderer.texture.NativeImage;

/**
 * Interface for a function that transforms a sprite into another sprite
 */
@FunctionalInterface
public interface ISpriteTransformer {
  /**
   * Transforms the given sprite
   * @param image  Image to transform
   */
  void transform(NativeImage image);

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
