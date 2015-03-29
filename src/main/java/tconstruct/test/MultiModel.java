package tconstruct.test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MultiModel implements IModel {
  // the modelblock is needed for the layer information
  private final ModelBlock modelBlock;
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
    // we need the original model for the processed vertex information
    IFlexibleBakedModel original = model.bake(state, Attributes.DEFAULT_BAKED_FORMAT, bakedTextureGetter);

    IFlexibleBakedModel[] partModels = new IFlexibleBakedModel[partBlocks.size()];
    int i = 0;
/*
    ItemModelGenerator generator = new ItemModelGenerator();

    // we build simple models for the parts, so we can exctract the UV information
    for(ModelBlock mb : partBlocks) {
      mb = generator.makeItemModel(Minecraft.getMinecraft().getTextureMapBlocks(), mb);
      SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(mb));
      TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(mb.resolveTextureName("layer0")));
      builder.setTexture(sprite);

      for (Object o : mb.getElements()) {
        BlockPart blockpart = (BlockPart) o;
      }

      partModels[i++] = new IFlexibleBakedModel.Wrapper(builder.makeBakedModel(), Attributes.DEFAULT_BAKED_FORMAT);
    }
*/



    return new BakedMultiModel(original, partModels);
  }

  @Override
  public IModelState getDefaultState() {
    return ModelRotation.X0_Y0;
  }
}
