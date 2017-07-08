package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import javax.vecmath.Vector3f;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;

/**
 * This model contains all modifiers for a tool Note that handling may seem confusing, because modifier textures are
 * loaded on a per-modifier basis, but are translated to a per-tool basis during loading.
 */
public class ModifierModel implements IModel {

  private Map<String, String> models = new THashMap<>();

  public ModifierModel() {
  }

  public void addModelForModifier(String modifier, String texture) {
    models.put(modifier, texture);
  }

  public Map<String, String> getModels() {
    return models;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return ImmutableList.of(); // none
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    for(String texture : models.values()) {
      builder.add(new ResourceLocation(texture));
    }

    return builder.build();
  }

  @Override
  public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    throw new UnsupportedOperationException("The modifier-Model is not built to be used as an item model");
  }

  public Map<String, IBakedModel> bakeModels(IModelState state, VertexFormat format,
                                             Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    Map<String, IBakedModel> bakedModels = new THashMap<>();

    // we scale the modifier up slightly so it's always above the tool
    float s = 0.025f;
    ITransformation transformation = new TRSRTransformation(new Vector3f(0, 0, 0.0001f - s / 2f), null, new Vector3f(1, 1, 1f + s), null);

    for(Map.Entry<String, String> entry : models.entrySet()) {
      // todo: turn this into an event?
      // check if the corresponding modifier needs this to be a material model
      // if this check ever causes an NPE then a modifier has been removed between model loading and model baking
      IModifier modifier = TinkerRegistry.getModifier(entry.getKey());
      if(modifier != null && modifier.hasTexturePerMaterial()) {
        MaterialModel materialModel = new MaterialModel(ImmutableList.of(new ResourceLocation(entry.getValue())));
        BakedMaterialModel bakedModel = materialModel.bakeIt(state, format, bakedTextureGetter);
        for(Material material : TinkerRegistry.getAllMaterials()) {
          IBakedModel materialBakedModel = bakedModel.getModelByIdentifier(material.getIdentifier());
          if(materialBakedModel != bakedModel) {
            bakedModels.put(entry.getKey() + material.getIdentifier(), materialBakedModel);
          }
        }
      }
      else {
        //ItemCameraTransforms transforms = new ItemCameraTransforms(modelBlock.getThirdPersonTransform(), modelBlock.getFirstPersonTransform(), modelBlock.getHeadTransform(), modelBlock.getInGuiTransform());
        //IPerspectiveState perspectiveState = new IPerspectiveState.Impl(state, transforms);
        IModel model = ItemLayerModel.INSTANCE.retexture(ImmutableMap.of("layer0", entry.getValue()));
        IBakedModel bakedModel = model.bake(state, format, bakedTextureGetter);
        bakedModels.put(entry.getKey(), bakedModel);
      }
    }

    return bakedModels;
  }

  @Override
  public IModelState getDefaultState() {
    return TRSRTransformation.identity();
  }
}
