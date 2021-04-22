package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.Function;

public class ItemLayerModel {

  private static final Direction[] HORIZONTALS = {Direction.UP, Direction.DOWN};
  private static final Direction[] VERTICALS = {Direction.WEST, Direction.EAST};

  private static int getPixelColor(Sprite sprite, int frameIndex, int x, int y) {
    return sprite.images[0].getPixelColor(x + sprite.frameXs[frameIndex] * sprite.info.width, y + sprite.frameYs[frameIndex] * sprite.info.height);
  }
  
  public static ImmutableList<BakedQuad> getQuadsForSprites(List<SpriteIdentifier> textures, AffineTransformation transform, Function<SpriteIdentifier, Sprite> spriteGetter) {
    return getQuadsForSprites(textures, transform, spriteGetter, Collections.emptySet());
  }

  public static ImmutableList<BakedQuad> getQuadsForSprites(List<SpriteIdentifier> textures, AffineTransformation transform, Function<SpriteIdentifier, Sprite> spriteGetter, Set<Integer> fullbrights) {
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    for (int i = 0; i < textures.size(); i++) {
      Sprite tas = spriteGetter.apply(textures.get(i));
      builder.addAll(getQuadsForSprite(i, tas, transform, fullbrights.contains(i)));
    }
    return builder.build();
  }

  public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, Sprite sprite, AffineTransformation transform) {
    return getQuadsForSprite(tint, sprite, transform, false);
  }

  public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, Sprite sprite, AffineTransformation transform, boolean fullbright) {
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

    int uMax = sprite.getWidth();
    int vMax = sprite.getHeight();

    FaceData faceData = new FaceData(uMax, vMax);
    boolean translucent = false;

    for (int f = 0; f < sprite.getFrameCount(); f++) {
      boolean ptu;
      boolean[] ptv = new boolean[uMax];
      Arrays.fill(ptv, true);
      for (int v = 0; v < vMax; v++) {
        ptu = true;
        for (int u = 0; u < uMax; u++) {
          int alpha = getPixelColor(sprite, f, u, vMax - v - 1) >> 24 & 0xFF;
          boolean t = alpha / 255f <= 0.1f;

          if (!t && alpha < 255) {
            translucent = true;
          }

          if (ptu && !t) // left - transparent, right - opaque
          {
            faceData.set(Direction.WEST, u, v);
          }
          if (!ptu && t) // left - opaque, right - transparent
          {
            faceData.set(Direction.EAST, u - 1, v);
          }
          if (ptv[u] && !t) // up - transparent, down - opaque
          {
            faceData.set(Direction.UP, u, v);
          }
          if (!ptv[u] && t) // up - opaque, down - transparent
          {
            faceData.set(Direction.DOWN, u, v - 1);
          }

          ptu = t;
          ptv[u] = t;
        }
        if (!ptu) // last - opaque
        {
          faceData.set(Direction.EAST, uMax - 1, v);
        }
      }
      // last line
      for (int u = 0; u < uMax; u++) {
        if (!ptv[u]) {
          faceData.set(Direction.DOWN, u, vMax - 1);
        }
      }
    }

    // horizontal quads
    for (Direction facing : HORIZONTALS) {
      for (int v = 0; v < vMax; v++) {
        int uStart = 0, uEnd = uMax;
        boolean building = false;
        for (int u = 0; u < uMax; u++) {
          boolean face = faceData.get(facing, u, v);
          if (!translucent) {
            if (face) {
              if (!building) {
                building = true;
                uStart = u;
              }
              uEnd = u + 1;
            }
          } else {
            if (building && !face) // finish current quad
            {
              // make quad [uStart, u]
              int off = facing == Direction.DOWN ? 1 : 0;
              builder.add(buildSideQuad(transform, facing, tint, sprite, uStart, v + off, u - uStart, fullbright));
              building = false;
            } else if (!building && face) // start new quad
            {
              building = true;
              uStart = u;
            }
          }
        }
        if (building) // build remaining quad
        {
          // make quad [uStart, uEnd]
          int off = facing == Direction.DOWN ? 1 : 0;
          builder.add(buildSideQuad(transform, facing, tint, sprite, uStart, v + off, uEnd - uStart, fullbright));
        }
      }
    }

    // vertical quads
    for (Direction facing : VERTICALS) {
      for (int u = 0; u < uMax; u++) {
        int vStart = 0, vEnd = vMax;
        boolean building = false;
        for (int v = 0; v < vMax; v++) {
          boolean face = faceData.get(facing, u, v);
          if (!translucent) {
            if (face) {
              if (!building) {
                building = true;
                vStart = v;
              }
              vEnd = v + 1;
            }
          } else {
            if (building && !face) // finish current quad
            {
              // make quad [vStart, v]
              int off = facing == Direction.EAST ? 1 : 0;
              builder.add(buildSideQuad(transform, facing, tint, sprite, u + off, vStart, v - vStart, fullbright));
              building = false;
            } else if (!building && face) // start new quad
            {
              building = true;
              vStart = v;
            }
          }
        }
        if (building) // build remaining quad
        {
          // make quad [vStart, vEnd]
          int off = facing == Direction.EAST ? 1 : 0;
          builder.add(buildSideQuad(transform, facing, tint, sprite, u + off, vStart, vEnd - vStart, fullbright));
        }
      }
    }

    // front
    builder.add(buildQuad(transform, Direction.NORTH, sprite, tint, fullbright,
      0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
      0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(),
      1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
      1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()
    ));
    // back
    builder.add(buildQuad(transform, Direction.SOUTH, sprite, tint, fullbright,
      0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
      1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(),
      1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
      0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()
    ));

    return builder.build();
  }

  private static BakedQuad buildSideQuad(AffineTransformation transform, Direction side, int tint, Sprite sprite, int u, int v, int size, boolean fullbright) {
    final float eps = 1e-2f;

    int width = sprite.getWidth();
    int height = sprite.getHeight();

    float x0 = (float) u / width;
    float y0 = (float) v / height;
    float x1 = x0, y1 = y0;
    float z0 = 7.5f / 16f, z1 = 8.5f / 16f;

    switch (side) {
      case WEST:
        z0 = 8.5f / 16f;
        z1 = 7.5f / 16f;
      case EAST:
        y1 = (float) (v + size) / height;
        break;
      case DOWN:
        z0 = 8.5f / 16f;
        z1 = 7.5f / 16f;
      case UP:
        x1 = (float) (u + size) / width;
        break;
      default:
        throw new IllegalArgumentException("can't handle z-oriented side");
    }

    float dx = side.getVector().getX() * eps / width;
    float dy = side.getVector().getY() * eps / height;

    float u0 = 16f * (x0 - dx);
    float u1 = 16f * (x1 - dx);
    float v0 = 16f * (1f - y0 - dy);
    float v1 = 16f * (1f - y1 - dy);

    return buildQuad(
      transform, remap(side), sprite, tint, fullbright,
      x0, y0, z0, sprite.getFrameU(u0), sprite.getFrameV(v0),
      x1, y1, z0, sprite.getFrameU(u1), sprite.getFrameV(v1),
      x1, y1, z1, sprite.getFrameU(u1), sprite.getFrameV(v1),
      x0, y0, z1, sprite.getFrameU(u0), sprite.getFrameV(v0)
    );
  }

  private static BakedQuad buildQuad(AffineTransformation transform, Direction side, Sprite sprite, int tint, boolean fullbright,
                                     float x0, float y0, float z0, float u0, float v0,
                                     float x1, float y1, float z1, float u1, float v1,
                                     float x2, float y2, float z2, float u2, float v2,
                                     float x3, float y3, float z3, float u3, float v3) {
    BakedQuadBuilder builder = new BakedQuadBuilder(sprite);

    builder.setQuadTint(tint);
    builder.setQuadOrientation(side);
    builder.setApplyDiffuseLighting(false);

//    boolean hasTransform = !transform.equals(AffineTransformation.identity());
    BakedQuadBuilder consumer = builder;

    int uLight, vLight;
    uLight = vLight = fullbright ? 15 : 0;

    putVertex(consumer, side, x0, y0, z0, u0, v0, uLight, vLight);
    putVertex(consumer, side, x1, y1, z1, u1, v1, uLight, vLight);
    putVertex(consumer, side, x2, y2, z2, u2, v2, uLight, vLight);
    putVertex(consumer, side, x3, y3, z3, u3, v3, uLight, vLight);

    return builder.build();
  }

  private static void putVertex(BakedQuadBuilder consumer, Direction side, float x, float y, float z, float u, float v, int uLight, int vLight) {
    VertexFormat format = consumer.getVertexFormat();
    for (int e = 0; e < format.getElements().size(); e++) {
      VertexFormatElement element = format.getElements().get(e);
      outer:
      switch (element.getType()) {
        case POSITION:
          consumer.put(e, x, y, z, 1f);
          break;
        case COLOR:
          consumer.put(e, 1f, 1f, 1f, 1f);
          break;
        case NORMAL:
          float offX = (float) side.getOffsetX();
          float offY = (float) side.getOffsetY();
          float offZ = (float) side.getOffsetZ();
          consumer.put(e, offX, offY, offZ, 0f);
          break;
        case UV:
          switch (element.getIndex()) {
            case 0:
              consumer.put(e, u, v, 0f, 1f);
              break outer;
            case 2:
              consumer.put(e, (uLight << 4) / 32768.0f, (vLight << 4) / 32768.0f, 0, 1);
              break outer;
          }
          // else fallthrough to default
        default:
          consumer.put(e);
          break;
      }
    }
  }

  private static Direction remap(Direction side) {
    // getOpposite is related to the swapping of V direction
    return side.getAxis() == Direction.Axis.Y ? side.getOpposite() : side;
  }

  private static class FaceData {

    private final EnumMap<Direction, BitSet> data = new EnumMap<>(Direction.class);

    private final int vMax;

    FaceData(int uMax, int vMax) {
      this.vMax = vMax;

      data.put(Direction.WEST, new BitSet(uMax * vMax));
      data.put(Direction.EAST, new BitSet(uMax * vMax));
      data.put(Direction.UP, new BitSet(uMax * vMax));
      data.put(Direction.DOWN, new BitSet(uMax * vMax));
    }

    public void set(Direction facing, int u, int v) {
      data.get(facing).set(getIndex(u, v));
    }

    public boolean get(Direction facing, int u, int v) {
      return data.get(facing).get(getIndex(u, v));
    }

    private int getIndex(int u, int v) {
      return v * vMax + u;
    }
  }
}
