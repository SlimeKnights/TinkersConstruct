package tconstruct.debug;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.ModelRegistryHelper;
import codechicken.lib.render.Vertex5;
import tconstruct.Util;

public class TestModel implements ISmartItemModel {
  List<BakedQuad> generalQuads;
  List<List<BakedQuad>> faceQuads;
  ItemCameraTransforms transforms;

  TextureAtlasSprite sprite;

  public TestModel(TextureAtlasSprite sprite) {
    Map<String, CCModel>
        model = CCModel.parseObjModels(new ResourceLocation(Util.resource("models/test.obj")));

    CCModel m = model.values().iterator().next();

    m.computeNormals();
    FaceBakery bakery = new FaceBakery();

    model.containsKey("Plane");

    TextureAtlasSprite texture = sprite;
    List<BakedQuad> quads = new ArrayList<>();
    float u = texture.getMinU();
    float v = texture.getMinV();
    float ud = (texture.getMaxU() - texture.getMinU());
    float vd = (texture.getMaxV() - texture.getMinV());

    for(int i = 0; i < m.getVertices().length; i += 4)
    {
      int[] data = new int[28];
      for(int j = 0; j < 4; j++)
      {
        Vertex5 vertex = m.getVertices()[j];
        data[j*7 + 0] = Float.floatToRawIntBits((float)vertex.vec.x);
        data[j*7 + 1] = Float.floatToRawIntBits((float)vertex.vec.y);
        data[j*7 + 2] = Float.floatToRawIntBits((float)vertex.vec.z);
        data[j*7 + 3] = -1; //color
        // uv
        //data[j*7 + 4] = Float.floatToRawIntBits((float)(u + ud*vertex.uv.u));
        //data[j*7 + 5] = Float.floatToRawIntBits((float)(v + vd*vertex.uv.v));
        data[j*7 + 4] = Float.floatToRawIntBits((float)(u + ud*(j&1)));
        data[j*7 + 5] = Float.floatToRawIntBits((float)(v + vd*(j&2)));
        data[j*7 + 6] = 0; // seems to be unused
      }
      // -1 is the tintIndex, i suppose this should be used in conjunction with the color-value of the vertex
      // EnumFacing is the way the quads normal faces. i'm using this for generalQuads so it shouldn't matter
      // otherwise you'd have to calculate the facing from the normals here I guess
      quads.add(new BakedQuad(data, -1, EnumFacing.UP));
    }

    generalQuads = quads;
    this.sprite = sprite;
  }

  // this allows us to do custom magic on the model
  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    return this;
  }

  @Override
  public List getFaceQuads(EnumFacing p_177551_1_) {
    // quads facing the given side
    return new LinkedList<BakedQuad>();
  }

  @Override
  public List getGeneralQuads() {
    return generalQuads;
/*
    CCModel m = CCModel.newModel(4, 24);
    m.generateBlock(0, 0, 0, 0, 1, 1, 1);
    TextureAtlasSprite texture = sprite;
    List<BakedQuad> quads = new ArrayList<>();
    float u = texture.getMinU();
    float v = texture.getMinV();
    float ud = (texture.getMaxU() - texture.getMinU());
    float vd = (texture.getMaxV() - texture.getMinV());

    for(int i = 0; i < m.getVertices().length; i += 4)
    {
      int[] data = new int[28];
      for(int j = 0; j < 4; j++)
      {

        Vertex5 vertex = m.getVertices()[i+j];
        data[j*7 + 0] = Float.floatToRawIntBits((float)vertex.vec.x);
        data[j*7 + 1] = Float.floatToRawIntBits((float)vertex.vec.y);
        data[j*7 + 2] = Float.floatToRawIntBits((float)vertex.vec.z);
        data[j*7 + 3] = -1; //color
        // uv
        data[j*7 + 4] = Float.floatToRawIntBits((float)(u + ud*vertex.uv.u));
        data[j*7 + 5] = Float.floatToRawIntBits((float)(v + vd*vertex.uv.v));
        data[j*7 + 6] = 0; // seems to be unused
      }
      // -1 is the tintIndex, i suppose this should be used in conjunction with the color-value of the vertex
      // EnumFacing is the way the quads normal faces. i'm using this for generalQuads so it shouldn't matter
      // otherwise you'd have to calculate the facing from the normals here I guess
      quads.add(new BakedQuad(data, -1, EnumFacing.EAST));
    }
    return quads;
/*
    // general quads that don't face a side and therefore are never culled
    //return generalQuads;
    List<BakedQuad> out = new LinkedList<>();

    FaceBakery faceBakery = new FaceBakery();
    BlockFaceUV uvs = new BlockFaceUV(new float[] {0, 0, 16, 16}, 0);
    BlockPartFace blockPartFace = new BlockPartFace(EnumFacing.UP, -1, "", uvs);
    ModelRotation modelRotation = ModelRotation.getModelRotation(0, 0);
    BakedQuad quad = faceBakery.makeBakedQuad(new Vector3f(0 , 00, 0), new Vector3f(8f, 16f, 8f),
                                      blockPartFace, sprite, EnumFacing.UP, modelRotation, null,
                                      false, false);

    out.add(quad);*//*
    List<BakedQuad> out = new LinkedList<>();
    int[] data = new int[21]; // 7 indices per vertex

    int zero = Float.floatToRawIntBits(0.0f);
    int one = Float.floatToRawIntBits(1.0f);

    int minu = Float.floatToRawIntBits(sprite.getMinU());
    int minv = Float.floatToRawIntBits(sprite.getMinV());
    int maxu = Float.floatToRawIntBits(sprite.getMaxU());
    int maxv = Float.floatToRawIntBits(sprite.getMaxV());

    data[0] = zero;
    data[1] = zero;
    data[2] = zero;
    data[3] = -1;
    data[4] = minu;
    data[5] = minv;

    data[0+7] = zero;
    data[1+7] = one;
    data[2+7] = zero;
    data[3+7] = -1;
    data[4+7] = minu;
    data[5+7] = maxv;

    data[0+14] = one;
    data[1+14] = one;
    data[2+14] = zero;
    data[3+14] = -1;
    data[4+14] = maxu;
    data[5+14] = maxv;
/*
    data[0+21] = one;
    data[1+21] = zero;
    data[2+21] = zero;
    data[3+21] = -1;
    data[4+21] = maxu;
    data[5+21] = minv;
*/
//    out.add(new BakedQuad(data, -1, EnumFacing.UP));
/*
    data = new int[28]; // 7 indices per vertex
    data[0] = zero;
    data[1] = zero;
    data[2] = one;
    data[3] = -1;
    data[4] = minu;
    data[5] = minv;

    data[0+7] = zero;
    data[1+7] = one;
    data[2+7] = one;
    data[3+7] = -1;
    data[4+7] = minu;
    data[5+7] = maxv;

    data[0+14] = one;
    data[1+14] = one;
    data[2+14] = one;
    data[3+14] = -1;
    data[4+14] = maxu;
    data[5+14] = maxv;

    data[0+21] = one;
    data[1+21] = zero;
    data[2+21] = one;
    data[3+21] = -1;
    data[4+21] = maxu;
    data[5+21] = minv;

    out.add(new BakedQuad(data, -1, EnumFacing.UP));*/
    //return out;
  }

  @Override
  public boolean isAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean isGui3d() {
    return false;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getTexture() {
    return sprite;
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return new ItemCameraTransforms(
        new ItemTransformVec3f(new Vector3f(0,0,0), new Vector3f(0,0.5f, -0.5f), new Vector3f(0.85f,0.85f,0.85f)),
        new ItemTransformVec3f(new Vector3f(0,0,0), new Vector3f(1,0.4f,-0.2f), new Vector3f(0.7f,0.7f,0.7f)),
        new ItemTransformVec3f(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0,0,0)),
        new ItemTransformVec3f(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0,0,0))
    );
    //return ItemCameraTransforms.DEFAULT;
  }
}
