package tconstruct.library.client.model;

import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.io.IOException;

import tconstruct.TConstruct;
import tconstruct.library.client.CustomTextureCreator;

public class MaterialModelLoader implements ICustomModelLoader {

  public static String MATERIALMODEL_EXTENSION = ".tmat";

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelLocation.getResourcePath()
        .endsWith(MATERIALMODEL_EXTENSION); // tinkermaterialmodel extension. Foo.tmat.json
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    try {
      ModelBlock modelBlock = ModelHelper.loadModelBlock(modelLocation);

      IModel model = new MaterialModel(modelBlock);

      // register the base texture for texture generation
      CustomTextureCreator.registerTextures(model.getTextures());

      return model;
    } catch (IOException e) {
      TConstruct.log.error("Could not load material model %s", modelLocation.toString());
    }
    return ModelLoaderRegistry.getMissingModel();
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {

  }
}
