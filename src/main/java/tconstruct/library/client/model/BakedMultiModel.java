package tconstruct.library.client.model;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class BakedMultiModel extends SimpleBakedModel implements ISmartItemModel, IFlexibleBakedModel {
  protected final List<List<BakedQuadUV>> subModels;

  private static final List<List<BakedQuad>> empty_face_quads;
  private static final List<BakedQuad> empty_list;
  static {
    empty_list = Collections.emptyList();
    empty_face_quads = Lists.newArrayList();
    for(int i = 0; i < 6; i++)
      empty_face_quads.add(empty_list);
  }

  public BakedMultiModel(ItemCameraTransforms transforms, IFlexibleBakedModel original, IFlexibleBakedModel... models) {
    super(null, null, original.isAmbientOcclusion(), false, original.getTexture(), transforms);

    subModels = Lists.newArrayList();

    int i = 0;
    // add the quads from the models and extract the UVs
    for(IFlexibleBakedModel model : models) {
      // Items usually don't have face-quads, so we only use the general quads
      List<BakedQuad> quads = model.getGeneralQuads();
      TextureAtlasSprite sprite = model.getTexture();
      List<BakedQuadUV> uvQuads = Lists.newArrayList();

      i++;

      // generate the normalized UV data
      for(BakedQuad quad : quads) {
        uvQuads.add(new BakedQuadUV(quad, sprite));
      }

      subModels.add(uvQuads);
    }
  }

  /**
   * Generates the finished model with the given textures. Amount of textures should be the same as amount of submodels.
   * @param textures The textures to use for the baked model. One for each submodel.
   * @return The baked model.
   */
  protected IBakedModel bakeModel(TextureAtlasSprite... textures) {
    List<BakedQuad> quads = Lists.newArrayList();

    String[] texs = new String[] {"Wood_head", "Stone_handle", "default_2"};

    for(int i = 0; i < subModels.size(); i++) {
      if(textures.length < i || textures[i] == null)
        break;

      List<BakedQuadUV> modelQuads = subModels.get(i);
      TextureAtlasSprite modelTexture = textures[i];
      for(BakedQuadUV quadUV : modelQuads) {
        quads.add(quadUV.applyTexture(modelTexture));
      }
    }

    SimpleBakedModel
        model = new SimpleBakedModel(quads, empty_face_quads, this.isAmbientOcclusion(), this.isGui3d(), this.getTexture(), this.getItemCameraTransforms());
    // todo: cache
    return model;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
    return empty_list;
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return empty_list;
  }

  @Override
  public VertexFormat getFormat() {
    return null;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }
}
