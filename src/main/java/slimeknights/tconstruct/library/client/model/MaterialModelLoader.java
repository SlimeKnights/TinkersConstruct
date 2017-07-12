package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.model.format.Offset;
import slimeknights.tconstruct.library.tools.IToolPart;

public class MaterialModelLoader implements ICustomModelLoader {

  public static String EXTENSION = ".tmat";

  // used to create only actually needed textures in the texturegenerator instead of ALL materials for all parts
  private static final Map<ResourceLocation, Set<IToolPart>> partTextureRestriction = Maps.newHashMap();

  public static void addPartMapping(ResourceLocation resourceLocation, IToolPart toolPart) {
    if(!partTextureRestriction.containsKey(resourceLocation)) {
      partTextureRestriction.put(resourceLocation, Sets.newHashSet());
    }

    partTextureRestriction.get(resourceLocation).add(toolPart);
  }

  public static Optional<ResourceLocation> getToolPartModelLocation(IToolPart toolPart) {
    return partTextureRestriction.entrySet().stream().filter(entry -> entry.getValue().contains(toolPart)).findFirst().map(Map.Entry::getKey);
  }

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelLocation.getResourcePath()
                        .endsWith(EXTENSION); // tinkermaterialmodel extension. Foo.tmat.json
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    try {
      Offset offset = ModelHelper.loadOffsetFromJson(modelLocation);
      IModel model = new MaterialModel(ModelHelper.loadTextureListFromJson(modelLocation), offset.x, offset.y);

      ResourceLocation originalLocation = getReducedPath(modelLocation);

      // register the base texture for texture generation
      if(partTextureRestriction.containsKey(originalLocation)) {
        for(IToolPart toolPart : partTextureRestriction.get(originalLocation)) {
          for(ResourceLocation texture : model.getTextures()) {
            CustomTextureCreator.registerTextureForPart(texture, toolPart);
          }
        }
      }
      else {
        CustomTextureCreator.registerTextures(model.getTextures());
      }

      return model;
    } catch(IOException e) {
      TinkerRegistry.log.error("Could not load material model {}", modelLocation.toString());
      TinkerRegistry.log.debug(e);
    }
    return ModelLoaderRegistry.getMissingModel();
  }

  @Override
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

  }

  public static ResourceLocation getReducedPath(ResourceLocation location) {
    String path = location.getResourcePath();
    path = path.substring("models/item/".length());
    return new ResourceLocation(location.getResourceDomain(), path);
  }
}
