package tconstruct.test;

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
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;

public class MultiModel implements IModel {
  private static final FaceBakery faceBakery = new FaceBakery();

  // the modelblock is needed for the layer information
  private ModelBlock modelBlock;
  // the original model is required for the vertex data
  private final IModel model;

  private final List<ModelBlock> partBlocks;

  public MultiModel(ModelBlock modelBlock, IModel model, List<ModelBlock> parts) {
    this.modelBlock = modelBlock;
    this.model = model;
    this.partBlocks = parts;
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

    for(String s : (List<String>) ItemModelGenerator.LAYERS) {
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
    ItemModelGenerator generator = new ItemModelGenerator();

    modelBlock = generator.makeItemModel(Minecraft.getMinecraft().getTextureMapBlocks(), modelBlock);

    // we need the original model for the processed vertex information
    IFlexibleBakedModel original = model.bake(state, Attributes.DEFAULT_BAKED_FORMAT, bakedTextureGetter);

    IFlexibleBakedModel[] partModels = new IFlexibleBakedModel[partBlocks.size()];
    int i = 0;

    // we build simple models for the parts, so we can extract the UV information
    for(ModelBlock mb : partBlocks) {
      mb = generator.makeItemModel(Minecraft.getMinecraft().getTextureMapBlocks(), mb);
      SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(mb));
      TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(mb.resolveTextureName("layer0")));
      builder.setTexture(sprite);

      for (Object o : mb.getElements()) {
        BlockPart blockpart = (BlockPart) o;
        for(Object o2 : blockpart.mapFaces.keySet()) {
          EnumFacing enumfacing = (EnumFacing)o2;
          BlockPartFace blockpartface = (BlockPartFace)blockpart.mapFaces.get(enumfacing);
          builder.addGeneralQuad(this.makeBakedQuad(blockpart, blockpartface, sprite, enumfacing, ModelRotation.X0_Y0, false));
        }
      }

      partModels[i++] = new IFlexibleBakedModel.Wrapper(builder.makeBakedModel(), Attributes.DEFAULT_BAKED_FORMAT);
    }

    ItemCameraTransforms transforms = new ItemCameraTransforms(modelBlock.getThirdPersonTransform(), modelBlock.getFirstPersonTransform(), modelBlock.getHeadTransform(), modelBlock.getInGuiTransform());
    BakedMultiModel bakedModel = new BakedMultiModel(transforms, original, partModels);

	// TODO: Remove
    for(Material material : TinkerRegistry.getAllMaterials()) {
      bakedModel.addTexture(material.identifier + "_head", CustomTextureCreator.sprites.get(
          "pick_head_" + material.identifier));
      bakedModel.addTexture(material.identifier + "_handle", CustomTextureCreator.sprites.get("pick_handle_" + material.identifier));
    }

    return bakedModel;
  }

  @Override
  public IModelState getDefaultState() {
    return ModelRotation.X0_Y0;
  }

  private BakedQuad makeBakedQuad(BlockPart p_177589_1_, BlockPartFace p_177589_2_, TextureAtlasSprite p_177589_3_, EnumFacing p_177589_4_, net.minecraftforge.client.model.ITransformation p_177589_5_, boolean p_177589_6_)
  {
    return faceBakery.makeBakedQuad(p_177589_1_.positionFrom, p_177589_1_.positionTo, p_177589_2_, p_177589_3_, p_177589_4_, p_177589_5_, p_177589_1_.partRotation, p_177589_6_, p_177589_1_.shade);
  }
}
