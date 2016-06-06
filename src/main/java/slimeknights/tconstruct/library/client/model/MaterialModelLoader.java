package slimeknights.tconstruct.library.client.model;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import java.io.IOException;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.CustomTextureCreator;

public class MaterialModelLoader implements ICustomModelLoader {

  public static String EXTENSION = ".tmat";

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelLocation.getResourcePath()
                        .endsWith(EXTENSION); // tinkermaterialmodel extension. Foo.tmat.json
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    if(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
      return DummyModel.INSTANCE;
    }

    try {
      ModelHelper.Offset offset = ModelHelper.loadOffsetFromJson(modelLocation);
      IModel model = new MaterialModel(ModelHelper.loadTextureListFromJson(modelLocation), offset.x, offset.y);

      // register the base texture for texture generation
      CustomTextureCreator.registerTextures(model.getTextures());

      return model;
    } catch(IOException e) {
      TinkerRegistry.log.error("Could not load material model {}", modelLocation.toString());
    }
    return ModelLoaderRegistry.getMissingModel();
  }

  @Override
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

  }
}
