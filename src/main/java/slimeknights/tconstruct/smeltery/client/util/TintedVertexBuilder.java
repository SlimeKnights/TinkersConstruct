package slimeknights.tconstruct.smeltery.client.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import lombok.RequiredArgsConstructor;

/**
 * Vertex builder wrapper that tints all quads passed in
 */
@RequiredArgsConstructor
public class TintedVertexBuilder implements IVertexBuilder {
  /** Base vertex builder */
  private final IVertexBuilder inner;
  /** Tint color from 0-255 */
  private final int tintRed, tintGreen, tintBlue, tintAlpha;

  @Override
  public IVertexBuilder vertex(double x, double y, double z) {
    return inner.vertex(x, y, z);
  }

  @Override
  public IVertexBuilder color(int red, int green, int blue, int alpha) {
    return inner.color((red * tintRed) / 0xFF, (green * tintGreen) / 0xFF, (blue * tintBlue) / 0xFF, (alpha * tintAlpha) / 0xFF);
  }

  @Override
  public IVertexBuilder uv(float u, float v) {
    return inner.uv(u, v);
  }

  @Override
  public IVertexBuilder overlayCoords(int u, int v) {
    return inner.overlayCoords(u, v);
  }

  @Override
  public IVertexBuilder uv2(int u, int v) {
    return inner.uv2(u, v);
  }

  @Override
  public IVertexBuilder normal(float x, float y, float z) {
    return inner.normal(x, y, z);
  }

  @Override
  public void endVertex() {
    inner.endVertex();
  }
}
