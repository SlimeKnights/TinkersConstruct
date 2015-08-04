package tconstruct.library.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.TRSRTransformation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ToolModel extends ItemLayerModel {

  private final List<MaterialModel> partBlocks;
  private final List<MaterialModel> brokenPartBlocks;
  private final ModifierModel modifiers;

  public ToolModel(ImmutableList<ResourceLocation> defaultTextures, List<MaterialModel> parts, List<MaterialModel> brokenPartBlocks,
                   ModifierModel modifiers) {
    super(defaultTextures);
    this.partBlocks = parts;
    this.brokenPartBlocks = brokenPartBlocks;
    this.modifiers = modifiers;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return ImmutableList.of();
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    builder.addAll(super.getTextures());

    // modifier textures
    if(modifiers != null) {
      builder.addAll(modifiers.getTextures());
    }

    return builder.build();
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    IFlexibleBakedModel base = super.bake(state, format, bakedTextureGetter);

    BakedMaterialModel[] partModels = new BakedMaterialModel[partBlocks.size()];
    BakedMaterialModel[] brokenPartModels = new BakedMaterialModel[partBlocks.size()]; // has to be same size

    // we build simple models for the parts, so we can extract the UV information AND have depth
    for(int i = 0; i < partBlocks.size(); i++) {
      MaterialModel m = partBlocks.get(i);
      partModels[i] = m.bakeIt(state, format, bakedTextureGetter);
    }
    for(int i = 0; i < brokenPartBlocks.size(); i++) {
      if(brokenPartBlocks.get(i) != null) {
        brokenPartModels[i] = brokenPartBlocks.get(i).bakeIt(state, format, bakedTextureGetter);
      }
    }

    Map<String, IFlexibleBakedModel> modifierModels;
    if(modifiers != null) {
      modifierModels = modifiers.bakeModels(state, format, bakedTextureGetter);
    }
    else {
      modifierModels = new THashMap<>();
    }

    ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms = ModelHelper.getTransformsFromState(state, this);

    return new BakedToolModel(base, partModels, brokenPartModels, modifierModels, transforms);
  }

  @Override
  public IModelState getDefaultState() {
    return ModelHelper.DEFAULT_TOOL_STATE;
  }
}
