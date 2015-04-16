package tconstruct.test;

import com.google.common.base.Charsets;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tconstruct.TConstruct;

public class MultiModelLoader implements ICustomModelLoader {
  private final Map<ResourceLocation, ResourceLocation> modelsToLoad = new THashMap<>();
  // copy of the one in the ModelBakery
  private static final ModelBlock DEFAULT_PARENT;

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

      modelBlock.parent = DEFAULT_PARENT;
      modelBlock.name = modelLocation.toString();

      List<ModelBlock> parts = new LinkedList<>();
      // also load the parts of the tool, defined as the layers of the tool model
      for(String s : MultiModel.getLayers()) {
        String r = modelBlock.resolveTextureName(s);
        if(!"missingno".equals(r))
          parts.add(ModelBlock.deserialize(getPartModelJSON(r)));
      }

      IModel output = new MultiModel(modelBlock, model, parts);

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

  private static String getPartModelJSON(String texture) {
    return String.format("{"
                         + "    \"parent\": \"builtin/generated\","
                         + "    \"textures\": {"
                         + "        \"layer0\": \"%s\""
                         + "    }"
                         + "}"
    ,texture);
  }

  static {
    DEFAULT_PARENT = ModelBlock.deserialize("{"
                             + "\t\"elements\": [{\n"
                             + "\t\t\"from\": [0, 0, 0],\n"
                             + "\t\t\"to\": [16, 16, 16],\n"
                             + "\t\t\"faces\": {\n"
                             + "\t\t\t\"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}\n"
                             + "\t\t}\n"
                             + "\t}],\n"
                             + "\t\n"
                             + "    \"display\": {\n"
                             + "        \"thirdperson\": {\n"
                             + "            \"rotation\": [ 0, 90, -35 ],\n"
                             + "            \"translation\": [ 0, 1.25, -3.5 ],\n"
                             + "            \"scale\": [ 0.85, 0.85, 0.85 ]\n"
                             + "        },\n"
                             + "        \"firstperson\": {\n"
                             + "            \"rotation\": [ 0, -135, 25 ],\n"
                             + "            \"translation\": [ 0, 4, 2 ],\n"
                             + "            \"scale\": [ 1.7, 1.7, 1.7 ]\n"
                             + "        }\n"
                             + "    }\n"
                             + "}");
  }
}
