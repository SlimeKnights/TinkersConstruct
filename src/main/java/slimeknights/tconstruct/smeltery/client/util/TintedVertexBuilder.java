package slimeknights.tconstruct.smeltery.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.RequiredArgsConstructor;

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
  public VertexConsumer addVertex(float x, float y, float z) {
    inner.addVertex(x, y, z);
    return this;
  }

  @Override
  public VertexConsumer setColor(int red, int green, int blue, int alpha) {
    inner.setColor((red * tintRed) / 0xFF, (green * tintGreen) / 0xFF, (blue * tintBlue) / 0xFF, (alpha * tintAlpha) / 0xFF);
    return this;
  }

  @Override
  public VertexConsumer setUv(float u, float v) {
    inner.setUv(u, v);
    return this;
  }

  @Override
  public VertexConsumer setUv1(int u, int v) {
    inner.setUv1(u, v);
    return this;
  }

  @Override
  public VertexConsumer setUv2(int u, int v) {
    inner.setUv2(u, v);
    return this;
  }

  @Override
  public VertexConsumer setNormal(float x, float y, float z) {
    inner.setNormal(x, y, z);
    return this;
  }
}
