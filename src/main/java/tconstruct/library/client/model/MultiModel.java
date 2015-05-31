package tconstruct.library.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import tconstruct.library.client.BakedMaterialModel;
import tconstruct.library.client.BakedTinkerToolModel;
import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.client.ModelHelper;

public class MultiModel implements IModel {
  private static final FaceBakery faceBakery = new FaceBakery();

  // the modelblock is needed for the layer information
  private ModelBlock modelBlock;

  private final List<MaterialModel> partBlocks;
  private final List<MaterialModel> brokenPartBlocks;

  public MultiModel(ModelBlock modelBlock, List<MaterialModel> parts, List<MaterialModel> brokenPartBlocks) {
    this.modelBlock = modelBlock;
    this.partBlocks = parts;
    this.brokenPartBlocks = brokenPartBlocks;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    if(modelBlock.getParentLocation() == null || modelBlock.getParentLocation().getResourcePath().startsWith("builtin/")) return Collections
        .emptyList();

    return Collections.singletonList(modelBlock.getParentLocation());
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    // regular layers
    for(String s : getLayers()) {
      String r = modelBlock.resolveTextureName(s);
      ResourceLocation loc = new ResourceLocation(r);
      if (!r.equals(s)) {
        builder.add(loc);
      }
    }

    // broken state textures
    for(String s : getBrokenLayers()) {
      String r = modelBlock.resolveTextureName(s);
      ResourceLocation loc = new ResourceLocation(r);
      if (!r.equals(s)) {
        builder.add(loc);
      }
    }

    return builder.build();
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

    IFlexibleBakedModel base = ModelHelper.bakeModelFromModelBlock(modelBlock, bakedTextureGetter);

    BakedMaterialModel[] partModels = new BakedMaterialModel[partBlocks.size()];
    BakedMaterialModel[] brokenPartModels = new BakedMaterialModel[partBlocks.size()]; // has to be same size

    // we build simple models for the parts, so we can extract the UV information AND have depth
    for(int i = 0; i < partBlocks.size(); i++) {
      partModels[i] = partBlocks.get(i).bakeIt(state, format, bakedTextureGetter);
    }
    for(int i = 0; i < brokenPartBlocks.size(); i++) {
      if(brokenPartBlocks.get(i) != null)
        brokenPartModels[i] = brokenPartBlocks.get(i).bakeIt(state, format, bakedTextureGetter);
    }

    return new BakedTinkerToolModel(base, partModels, brokenPartModels);
  }

  @Override
  public IModelState getDefaultState() {
    return ModelRotation.X0_Y0;
  }

  public static String[] getLayers() {
    String[] out = new String[10];
    for(int i = 0; i < out.length; i++) {
      // regular layers
      out[i] = "layer" + i;
    }
    return out;
  }

  public static String[] getBrokenLayers() {
    String[] out = new String[10];
    for(int i = 0; i < out.length; i++) {
      // regular layers
      out[i] = "broken" + i;
    }
    return out;
  }
}
