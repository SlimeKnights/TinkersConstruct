package tconstruct.library.client.model;

import com.google.common.base.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.Collection;
import java.util.Collections;

/**
 * Dummy Model to be returned on the initial load to silence the missing model messages.
 * It's never actually used and gets replaced with the real models when the resource manager reloads.
 */
public class DummyModel implements IModel {
  public static final DummyModel INSTANCE = new DummyModel();

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    return ModelLoaderRegistry.getMissingModel().bake(ModelLoaderRegistry.getMissingModel().getDefaultState(), format, bakedTextureGetter);
  }

  @Override
  public IModelState getDefaultState() {
    return ModelLoaderRegistry.getMissingModel().getDefaultState();
  }
}
