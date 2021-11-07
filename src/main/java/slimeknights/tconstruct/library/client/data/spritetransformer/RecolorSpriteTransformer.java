package slimeknights.tconstruct.library.client.data.spritetransformer;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.texture.NativeImage;

/** Sprite transformer that applies the given color mapping to recolor each pixel */
@RequiredArgsConstructor
public class RecolorSpriteTransformer implements ISpriteTransformer {
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
}
