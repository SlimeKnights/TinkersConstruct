package tconstruct.test;

import com.google.common.base.Charsets;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tconstruct.TConstruct;

public class MultiModelLoader implements ICustomModelLoader {
  private final Map<ResourceLocation, ResourceLocation> modelsToLoad = new THashMap<>();

  public void addModel(ResourceLocation original, ResourceLocation generated) {
    modelsToLoad.put(generated, original);
  }

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelsToLoad.containsKey(modelLocation);
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    try {
      ResourceLocation original = modelsToLoad.get(modelLocation);
      original = new ResourceLocation(original.getResourceDomain(), "item/" + original.getResourcePath());

      IResource
          iresource =
          Minecraft.getMinecraft().getResourceManager().getResource(this.getModelLocation(original));
      Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);

      ModelBlock modelBlock = ModelBlock.deserialize(reader);
      IModel model = ModelLoaderRegistry.getModel(original);


      List<ModelBlock> parts = new LinkedList<>();
      // also load the parts of the tool, defined as the layers of the tool model
      for(String s : (List<String>) ItemModelGenerator.LAYERS) {
        String r = modelBlock.resolveTextureName(s);
        if(!"missingno".equals(r))
          parts.add(ModelBlock.deserialize(getPartModelJSON(r)));
      }

      return new MultiModel(modelBlock, model, parts);
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

  private static String getPartModelJSON(String texture) {
    return String.format("{"
                         + "    \"parent\": \"builtin/generated\","
                         + "    \"textures\": {"
                         + "        \"layer0\": \"%s\""
                         + "    }"
                         + "}"
    ,texture);
  }
}
