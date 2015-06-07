package tconstruct.library.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ITransformation;
import net.minecraftforge.client.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import tconstruct.library.TinkerRegistry;

/**
 * This model contains all modifiers for a tool
 * Note that handling may seem confusing, because modifier textures are loaded on a per-modifier basis, but are
 * translated to a per-tool basis during loading.
 */
public class ModifierModel implements IModel {

  private Map<String, ModelBlock> models = new THashMap<>();

  public ModifierModel() {
  }

  public void addModelForModifier(String modifier, ModelBlock model) {
    models.put(modifier, model);
  }

  public String getTextureForModifier(String modifier) {
    if (!models.containsKey(modifier)) {
      return null;
    }

    return models.get(modifier).resolveTextureName("layer0");
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return Collections.EMPTY_LIST; // none
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    for (ModelBlock modelBlock : models.values()) {
      builder.add(new ResourceLocation(modelBlock.resolveTextureName("layer0")));
    }

    return builder.build();
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    throw new UnsupportedOperationException("The modifier-Model is not built to be used as an item model");
  }

  public Map<String, IFlexibleBakedModel> bakeModels(IModelState state, VertexFormat format,
                                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    Map<String, IFlexibleBakedModel> bakedModels = new THashMap<>();

    // we scale the modifier up slightly so it's always above the tool
    float s = 0.025f;
    ITransformation transformation = new TRSRTransformation(new Vector3f(0, 0, 0.0001f-s/2f), null, new Vector3f(1,1,1f + s), null);

    for(Map.Entry<String, ModelBlock> entry : models.entrySet()) {
      ModelBlock modelBlock = entry.getValue();

      // todo: turn this into an event?
      IFlexibleBakedModel bakedModel;
      // check if the corresponding modifier needs this to be a material model
      // if this check ever causes an NPE then a modifier has been removed between model loading and model baking
      if (TinkerRegistry.getModifier(entry.getKey()).hasTexturePerMaterial()) {
        IModel materialModel = new MaterialModel(modelBlock);
        bakedModel = materialModel.bake(state, format, bakedTextureGetter);
      } else {
        bakedModel = ModelHelper.bakeModelFromModelBlock(modelBlock, bakedTextureGetter, transformation);
      }

      bakedModels.put(entry.getKey(), bakedModel);
    }

    return bakedModels;
  }

  @Override
  public IModelState getDefaultState() {
    return ModelRotation.X0_Y0;
  }
}
