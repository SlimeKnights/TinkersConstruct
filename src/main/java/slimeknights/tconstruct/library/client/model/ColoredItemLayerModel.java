package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Clone of {@link ItemLayerModel} to propagate a hardcoded color in, allows reducing rendering time by bypassing item colors for a static color
 */
@RequiredArgsConstructor
public class ColoredItemLayerModel implements IModelGeometry<ColoredItemLayerModel> {
  /** Model loader instance */
  public static final Loader LOADER = new Loader();

  private static final Direction[] HORIZONTALS = {Direction.UP, Direction.DOWN};
  private static final Direction[] VERTICALS = {Direction.WEST, Direction.EAST};

  /** Layers in the model */
  private final List<LayerData> layers;
  /** Textures fetched during baking */
  private List<RenderMaterial> textures = Collections.emptyList();

  /** Gets the layer at the given index */
  private LayerData getLayer(int index) {
    if (index < 0 || index >= layers.size()) {
      return LayerData.DEFAULT;
    }
    return layers.get(index);
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    ImmutableList.Builder<RenderMaterial> builder = ImmutableList.builder();
    for (int i = 0; owner.isTexturePresent("layer" + i); i++) {
      builder.add(owner.resolveTexture("layer" + i));
    }
    textures = builder.build();
    return textures;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    // determine particle texture
    TextureAtlasSprite particle = spriteGetter.apply(owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : textures.get(0));
    // bake in special properties
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
    TransformationMatrix transform = modelTransform.getRotation();
    for(int i = 0; i < textures.size(); i++) {
      TextureAtlasSprite sprite = spriteGetter.apply(textures.get(i));
      LayerData data = getLayer(i);
      builder.addAll(getQuadsForSprite(data.getColor(), data.isNoTint() ? -1 : i, sprite, transform, data.getLuminosity()));
    }
    // transform data
    ImmutableMap<TransformType,TransformationMatrix> transformMap = PerspectiveMapWrapper.getTransforms(new ModelTransformComposition(owner.getCombinedTransform(), modelTransform));
    return new BakedItemModel(builder.build(), particle, Maps.immutableEnumMap(transformMap), overrides, true, owner.isSideLit());
  }

  /**
   * Gets all quads for an item layer for the given sprite
   * @param color       Color for the sprite in AARRGGBB format.
   * @param tint        Tint index for {@link net.minecraft.client.renderer.color.BlockColors} and {@link net.minecraft.client.renderer.color.ItemColors}. Generally unused
   * @param sprite      Sprite to convert into quads
   * @param transform   Transforms to apply
   * @param luminosity  Extra light to add to the quad from 0-15, makes it appear to glow a bit
   * @return  List of baked quads
   */
  public static ImmutableList<BakedQuad> getQuadsForSprite(int color, int tint, TextureAtlasSprite sprite, TransformationMatrix transform, int luminosity) {
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

    int uMax = sprite.getWidth();
    int vMax = sprite.getHeight();
    FaceData faceData = new FaceData(uMax, vMax);
    boolean translucent = false;

    for(int f = 0; f < sprite.getFrameCount(); f++) {
      boolean ptu;
      boolean[] ptv = new boolean[uMax];
      Arrays.fill(ptv, true);
      for(int v = 0; v < vMax; v++) {
        ptu = true;
        for(int u = 0; u < uMax; u++) {
          int alpha = sprite.getPixelRGBA(f, u, vMax - v - 1) >> 24 & 0xFF;
          boolean t = alpha / 255f <= 0.1f;

          if (!t && alpha < 255) {
            translucent = true;
          }

          if(ptu && !t) { // left - transparent, right - opaque
            faceData.set(Direction.WEST, u, v);
          }
          if(!ptu && t) { // left - opaque, right - transparent
            faceData.set(Direction.EAST, u-1, v);
          }
          if(ptv[u] && !t) { // up - transparent, down - opaque
            faceData.set(Direction.UP, u, v);
          }
          if(!ptv[u] && t) { // up - opaque, down - transparent
            faceData.set(Direction.DOWN, u, v-1);
          }

          ptu = t;
          ptv[u] = t;
        }
        if(!ptu) { // last - opaque
          faceData.set(Direction.EAST, uMax-1, v);
        }
      }
      // last line
      for(int u = 0; u < uMax; u++) {
        if(!ptv[u]) {
          faceData.set(Direction.DOWN, u, vMax-1);
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
            if (building && !face) { // finish current quad
              // make quad [uStart, u]
              int off = facing == Direction.DOWN ? 1 : 0;
              builder.add(buildSideQuad(transform, facing, color, tint, sprite, uStart, v+off, u-uStart, luminosity));
              building = false;
            } else if (!building && face) { // start new quad
              building = true;
              uStart = u;
            }
          }
        }
        if (building) { // build remaining quad
          // make quad [uStart, uEnd]
          int off = facing == Direction.DOWN ? 1 : 0;
          builder.add(buildSideQuad(transform, facing, color, tint, sprite, uStart, v+off, uEnd-uStart, luminosity));
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
            if (building && !face) { // finish current quad
              // make quad [vStart, v]
              int off = facing == Direction.EAST ? 1 : 0;
              builder.add(buildSideQuad(transform, facing, color, tint, sprite, u+off, vStart, v-vStart, luminosity));
              building = false;
            } else if (!building && face) { // start new quad
              building = true;
              vStart = v;
            }
          }
        }
        if (building) { // build remaining quad
          // make quad [vStart, vEnd]
          int off = facing == Direction.EAST ? 1 : 0;
          builder.add(buildSideQuad(transform, facing, color, tint, sprite, u+off, vStart, vEnd-vStart, luminosity));
        }
      }
    }

    // front
    builder.add(buildQuad(transform, Direction.NORTH, sprite, color, tint, luminosity,
                          0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
                          0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(),
                          1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
                          1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()
                         ));
    // back
    builder.add(buildQuad(transform, Direction.SOUTH, sprite, color, tint, luminosity,
                          0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
                          1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(),
                          1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
                          0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()
                         ));

    return builder.build();
  }

  /**
   * Builds a single quad on the side of the sprite
   * @param transform  Transforms to apply
   * @param side       Side to build
   * @param color      Color for the sprite
   * @param tint       Tint index for {@link net.minecraft.client.renderer.color.BlockColors} and {@link net.minecraft.client.renderer.color.ItemColors}
   * @param sprite     Sprite to render
   * @param u          Sprite U
   * @param v          Sprite V
   * @param size       Size of the quad in the correct direction (depth is always 1 pixel)
   * @param luminosity Extra light to add to the quad between 0 and 15
   * @return  Baked quad
   */
  @SuppressWarnings("DuplicateBranchesInSwitch")
  private static BakedQuad buildSideQuad(TransformationMatrix transform, Direction side, int color, int tint, TextureAtlasSprite sprite, int u, int v, int size, int luminosity) {
    final float eps = 1e-2f;
    int width = sprite.getWidth();
    int height = sprite.getHeight();
    float x0 = (float) u / width;
    float y0 = (float) v / height;
    float x1 = x0, y1 = y0;
    float z0 = 7.5f / 16f, z1 = 8.5f / 16f;
    switch(side) {
      case WEST:
        z0 = 8.5f / 16f;
        z1 = 7.5f / 16f;
        // continue into EAST
      case EAST:
        y1 = (float) (v + size) / height;
        break;
      case DOWN:
        z0 = 8.5f / 16f;
        z1 = 7.5f / 16f;
        // continue into UP
      case UP:
        x1 = (float) (u + size) / width;
        break;
      default:
        throw new IllegalArgumentException("can't handle z-oriented side");
    }

    // for the side, Y axis's use of getOpposite is related to the swapping of V direction
    float dx = side.getDirectionVec().getX() * eps / width;
    float dy = side.getDirectionVec().getY() * eps / height;
    float u0 = 16f * (x0 - dx);
    float u1 = 16f * (x1 - dx);
    float v0 = 16f * (1f - y0 - dy);
    float v1 = 16f * (1f - y1 - dy);
    return buildQuad(
      transform, (side.getAxis() == Axis.Y ? side.getOpposite() : side),
      sprite, color, tint, luminosity,
      x0, y0, z0, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0),
      x1, y1, z0, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
      x1, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
      x0, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0));
  }

  /**
   * Builds a single quad in the model, based on the method in {@link ItemLayerModel} but with color added
   * @param transform    Model transforms
   * @param side         Quad side
   * @param sprite       Sprite to use in the quad
   * @param color        Color for the sprite in AARRGGBB format
   * @param tint         Tint index for {@link net.minecraft.client.renderer.color.BlockColors} and {@link net.minecraft.client.renderer.color.ItemColors}
   * @param luminosity Extra light to add to the quad between 0 and 15
   * @return  Final quad
   */
  protected static BakedQuad buildQuad(TransformationMatrix transform, Direction side, TextureAtlasSprite sprite, int color, int tint, int luminosity,
                                     float x0, float y0, float z0, float u0, float v0,
                                     float x1, float y1, float z1, float u1, float v1,
                                     float x2, float y2, float z2, float u2, float v2,
                                     float x3, float y3, float z3, float u3, float v3) {
    BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
    builder.setQuadTint(tint);
    builder.setQuadOrientation(side);
    builder.setApplyDiffuseLighting(false); // TODO: luminosity == 0?

    boolean hasTransform = !transform.isIdentity();
    IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;

    putVertex(consumer, side, x0, y0, z0, u0, v0, color, luminosity);
    putVertex(consumer, side, x1, y1, z1, u1, v1, color, luminosity);
    putVertex(consumer, side, x2, y2, z2, u2, v2, color, luminosity);
    putVertex(consumer, side, x3, y3, z3, u3, v3, color, luminosity);
    return builder.build();
  }

  /**
   * Clone of the method in {@link ItemLayerModel} with the color parameter added
   * @param consumer   Vertex consumer
   * @param side       Side for the quad
   * @param x          Quad X position
   * @param y          Quad Y position
   * @param z          Quad Z position
   * @param u          Quad texture U
   * @param v          Quad texture V
   * @param color      Quad color in AARRGGBB format
   * @param luminosity Extra light to add to the quad between 0 and 15
   */
  private static void putVertex(IVertexConsumer consumer, Direction side, float x, float y, float z, float u, float v, int color, int luminosity) {
    VertexFormat format = consumer.getVertexFormat();
    ImmutableList<VertexFormatElement> elements = format.getElements();
    int size = elements.size();
    for (int e = 0; e < size; e++) {
      VertexFormatElement element = elements.get(e);
      outer:
      switch(element.getUsage()) {
        case POSITION:
          consumer.put(e, x, y, z, 1f);
          break;
        case COLOR:
          float r = ((color >> 16) & 0xFF) / 255f;
          float g = ((color >>  8) & 0xFF) / 255f;
          float b = ((color      ) & 0xFF) / 255f;
          float a = ((color >> 24) & 0xFF) / 255f;
          consumer.put(e, r, g, b, a);
          break;
        case NORMAL:
          float offX = (float) side.getXOffset();
          float offY = (float) side.getYOffset();
          float offZ = (float) side.getZOffset();
          consumer.put(e, offX, offY, offZ, 0f);
          break;
        case UV:
          switch(element.getIndex()) {
            case 0:
              consumer.put(e, u, v, 0f, 1f);
              break outer;
            case 2:
              float light = (luminosity << 4) / 32768f;
              consumer.put(e, light, light, 0, 1);
              break outer;
          }
          // else fallthrough to default
        default:
          consumer.put(e);
          break;
      }
    }
  }

  /** Cloned from {@link ItemLayerModel}'s FaceData subclass */
  private static class FaceData {
    private final EnumMap<Direction,BitSet> data = new EnumMap<>(Direction.class);
    private final int vMax;

    FaceData(int uMax, int vMax) {
      this.vMax = vMax;

      data.put(Direction.WEST, new BitSet(uMax * vMax));
      data.put(Direction.EAST, new BitSet(uMax * vMax));
      data.put(Direction.UP,   new BitSet(uMax * vMax));
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

  /**
   * Parses a color as a string
   * @param color  Color to parse
   * @return  Parsed string
   */
  private static int parseColor(@Nullable String color) {
    if (color == null || color.isEmpty()) {
      return -1;
    }
    // two options, 6 character or 8 character, must not start with - sign
    int length = color.length();
    if (color.charAt(0) != '-') {
      try {
        // length of 8 must parse as long
        if (length == 8) {
          return (int)Long.parseLong(color, 16);
        }
        return Integer.parseInt(color, 16);
      } catch (NumberFormatException ex) {
        // NO-OP
      }
    }
    throw new JsonSyntaxException("Invalid color '" + color + "'");
  }

  /** Class holding details about a single layer in the model */
  @Data
  private static class LayerData {
    private static final LayerData DEFAULT = new LayerData(-1, 0, false);

    private final int color;
    private final int luminosity;
    private final boolean noTint;

    /** Parses the layer data from JSON */
    public static LayerData fromJson(JsonObject json) {
      int color = parseColor(JSONUtils.getString(json, "color", ""));
      int luminosity = JSONUtils.getInt(json, "luminosity");
      boolean noTint = JSONUtils.getBoolean(json, "no_tint", false);
      return new LayerData(color, luminosity, noTint);
    }
  }

  private static class Loader implements IModelLoader<ColoredItemLayerModel> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public ColoredItemLayerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      List<LayerData> layers = JsonHelper.parseList(modelContents, "layers", LayerData::fromJson);
      return new ColoredItemLayerModel(layers);
    }
  }
}
