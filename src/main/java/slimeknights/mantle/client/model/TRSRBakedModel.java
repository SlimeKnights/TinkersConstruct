package slimeknights.mantle.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.TRSRTransformation;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

// for those wondering TRSR stands for Translation Rotation Scale Rotation
public class TRSRBakedModel implements IFlexibleBakedModel {

  protected final ImmutableList<BakedQuad> general;
  protected final ImmutableMap<EnumFacing, ImmutableList<BakedQuad>> faces;
  protected final IFlexibleBakedModel original;

  public TRSRBakedModel(IFlexibleBakedModel original, float x, float y, float z, float scale) {
    this(original, x, y, z, 0, 0, 0, scale, scale, scale);
  }

  public TRSRBakedModel(IFlexibleBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
    this(original, x, y, z, rotX, rotY, rotZ, scale, scale, scale);
  }

  public TRSRBakedModel(IFlexibleBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
    this(original, new TRSRTransformation(new Vector3f(x, y, z),
                                          null,
                                          new Vector3f(scaleX, scaleY, scaleZ),
                                          TRSRTransformation.quatFromYXZ(rotY, rotX, rotZ)));
  }

  public TRSRBakedModel(IFlexibleBakedModel original, TRSRTransformation transform) {
    this.original = original;

    ImmutableList.Builder<BakedQuad> builder;
    builder = ImmutableList.builder();

    transform = TRSRTransformation.blockCenterToCorner(transform);

    // face quads
    EnumMap<EnumFacing, ImmutableList<BakedQuad>> faces = Maps.newEnumMap(EnumFacing.class);
    for(EnumFacing face : EnumFacing.values()) {
      for(BakedQuad quad : original.getFaceQuads(face)) {
        builder.add(transform(transform, quad, original.getFormat()));
      }
      //faces.put(face, builder.build());
      faces.put(face, ImmutableList.<BakedQuad>of());
    }

    // general quads
    //builder = ImmutableList.builder();
    for(BakedQuad quad : original.getGeneralQuads()) {
      builder.add(transform(transform, quad, original.getFormat()));
    }

    this.general = builder.build();
    this.faces = Maps.immutableEnumMap(faces);
  }

  public static BakedQuad transform(TRSRTransformation transform, BakedQuad quad, VertexFormat format) {
    for(VertexFormatElement e : (List<VertexFormatElement>) format.getElements()) {
      if(e.getUsage() == VertexFormatElement.EnumUsage.POSITION) {
        if(e.getType() != VertexFormatElement.EnumType.FLOAT) {
          throw new IllegalArgumentException("can only transform float position");
        }
        int[] data = Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length);
        // once for each vertex
        for(int j = 0; j < 4; j++) {
          int v = j * format.getNextOffset() / 4;
          float[] pos = new float[]{0f, 0f, 0f, 1f};
          for(int i = 0; i < Math.min(4, e.getElementCount()); i++) {
            pos[i] = Float.intBitsToFloat(data[v + e.getOffset() / 4 + i]);
          }
          Vector4f vec = new Vector4f(pos);
          transform.getMatrix().transform(vec);
          vec.get(pos);
          for(int i = 0; i < Math.min(4, e.getElementCount()); i++) {
            data[v + e.getOffset() / 4 + i] = Float.floatToRawIntBits(pos[i]);
          }
        }
        return new BakedQuad(data, quad.getTintIndex(), quad.getFace());
      }
    }
    return quad;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing side) {
    return faces.get(side);
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return general;
  }

  @Override
  public boolean isAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean isGui3d() {
    return original.isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return original.isBuiltInRenderer();
  }

  @Override
  public TextureAtlasSprite getTexture() {
    return original.getTexture();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return original.getItemCameraTransforms();
  }

  @Override
  public VertexFormat getFormat() {
    return original.getFormat();
  }
}
