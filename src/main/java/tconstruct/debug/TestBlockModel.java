package tconstruct.debug;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.Vertex5;

public class TestBlockModel implements ISmartBlockModel {

  private List<BakedQuad> core;
  private List<BakedQuad> up;
  private List<BakedQuad> down;
  private List<BakedQuad> north;
  private List<BakedQuad> east;
  private List<BakedQuad> south;
  private List<BakedQuad> west;
  private TextureAtlasSprite sprite;

  private List<BakedQuad> generalQuads;

  public TestBlockModel(TextureAtlasSprite sprite) {
    this.sprite = sprite;
    CCModel core = CCModel.quadModel(24);
    CCModel up = CCModel.quadModel(24);
    CCModel down = CCModel.quadModel(24);
    CCModel north = CCModel.quadModel(24);
    CCModel east = CCModel.quadModel(24);
    CCModel south = CCModel.quadModel(24);
    CCModel west = CCModel.quadModel(24);

    core.generateBlock(0, 0.25, 0.25, 0.25, 0.75, 0.75, 0.75);

    up.generateBlock(0, 0.25, 0.75, 0.25, 0.75, 1.00, 0.75);
    down.generateBlock(0, 0.25, 0.00, 0.25, 0.75, 0.25, 0.75);
    north.generateBlock(0, 0.25, 0.25, 0.00, 0.75, 0.75, 0.25);
    south.generateBlock(0, 0.25, 0.25, 0.75, 0.75, 0.75, 1.00);
    east.generateBlock(0, 0.75, 0.25, 0.25, 1.00, 0.75, 0.75);
    west.generateBlock(0, 0.00, 0.25, 0.25, 0.25, 0.75, 0.75);

    this.core = ccmodelToBakedQuads(core, sprite);

    this.up = ccmodelToBakedQuads(up, sprite);
    this.down = ccmodelToBakedQuads(down, sprite);
    this.north = ccmodelToBakedQuads(north, sprite);
    this.east = ccmodelToBakedQuads(east, sprite);
    this.south = ccmodelToBakedQuads(south, sprite);
    this.west = ccmodelToBakedQuads(west, sprite);

    generalQuads = this.core;
  }

  protected TestBlockModel(TestBlockModel parent, boolean up, boolean down, boolean north,
                           boolean east, boolean south, boolean west) {
    generalQuads = new ArrayList<>();
    // add the box in the middle
    generalQuads.addAll(parent.core);
    // add the remaining ones depending on the state
    if (up) {
      generalQuads.addAll(parent.up);
    }
    if (down) {
      generalQuads.addAll(parent.down);
    }
    if (north) {
      generalQuads.addAll(parent.north);
    }
    if (east) {
      generalQuads.addAll(parent.east);
    }
    if (south) {
      generalQuads.addAll(parent.south);
    }
    if (west) {
      generalQuads.addAll(parent.west);
    }
  }

  @Override
  public IBakedModel handleBlockState(IBlockState state) {
    boolean up = (boolean) state.getValue(TestBlock.up); // this is a generic function actually.
    boolean down = (boolean) state.getValue(TestBlock.down);
    boolean north = (boolean) state.getValue(TestBlock.north);
    boolean east = (boolean) state.getValue(TestBlock.east);
    boolean south = (boolean) state.getValue(TestBlock.south);
    boolean west = (boolean) state.getValue(TestBlock.west);

    return new TestBlockModel(this, up, down, north, east, south, west);
  }

  @Override
  public List getFaceQuads(EnumFacing p_177551_1_) {
    return new ArrayList<BakedQuad>();
  }

  @Override
  public List getGeneralQuads() {
    return generalQuads;
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
    return null;
  }

  private List<BakedQuad> ccmodelToBakedQuads(CCModel model, TextureAtlasSprite texture) {
    List<BakedQuad> quads = new ArrayList<>();
    float u = texture.getMinU();
    float v = texture.getMinV();
    float ud = (texture.getMaxU() - texture.getMinU());
    float vd = (texture.getMaxV() - texture.getMinV());

    for (int i = 0; i < model.getVertices().length; i += 4) {
      int[] data = new int[28];
      for (int j = 0; j < 4; j++) {

        Vertex5 vertex = model.getVertices()[i + j];
        data[j * 7 + 0] = Float.floatToRawIntBits((float) vertex.vec.x);
        data[j * 7 + 1] = Float.floatToRawIntBits((float) vertex.vec.y);
        data[j * 7 + 2] = Float.floatToRawIntBits((float) vertex.vec.z);
        data[j * 7 + 3] = -1; //color
        // uv
        data[j * 7 + 4] = Float.floatToRawIntBits((float) (u + ud * vertex.uv.u));
        data[j * 7 + 5] = Float.floatToRawIntBits((float) (v + vd * vertex.uv.v));
        data[j * 7 + 6] = 0; // seems to be unused
      }
      // -1 is the tintIndex, i suppose this should be used in conjunction with the color-value of the vertex
      // EnumFacing is the way the quads normal faces. i'm using this for generalQuads so it shouldn't matter
      // otherwise you'd have to calculate the facing from the normals here I guess
      quads.add(new BakedQuad(data, -1, EnumFacing.DOWN));
    }
    return quads;
  }
}
