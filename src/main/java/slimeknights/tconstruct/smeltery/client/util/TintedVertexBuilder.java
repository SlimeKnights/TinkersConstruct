package slimeknights.tconstruct.smeltery.client.util;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.render.VertexConsumer;

/**
 * Vertex builder wrapper that tints all quads passed in
 */
@RequiredArgsConstructor
public class TintedVertexBuilder implements VertexConsumer {
  /** Base vertex builder */
  private final VertexConsumer inner;
  /** Tint color from 0-255 */
  private final int tintRed, tintGreen, tintBlue, tintAlpha;

  @Override
  public VertexConsumer vertex(double x, double y, double z) {
    return inner.vertex(x, y, z);
  }

  @Override
  public VertexConsumer color(int red, int green, int blue, int alpha) {
    return inner.color((red * tintRed) / 0xFF, (green * tintGreen) / 0xFF, (blue * tintBlue) / 0xFF, (alpha * tintAlpha) / 0xFF);
  }

  @Override
  public VertexConsumer texture(float u, float v) {
    return inner.texture(u, v);
  }

  @Override
  public VertexConsumer overlay(int u, int v) {
    return inner.overlay(u, v);
  }

  @Override
  public VertexConsumer light(int u, int v) {
    return inner.light(u, v);
  }

  @Override
  public VertexConsumer normal(float x, float y, float z) {
    return inner.normal(x, y, z);
  }

  @Override
  public void next() {
    inner.next();
  }
}
