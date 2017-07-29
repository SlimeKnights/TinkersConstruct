package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

/**
 * Dummy Model to be returned on the initial load to silence the missing model messages.
 * It's never actually used and gets replaced with the real models when the resource manager reloads.
 */
public class DummyModel implements IModel {

  public static final DummyModel INSTANCE = new DummyModel();

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return ImmutableList.of();
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    return ImmutableList.of();
  }

  @Override
  public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    return ModelLoaderRegistry.getMissingModel().bake(ModelLoaderRegistry.getMissingModel().getDefaultState(), format, bakedTextureGetter);
  }

  @Override
  public IModelState getDefaultState() {
    return ModelLoaderRegistry.getMissingModel().getDefaultState();
  }
}
