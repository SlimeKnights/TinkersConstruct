package tconstruct.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class BakedMultiModel extends SimpleBakedModel implements ISmartItemModel, IFlexibleBakedModel {
  private final List<List<BakedQuadUV>> subModels;
  private final Map<String, TextureAtlasSprite> textures;

  private static final List<List<BakedQuad>> empty_face_quads;
  private static final List<BakedQuad> empty_list;
  static {
    empty_list = Collections.emptyList();
    empty_face_quads = Lists.newArrayList();
    for(int i = 0; i < 6; i++)
      empty_face_quads.add(empty_list);
  }

  public BakedMultiModel(IBakedModel original, IBakedModel... models) {
    super(null, null, original.isAmbientOcclusion(), original.isGui3d(), original.getTexture(), original.getItemCameraTransforms());

    subModels = Lists.newArrayList();
    textures = Maps.newHashMap();

    textures.put("default", original.getTexture());

    int i = 0;
    // add the quads from the models and extract the UVs
    for(IBakedModel model : models) {
      // Items usually don't have face-quads, so we only use the general quads
      List<BakedQuad> quads = model.getGeneralQuads();
      TextureAtlasSprite sprite = model.getTexture();
      List<BakedQuadUV> uvQuads = Lists.newArrayList();

      textures.put("default_" + i, sprite);
      i++;

      // generate the normalized UV data
      for(BakedQuad quad : quads) {
        uvQuads.add(new BakedQuadUV(quad, sprite));
      }

      subModels.add(uvQuads);
    }
  }

  public void addTexture(String name, TextureAtlasSprite texture) {
    textures.put(name, texture);
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    // todo: actually consider itemstack and/or add a hook for it

    List<BakedQuad> quads = Lists.newArrayList();

    String[] texs = new String[] {"Wood_head", "Stone_handle", "default_2"};

    int i = 0;
    for(List<BakedQuadUV> modelQuads : subModels) {
      // determine if submodel should be used and its texture
      TextureAtlasSprite modelTexture = textures.get(texs[i]);
      for(BakedQuadUV quadUV : modelQuads) {
        quads.add(quadUV.applyTexture(modelTexture));
      }
      i++;
    }

    SimpleBakedModel model = new SimpleBakedModel(quads, empty_face_quads, this.isAmbientOcclusion(), this.isGui3d(), this.getTexture(), this.getItemCameraTransforms());
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
