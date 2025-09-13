package slimeknights.tconstruct.library.client.data.spritetransformer;

import com.mojang.blaze3d.platform.NativeImage;

/** Sprite transformer that just swaps the color on each pixel */
public interface IRecolorSpriteTransformer extends ISpriteTransformer {
  /** Gets the new color for the given pixel coordinates */
  int getNewColor(int color, int x, int y, int frame);

  @Override
  default void transform(NativeImage image, boolean allowAnimated) {
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        image.setPixelRGBA(x, y, getNewColor(image.getPixelRGBA(x, y), x, y, 0));
      }
    }
  }

  @Override
  default int getFallbackColor() {
    return getNewColor(0xFFD8D8D8, 0, 0, 0);
  }
}
