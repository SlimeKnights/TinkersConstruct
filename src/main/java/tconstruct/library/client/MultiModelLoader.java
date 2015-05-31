package tconstruct.library.client;

import com.google.common.base.Charsets;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tconstruct.TConstruct;
import tconstruct.library.client.model.MaterialModel;
import tconstruct.library.client.model.MultiModel;

public class MultiModelLoader implements ICustomModelLoader {
  public static String TOOLMODEL_EXTENSION = ".tcon";

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelLocation.getResourcePath().endsWith(TOOLMODEL_EXTENSION); // tinkertoolmodel extension. Foo.tcon.json
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    try {
      ModelBlock modelBlock = ModelHelper.loadModelBlock(modelLocation);

      modelBlock.parent = ModelHelper.DEFAULT_PARENT;
      modelBlock.name = modelLocation.toString();

      List<MaterialModel> parts = new LinkedList<>();
      // also load the parts of the tool, defined as the layers of the tool model
      for(String s : MultiModel.getLayers()) {
        String r = modelBlock.resolveTextureName(s);
        if(!"missingno".equals(r)) {
          ModelBlock mb = ModelBlock.deserialize(ModelHelper.getPartModelJSON(r));
          mb.name = r;
          parts.add(new MaterialModel(mb));
        }
      }

      List<MaterialModel> brokenParts = new LinkedList<>();
      for(String s : MultiModel.getBrokenLayers()) {
        String r = modelBlock.resolveTextureName(s);
        if(!"missingno".equals(r)) {
          ModelBlock mb = ModelBlock.deserialize(ModelHelper.getPartModelJSON(r));
          mb.name = r;
          brokenParts.add(new MaterialModel(mb));
        }
      }

      IModel output = new MultiModel(modelBlock, parts, brokenParts);

      // inform the texture manager about the textures it has to process
      CustomTextureCreator.registerTextures(output.getTextures());

      return output;
    } catch (IOException e) {
      TConstruct.log.error("Could not load multimodel %s", modelLocation.toString());
    }
    return ModelLoaderRegistry.getMissingModel();
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {

  }

  protected ResourceLocation getModelLocation(ResourceLocation p_177580_1_)
  {
    return new ResourceLocation(p_177580_1_.getResourceDomain(), "models/" + p_177580_1_.getResourcePath() + ".json");
  }


}
