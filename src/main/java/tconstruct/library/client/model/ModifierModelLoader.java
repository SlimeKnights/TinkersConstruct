package tconstruct.library.client.model;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerAPIException;
import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.modifiers.IModifier;

public class ModifierModelLoader implements ICustomModelLoader {

  public static String EXTENSION = ".mod";

  private static Gson gson = new Gson();
  private static Type jsonType = new TypeToken<Map<String, String>>() {}.getType();
  private static final String defaultName = "default";

  // holds additional json files that shall be loaded for a specific modifier
  protected Map<String, List<ResourceLocation>> locations = Maps.newHashMap();
  protected Map<String, Map<String, String>> cache;

  public static ResourceLocation getLocationForToolModifiers(String toolName) {
    return new ResourceLocation(Util.RESOURCE, "modifiers/" + toolName + ModifierModelLoader.EXTENSION);
  }

  public void registerModifierFile(String modifier, ResourceLocation location) {
    List<ResourceLocation> files = locations.get(modifier);
    if (files == null) {
      files = Lists.newLinkedList();
      locations.put(modifier, files);
    }

    files.add(location);
  }

  @Override
  public boolean accepts(ResourceLocation modelLocation) {
    return modelLocation.getResourcePath().endsWith(EXTENSION); // tinkermodifier extension. Foo.mod.json
  }

  @Override
  public IModel loadModel(ResourceLocation modelLocation) {
    // this function is actually getting called on a PER TOOL basis, not per modifier
    // we therefore need to look through all modifiers to construct a model containing all modifiers for that tool

    int start = modelLocation.getResourcePath().lastIndexOf('/');
    String toolname = modelLocation.getResourcePath().substring(start < 0 ? 0 : start + 1,
                                                                modelLocation.getResourcePath().length() - EXTENSION
                                                                    .length());
    toolname = toolname.toLowerCase();

    // we only load once. Without cache we'd have to load ALL modifier files again for each tool!
    if (cache == null) {
      cache = new THashMap<>();
      loadFilesIntoCache();
    }

    if (!cache.containsKey(toolname)) {
      return ModelLoaderRegistry.getMissingModel();
    }

    ModifierModel model = new ModifierModel();

    // generate the modelblocks for each entry
    for (Map.Entry<String, String> entry : cache.get(toolname).entrySet()) {
      // check if the modifier actually exists in the game so we don't load unnecessary textures
      IModifier mod = TinkerRegistry.getModifier(entry.getKey());

      if (mod == null) {
        TinkerRegistry.log.debug("Removing texture {} for modifier {}: No modifier present for texture",
                                 entry.getValue(), entry.getKey());
        continue;
      }

      try {
        ModelBlock modelBlock = ModelHelper.loadModelBlockFromTexture(entry.getValue());
        modelBlock.parent = ModelHelper.DEFAULT_PARENT;
        // using the String from the modifier means an == check succeeds and fixes lowercasing from the loading from files
        model.addModelForModifier(mod.getIdentifier(), modelBlock);

        // register per-material modifiers for texture creation
        if (mod.hasTexturePerMaterial()) {
          CustomTextureCreator.registerTexture(new ResourceLocation(entry.getValue()));
        }
      } catch (IOException e) {
        TinkerRegistry.log.error("Could not load model for modifier {} on tool {}: {}", entry.getKey(), toolname,
                                 modelLocation.toString());
      }
    }

    return model;
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {
    // goodbye, my dear data. You'll be...loaded again
    cache = null;
  }

  private void loadFilesIntoCache() {
    cache.put(defaultName, new THashMap<String, String>());

    // loop through all knows modifier-model-files
    for (Map.Entry<String, List<ResourceLocation>> entry : locations.entrySet()) {
      String modifier = entry.getKey();
      List<ResourceLocation> modLocations = entry.getValue();
      for (ResourceLocation location : modLocations) {
        try {
          // load the entries in the json file
          location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json");
          IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(location);
          Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
          Map<String, String> textureEntries = gson.fromJson(reader, jsonType);

          // save them in the cache
          for (Map.Entry<String, String> textureEntry : textureEntries.entrySet()) {
            String tool = textureEntry.getKey().toLowerCase();
            String texture = textureEntry.getValue();

            if (!cache.containsKey(tool)) {
              cache.put(tool, new THashMap<String, String>());
            }
            // we don't allow overriding
            if (!cache.get(tool).containsKey(modifier)) {
              cache.get(tool).put(modifier, texture);
            }
          }
        } catch (IOException e) {
          TinkerRegistry.log.error("Cannot load modifier-model {}", entry.getValue());
        }
      }

      if (!cache.get(defaultName).containsKey(modifier)) {
        throw new TinkerAPIException(String.format("%s Modifiers model does not contain a default-entry!", modifier));
      }
    }

    Map<String, String> defaults = cache.get(defaultName);

    // fill in defaults where models are missing
    Iterator<Map.Entry<String, Map<String, String>>> toolEntryIter = cache.entrySet().iterator();
// todo: change this to iterate over all registered tools instead?
    while (toolEntryIter.hasNext()) {
      Map.Entry<String, Map<String, String>> toolEntry = toolEntryIter.next();
      //String tool = toolEntry.getKey();
      Map<String, String> textures = toolEntry.getValue();

      for (Map.Entry<String, String> defaultEntry : defaults.entrySet()) {
        // check if the tool has an entry for this modifier, otherwise fill in default
        if (!textures.containsKey(defaultEntry.getKey())) {
          textures.put(defaultEntry.getKey(), defaultEntry.getValue());
        }
      }
    }
  }
}
