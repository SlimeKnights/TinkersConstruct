package tconstruct.tools.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;

import java.util.List;
import java.util.Map;

public class BakedTableModel implements ISmartBlockModel, IFlexibleBakedModel {

  private final IFlexibleBakedModel standard;
  private final IRetexturableModel tableModel;

  private final Map<String, IFlexibleBakedModel> cache = Maps.newHashMap();
  private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;

  public BakedTableModel(IFlexibleBakedModel standard, IRetexturableModel tableModel) {
    this.standard = standard;
    this.tableModel = tableModel;

    textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
      public TextureAtlasSprite apply(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
      }
    };
  }

  @Override
  public IBakedModel handleBlockState(IBlockState state) {
    // get texture from state
    String texture = null; // todo: get texture from state

    if(texture == null) {
      return standard;
    }

    if(cache.containsKey(texture)) {
      return cache.get(texture);
    }

    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    builder.put("bottom", texture);
    builder.put("leg", texture);
    builder.put("legBottom", texture);
    IModel retexturedModel = tableModel.retexture(builder.build());

    IFlexibleBakedModel
        bakedModel =
        retexturedModel.bake(retexturedModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);

    cache.put(texture, bakedModel);

    return bakedModel;
  }

  @Override
  public List getFaceQuads(EnumFacing p_177551_1_) {
    return standard.getFaceQuads(p_177551_1_);
  }

  @Override
  public List getGeneralQuads() {
    return standard.getGeneralQuads();
  }

  @Override
  public VertexFormat getFormat() {
    return standard.getFormat();
  }

  @Override
  public boolean isAmbientOcclusion() {
    return standard.isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return standard.isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return standard.isBuiltInRenderer();
  }

  @Override
  public TextureAtlasSprite getTexture() {
    return standard.getTexture();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return standard.getItemCameraTransforms();
  }
}
