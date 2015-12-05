package slimeknights.tconstruct.library.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveState;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.TRSRTransformation;

import java.util.Map;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.materials.Material;

public class MaterialModel extends ItemLayerModel {

  public MaterialModel(ImmutableList<ResourceLocation> textures) {
    super(textures);
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    return bakeIt(state, format, bakedTextureGetter);
  }

  // the only difference here is the return-type
  public BakedMaterialModel bakeIt(IModelState state, VertexFormat format,
                                   Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    // normal model as the base
    IFlexibleBakedModel base = super.bake(state, format, bakedTextureGetter);

    // turn it into a baked material-model
    BakedMaterialModel bakedMaterialModel = new BakedMaterialModel(base);

    // and generate the baked model for each material-variant we have for the base texture
    String baseTexture = base.getParticleTexture().getIconName();
    Map<String, TextureAtlasSprite> sprites = CustomTextureCreator.sprites.get(baseTexture);

    for(Map.Entry<String, TextureAtlasSprite> entry : sprites.entrySet()) {
      Material material = TinkerRegistry.getMaterial(entry.getKey());

      IModel model2 = this.retexture(ImmutableMap.of("layer0", entry.getValue().getIconName()));
      IFlexibleBakedModel bakedModel2 = model2.bake(state, format, bakedTextureGetter);

      // if it's a colored material we need to color the quads. But only if the texture was not a custom texture
      if(material.renderInfo.useVertexColoring() && !CustomTextureCreator.exists(baseTexture + "_" + material.identifier)) {
        int color = (material.renderInfo).getVertexColor();

        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
        // ItemLayerModel.BakedModel only uses general quads
        for(BakedQuad quad : bakedModel2.getGeneralQuads()) {
          quads.add(ModelHelper.colorQuad(color, quad));
        }

        // create a new model with the colored quads
        if(state instanceof IPerspectiveState) {
          IPerspectiveState ps = (IPerspectiveState) state;
          Map<ItemCameraTransforms.TransformType, TRSRTransformation> map = Maps.newHashMap();
          for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
            map.put(type, ps.forPerspective(type).apply(this));
          }
          bakedModel2 =
              new ItemLayerModel.BakedModel(quads.build(), bakedModel2.getParticleTexture(), bakedModel2.getFormat(), Maps
                  .immutableEnumMap(map));
        }
        else {
          bakedModel2 = new ItemLayerModel.BakedModel(quads.build(), bakedModel2.getParticleTexture(), bakedModel2.getFormat());
        }
      }

      bakedMaterialModel.addMaterialModel(material, bakedModel2);
    }

    return bakedMaterialModel;
  }

  @Override
  public IModelState getDefaultState() {
    return ModelHelper.DEFAULT_ITEM_STATE;
  }
}
