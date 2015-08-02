package tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.client.CustomTextureCreator;

public class ToolModelLoader implements ICustomModelLoader {

  public static String EXTENSION = ".tcon";

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelLocation.getResourcePath().endsWith(EXTENSION); // tinkertoolmodel extension. Foo.tcon.json
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    if(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
      return DummyModel.INSTANCE;
    }

    try {
      // Modelblock is used since our format is compatible to the vanilla format
      // and we don't have to write our own json deserializer
      // it also provides us with the textures
      //ModelBlock modelBlock = ModelHelper.loadModelBlock(modelLocation);

      Map<String, String> textures = ModelHelper.loadTexturesFromJson(modelLocation);
      ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();

      List<MaterialModel> parts = Lists.newArrayList();
      List<MaterialModel> brokenParts = Lists.newArrayList();

      for(Map.Entry<String, String> entry : textures.entrySet()) {
        String name = entry.getKey();
        try {
          int i = Integer.valueOf(name.substring(5));
          List<MaterialModel> listToAdd;

          if(name.startsWith("layer")) {
            listToAdd = parts;
          }
          else if(name.startsWith("broken")) {
            listToAdd = brokenParts;
          }
          // invalid entry, ignore
          else {
            TinkerRegistry.log.warn("Toolmodel {} has invalid texture entry {}; Skipping layer.", modelLocation, name);
            continue;
          }

          ResourceLocation location = new ResourceLocation(entry.getValue());
          MaterialModel partModel = new MaterialModel(ImmutableList.of(location));
          while(listToAdd.size() <= i) {
            listToAdd.add(null);
          }
          listToAdd.set(i, partModel);

          builder.add(location);
        } catch(NumberFormatException e) {
          TinkerRegistry.log.error("Toolmodel {} has invalid texture entry {}; Skipping layer.", modelLocation, name);
        }
      }

      String toolName = FilenameUtils.getBaseName(modelLocation.getResourcePath());
      IModel mods = ModelLoaderRegistry.getModel(ModifierModelLoader.getLocationForToolModifiers(toolName));
      ModifierModel modifiers = null;

      if(mods == null || !(mods instanceof ModifierModel)) {
        TinkerRegistry.log.trace(
            "Toolmodel {} does not have any modifiers associated with it. Be sure that the Tools internal name, the Toolmodels filename and the name used inside the Modifier Model Definition match!",
            modelLocation);
      }
      else {
        modifiers = (ModifierModel) mods;
      }

      IModel output = new ToolModel(builder.build(), parts, brokenParts, modifiers);

      // inform the texture manager about the textures it has to process
      CustomTextureCreator.registerTextures(builder.build());

      return output;
    } catch(IOException e) {
      TinkerRegistry.log.error("Could not load multimodel {}", modelLocation.toString());
    }
    return ModelLoaderRegistry.getMissingModel();
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {

  }

  protected ResourceLocation getModelLocation(ResourceLocation p_177580_1_) {
    return new ResourceLocation(p_177580_1_.getResourceDomain(), "models/" + p_177580_1_.getResourcePath() + ".json");
  }


}
