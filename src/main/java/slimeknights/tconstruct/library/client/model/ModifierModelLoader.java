package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.modifiers.IModifier;

public class ModifierModelLoader implements ICustomModelLoader {

  public static String EXTENSION = ".mod";

  private static final String defaultName = "default";
  private static final Logger log = Util.getLogger("modifier");

  // holds additional json files that shall be loaded for a specific modifier
  protected Map<String, List<ResourceLocation>> locations = Maps.newHashMap();
  protected Map<String, Map<String, String>> cache;

  public static ResourceLocation getLocationForToolModifiers(String toolName) {
    return new ResourceLocation(Util.RESOURCE, "modifiers/" + toolName + ModifierModelLoader.EXTENSION);
  }

  public void registerModifierFile(String modifier, ResourceLocation location) {
    List<ResourceLocation> files = locations.get(modifier);
    if(files == null) {
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
    toolname = toolname.toLowerCase(Locale.US);

    // we only load once. Without cache we'd have to load ALL modifier files again for each tool!
    if(cache == null) {
      cache = new THashMap<String, Map<String, String>>();
      loadFilesIntoCache();
    }

    ModifierModel model = new ModifierModel();

    if(cache.containsKey(toolname)) {
      // generate the modelblocks for each entry
      for(Map.Entry<String, String> entry : cache.get(toolname).entrySet()) {
        // check if the modifier actually exists in the game so we don't load unnecessary textures
        IModifier mod = TinkerRegistry.getModifier(entry.getKey());
/*
      if(mod == null) {
        log.debug("Removing texture {} for modifier {}: No modifier present for texture", entry.getValue(), entry.getKey());
        continue;
      }*/

        // using the String from the modifier means an == check succeeds and fixes lowercasing from the loading from files
        model.addModelForModifier(entry.getKey(), entry.getValue());

        // register per-material modifiers for texture creation
        if(mod != null && mod.hasTexturePerMaterial()) {
          CustomTextureCreator.registerTexture(new ResourceLocation(entry.getValue()));
        }
      }
    }
    else {
      log.debug("Tried to load modifier models for " + toolname + "but none were found");
    }

    return model;
  }

  @Override
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
    // goodbye, my dear data. You'll be...loaded again
    cache = null;
  }

  private void loadFilesIntoCache() {
    cache.put(defaultName, new THashMap<String, String>());

    // loop through all knows modifier-model-files
    for(Map.Entry<String, List<ResourceLocation>> entry : locations.entrySet()) {
      String modifier = entry.getKey();
      List<ResourceLocation> modLocations = entry.getValue();
      for(ResourceLocation location : modLocations) {
        try {
          // load the entries in the json file
          Map<String, String> textureEntries = ModelHelper.loadTexturesFromJson(location);

          // save them in the cache
          for(Map.Entry<String, String> textureEntry : textureEntries.entrySet()) {
            String tool = textureEntry.getKey().toLowerCase(Locale.US);
            String texture = textureEntry.getValue();

            if(!cache.containsKey(tool)) {
              cache.put(tool, new THashMap<String, String>());
            }
            // we don't allow overriding
            if(!cache.get(tool).containsKey(modifier)) {
              cache.get(tool).put(modifier, texture);
            }
          }
        } catch(IOException e) {
          log.error("Cannot load modifier-model " + entry.getValue(), e);
        } catch(JsonParseException e) {
          log.error("Cannot load modifier-model " + entry.getValue(), e);
          throw e;
        }
      }

      if(!cache.get(defaultName).containsKey(modifier)) {
        log.debug(String.format("%s Modifiers model does not contain a default-entry", modifier));
      }
    }

    Map<String, String> defaults = cache.get(defaultName);

    // fill in defaults where models are missing
    Iterator<Map.Entry<String, Map<String, String>>> toolEntryIter = cache.entrySet().iterator();
// todo: change this to iterate over all registered tools instead?
    while(toolEntryIter.hasNext()) {
      Map.Entry<String, Map<String, String>> toolEntry = toolEntryIter.next();
      //String tool = toolEntry.getKey();
      Map<String, String> textures = toolEntry.getValue();

      for(Map.Entry<String, String> defaultEntry : defaults.entrySet()) {
        // check if the tool has an entry for this modifier, otherwise fill in default
        if(!textures.containsKey(defaultEntry.getKey())) {
          log.debug("Filling in default for modifier {} on tool {}", defaultEntry.getKey(), toolEntry.getKey());
          textures.put(defaultEntry.getKey(), defaultEntry.getValue());
        }
      }
    }
  }
}
