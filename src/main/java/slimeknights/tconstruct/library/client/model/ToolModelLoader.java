package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.CustomTextureCreator;

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
      Map<String, String> textures = ModelHelper.loadTexturesFromJson(modelLocation);
      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms = ModelHelper.loadTransformFromJson(modelLocation);
      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> blockingTransforms = ModelHelper.loadTransformFromJson(modelLocation, "blocking");
      Float[] rotations = ModelHelper.loadLayerRotations(modelLocation);

      if(rotations.length > 0 && textures.size() != rotations.length) {
        TinkerRegistry.log.error("Toolmodel {} has invalid layerrotation entry: Size should be {} but is {}; Skipping rotations.", modelLocation, textures.size(), rotations.length);
        rotations = new Float[0];
      }

      if(blockingTransforms.isEmpty()) {
        blockingTransforms = transforms;
      }

      ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
      List<MaterialModel> parts = Lists.newArrayList();
      List<MaterialModel> brokenParts = Lists.newArrayList();

      for(Map.Entry<String, String> entry : textures.entrySet()) {
        String name = entry.getKey();
        try {
          int i;
          List<MaterialModel> listToAdd;

          if(name.startsWith("layer")) {
            i = Integer.valueOf(name.substring(5));
            listToAdd = parts;
          }
          else if(name.startsWith("broken")) {
            i = Integer.valueOf(name.substring(6));
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
      IModel mods;
      try {
        mods = ModelLoaderRegistry.getModel(ModifierModelLoader.getLocationForToolModifiers(toolName));
      } catch(Exception e) {
        TinkerRegistry.log.error(e);
        mods = null;
      }
      ModifierModel modifiers = null;

      if(mods == null || !(mods instanceof ModifierModel)) {
        TinkerRegistry.log.trace(
            "Toolmodel {} does not have any modifiers associated with it. Be sure that the Tools internal name, the Toolmodels filename and the name used inside the Modifier Model Definition match!",
            modelLocation);
      }
      else {
        modifiers = (ModifierModel) mods;
      }

      IModel output = new ToolModel(builder.build(), parts, brokenParts, rotations, modifiers, transforms, blockingTransforms);

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
}
