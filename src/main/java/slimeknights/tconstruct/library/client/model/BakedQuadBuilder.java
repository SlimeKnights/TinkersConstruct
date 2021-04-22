package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BakedQuadBuilder implements VertexConsumer {

  private static final int SIZE = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getElements().size();

  private final float[][][] unpackedData = new float[4][SIZE][4];
  private int tint = -1;
  private Direction orientation;
  private Sprite texture;
  private boolean applyDiffuseLighting = true;

  private int vertices = 0;
  private int elements = 0;
  private boolean full = false;
  private boolean contractUVs = false;

  public BakedQuadBuilder(Sprite texture) {
    this.texture = texture;
  }

  public void setContractUVs(boolean value) {
    this.contractUVs = value;
  }

  public VertexFormat getVertexFormat() {
    return VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
  }

  public void setQuadTint(int tint) {
    this.tint = tint;
  }

  public void setQuadOrientation(Direction orientation) {
    this.orientation = orientation;
  }

  public void setTexture(Sprite texture) {
    this.texture = texture;
  }

  public void setApplyDiffuseLighting(boolean diffuse) {
    this.applyDiffuseLighting = diffuse;
  }

  public void put(int element, float... data) {
    for (int i = 0; i < 4; i++) {
      if (i < data.length) {
        unpackedData[vertices][element][i] = data[i];
      } else {
        unpackedData[vertices][element][i] = 0;
      }
    }
    elements++;
    if (elements == SIZE) {
      vertices++;
      elements = 0;
    }
    if (vertices == 4) {
      full = true;
    }
  }

  public BakedQuad build() {
    if (!full) {
      throw new IllegalStateException("not enough data");
    }
    if (texture == null) {
      throw new IllegalStateException("texture not set");
    }
    if (contractUVs) {
      float tX = texture.getWidth() / (texture.getMaxU() - texture.getMinU());
      float tY = texture.getHeight() / (texture.getMaxV() - texture.getMinV());
      float tS = tX > tY ? tX : tY;
      float ep = 1f / (tS * 0x100);
      int uve = 0;
      ImmutableList<VertexFormatElement> elements = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getElements();
      while (uve < elements.size()) {
        VertexFormatElement e = elements.get(uve);
        if (e.getType() == VertexFormatElement.Type.UV && e.getIndex() == 0) {
          break;
        }
        uve++;
      }
      if (uve == elements.size()) {
        throw new IllegalStateException("Can't contract UVs: format doesn't contain UVs");
      }
      float[] uvc = new float[4];
      for (int v = 0; v < 4; v++) {
        for (int i = 0; i < 4; i++) {
          uvc[i] += unpackedData[v][uve][i] / 4;
        }
      }
      for (int v = 0; v < 4; v++) {
        for (int i = 0; i < 4; i++) {
          float uo = unpackedData[v][uve][i];
          float eps = 1f / 0x100;
          float un = uo * (1 - eps) + uvc[i] * eps;
          float ud = uo - un;
          float aud = ud;
          if (aud < 0) aud = -aud;
          if (aud < ep) // not moving a fraction of a pixel
          {
            float udc = uo - uvc[i];
            if (udc < 0) udc = -udc;
            if (udc < 2 * ep) // center is closer than 2 fractions of a pixel, don't move too close
            {
              un = (uo + uvc[i]) / 2;
            } else // move at least by a fraction
            {
              un = uo + (ud < 0 ? ep : -ep);
            }
          }
          unpackedData[v][uve][i] = un;
        }
      }
    }
    int[] packed = new int[VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeInteger() * 4];
    for (int v = 0; v < 4; v++) {
      for (int e = 0; e < SIZE; e++) {
        pack(unpackedData[v][e], packed, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, v, e);
      }
    }
    return new BakedQuad(packed, tint, orientation, texture, applyDiffuseLighting);
  }

  public static void pack(float[] from, int[] to, VertexFormat formatTo, int v, int e) {
    VertexFormatElement element = formatTo.getElements().get(e);
    int vertexStart = v * formatTo.getVertexSize() + formatTo.offsets.getInt(e);
    int count = element.count;
    VertexFormatElement.Format type = element.getFormat();
    int size = type.getSize();
    int mask = (256 << (8 * (size - 1))) - 1;
    for(int i = 0; i < 4; i++)
    {
      if(i < count)
      {
        int pos = vertexStart + size * i;
        int index = pos >> 2;
        int offset = pos & 3;
        int bits;
        float f = i < from.length ? from[i] : 0;
        if(type == VertexFormatElement.Format.FLOAT)
        {
          bits = Float.floatToRawIntBits(f);
        }
        else if(
          type == VertexFormatElement.Format.UBYTE ||
            type == VertexFormatElement.Format.USHORT ||
            type == VertexFormatElement.Format.UINT
        )
        {
          bits = Math.round(f * mask);
        }
        else
        {
          bits = Math.round(f * (mask >> 1));
        }
        to[index] &= ~(mask << (offset * 8));
        to[index] |= (((bits & mask) << (offset * 8)));
        // TODO handle overflow into to[index + 1]
      }
    }
  }

  @Override
  public VertexConsumer vertex(double x, double y, double z) {
    return null;
  }

  @Override
  public VertexConsumer color(int red, int green, int blue, int alpha) {
    return null;
  }

  @Override
  public VertexConsumer texture(float u, float v) {
    return null;
  }

  @Override
  public VertexConsumer overlay(int u, int v) {
    return null;
  }

  @Override
  public VertexConsumer light(int u, int v) {
    return null;
  }

  @Override
  public VertexConsumer normal(float x, float y, float z) {
    return null;
  }

  @Override
  public void next() {

  }
}
