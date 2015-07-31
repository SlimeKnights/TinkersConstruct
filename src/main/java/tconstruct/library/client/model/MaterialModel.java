package tconstruct.library.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveState;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.client.MaterialRenderInfo;
import tconstruct.library.materials.Material;

public class MaterialModel implements IModel {

  private final ModelBlock model;

  public MaterialModel(ModelBlock model) {
    this.model = model;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    if(model.getParentLocation() == null || model.getParentLocation().getResourcePath().startsWith("builtin/")) {
      return Collections
          .emptyList();
    }
    return Collections.singletonList(model.getParentLocation());
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    // setting parent here to make textures resolve properly
    model.parent = ModelHelper.DEFAULT_PARENT;

    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    // item-model. should be the standard.
    if(model.getRootModel().name.equals("generation marker")) {
      for(String s : (List<String>) ItemModelGenerator.LAYERS) {
        String r = model.resolveTextureName(s);
        ResourceLocation loc = new ResourceLocation(r);
        if(!r.equals(s)) {
          builder.add(loc);
        }
      }
    }
    for(String s : (Iterable<String>) model.textures.values()) {
      if(!s.startsWith("#")) {
        builder.add(new ResourceLocation(s));
      }
    }
    return builder.build();
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    return bakeIt(state, format, bakedTextureGetter);
  }

  public BakedMaterialModel bakeIt(IModelState state, VertexFormat format,
                                   Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    ItemLayerModel itemModel = new ItemLayerModel(model);

    // obtain the base model with the base texture
    IFlexibleBakedModel base = itemModel.bake(state, format, bakedTextureGetter);

    // turn it into a baked material-model
    BakedMaterialModel bakedMaterialModel = new BakedMaterialModel(base);

    // and generate the baked model for each material-variant we have for the base texture
    String baseTexture = base.getTexture().getIconName();
    Map<String, TextureAtlasSprite> sprites = CustomTextureCreator.sprites.get(baseTexture);

    for(Map.Entry<String, TextureAtlasSprite> entry : sprites.entrySet()) {
      Material material = TinkerRegistry.getMaterial(entry.getKey());

      IModel model2 = itemModel.retexture(ImmutableMap.of("layer0", entry.getValue().getIconName()));
      IFlexibleBakedModel bakedModel2 = model2.bake(state, format, bakedTextureGetter);

      // if it's a colored material we need to color the quads
      if(material.renderInfo instanceof MaterialRenderInfo.Color) {
        int color = ((MaterialRenderInfo.Color) material.renderInfo).color;

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
              new ItemLayerModel.BakedModel(quads.build(), bakedModel2.getTexture(), bakedModel2.getFormat(), Maps
                  .immutableEnumMap(map));
        }
        else {
          bakedModel2 = new ItemLayerModel.BakedModel(quads.build(), bakedModel2.getTexture(), bakedModel2.getFormat());
        }
      }

      bakedMaterialModel.addMaterialModel(material, bakedModel2);
    }

    return bakedMaterialModel;
  }

  @Override
  public IModelState getDefaultState() {
    return ModelRotation.X0_Y0;
  }
}
