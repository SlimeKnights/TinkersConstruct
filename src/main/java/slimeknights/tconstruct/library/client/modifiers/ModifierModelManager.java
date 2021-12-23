package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.data.IEarlySafeManagerReloadListener;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class handling the loading of modifier models
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ModifierModelManager implements IEarlySafeManagerReloadListener {
  /** Modifier file to load, has merging behavior but forge prevents multiple mods from loading the same file */
  private static final String VISIBLE_MODIFIERS = "models/tconstruct_modifiers.json";
  /** Instance of this manager */
  public static final ModifierModelManager INSTANCE = new ModifierModelManager();

  /** If true, the registration event has been fired */
  private static boolean eventFired = false;

  /** GSON instance for this */
  private static final Gson GSON = new GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Model overrides, if not in this map the default is used */
  private static final Map<ResourceLocation,IUnbakedModifierModel> MODIFIER_MODEL_OPTIONS = new HashMap<>();

  /** Map of models for each modifier */
  private static Map<Modifier,IUnbakedModifierModel> modifierModels = Collections.emptyMap();

  /**
   * Initializes this manager, registering it with the resource manager
   * @param manager  Manager
   */
  public static void init(IReloadableResourceManager manager) {
    manager.addReloadListener(INSTANCE);
  }

  /**
   * Converts the resource into a JSON file
   * @param resource  Resource to read. Closed when done
   * @return  JSON object, or null if failed to parse
   */
  @Nullable
  private static JsonObject getJson(IResource resource) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      return JSONUtils.fromJson(reader);
    } catch (JsonParseException | IOException e) {
      log.error("Failed to load texture JSON " + resource.getLocation(), e);
      return null;
    }
  }

  /**
   * Gets the loader for the given name
   * @param key    Key for errors
   * @param name   Loader name
   * @return  Unbaked model, or null if an error occurred (error is logged)
   */
  @Nullable
  private static IUnbakedModifierModel getLoader(String key, String name) {
    // find a model name
    ResourceLocation loader = ResourceLocation.tryCreate(name);
    if (loader == null) {
      log.error("Skipping modifier " + key + " as " + name + " is an invalid loader name");
    } else {
      // find a model
      IUnbakedModifierModel model = MODIFIER_MODEL_OPTIONS.get(loader);
      if (model == null) {
        log.error("Skipping modifier " + key + " as the loader " + loader + " is unknown");
      } else {
        return model;
      }
    }
    return null;
  }

  @Override
  public void onReloadSafe(IResourceManager manager) {
    // fire an event so people can register loaders, was the easiest way to do so after modifiers are registered but before models load
    if (!eventFired) {
      ModLoader.get().postEvent(new ModifierModelRegistrationEvent());
      eventFired = true;
    }

    // start building the model map
    Map<Modifier,IUnbakedModifierModel> models = new HashMap<>();

    // get a list of files from all namespaces
    List<JsonObject> jsonFiles = manager.getResourceNamespaces().stream()
                                        .flatMap(namespace -> {
                                          ResourceLocation location = new ResourceLocation(namespace, VISIBLE_MODIFIERS);
                                          try {
                                            return manager.getAllResources(location).stream();
                                          } catch (FileNotFoundException e) {
                                            // suppress, the above method throws instead of returning empty
                                          } catch (IOException e) {
                                            log.error("Failed to load modifier models from {}", location, e);
                                          }
                                          return Stream.empty();
                                        })
                                        .map(ModifierModelManager::getJson)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());
    // first object is bottom most pack, so upper resource packs will replace it
    for (int i = jsonFiles.size() - 1; i >= 0; i--) {
      JsonObject json = jsonFiles.get(i);
      // right now just do simply key value pairs
      for (Entry<String,JsonElement> entry : json.entrySet()) {
        // get a valid name
        String key = entry.getKey();
        ResourceLocation name = ResourceLocation.tryCreate(key);
        if (name == null) {
          log.error("Skipping invalid modifier key " + key + " as it is not a valid resource location");
        } else {
          // ensure its a valid modifier and not already parsed
          Modifier modifier = TinkerRegistries.MODIFIERS.getValue(name);
          if (modifier == null || modifier == TinkerModifiers.empty.get()) {
            log.error("Skipping unknown modifier " + key);
          } else if (!models.containsKey(modifier)) {
            // get a valid element, remove if null, error if not primitive
            JsonElement element = entry.getValue();
            if (element.isJsonNull()) {
              models.remove(modifier);
              // object means we configure the unbaked model
            } else if (element.isJsonObject()) {
              JsonObject object = element.getAsJsonObject();
              IUnbakedModifierModel model = getLoader(key, JSONUtils.getString(object, "type"));
              // configure the model with the given JSON data
              if (model != null) {
                models.put(modifier, model.configure(object));
              }
              // primitive means a string loader name
            } else if (element.isJsonPrimitive()) {
              IUnbakedModifierModel model = getLoader(key, element.getAsString());
              if (model != null) {
                models.put(modifier, model);
              }
            } else {
              log.error("Skipping key " + key + " as the value is not a string");
            }
          }
        }
      }
    }
    // replace the map
    modifierModels = models;
  }

  /**
   * Gets the path to the texture for a given modifier
   * @param modifierRoot  Modifier root location
   * @param modifierId    Specific modifier ID
   * @return  Path to the modifier
   */
  private static RenderMaterial getModifierTexture(ResourceLocation modifierRoot, ResourceLocation modifierId, String suffix) {
    return ForgeHooksClient.getBlockMaterial(new ResourceLocation(modifierRoot.getNamespace(), modifierRoot.getPath() + modifierId.getNamespace() + "_" + modifierId.getPath() + suffix));
  }

  /**
   * Gets the texture for the given parameters
   * @param modifierRoots   List of modifier roots, tries each
   * @param textureAdder    Functon to check if a texture exists, storing it as needed
   * @param modifier        Modifier to fetch
   * @param suffix          Additional suffix for the fetched texture
   * @return  Texture, or null if missing
   */
  @Nullable
  private static RenderMaterial getTexture(List<ResourceLocation> modifierRoots, @Nullable Predicate<RenderMaterial> textureAdder, ResourceLocation modifier, String suffix) {
    if (textureAdder == null) {
      return null;
    }

    // try the non-logging ones first
    for (ResourceLocation root : modifierRoots) {
      RenderMaterial texture = getModifierTexture(root, modifier, suffix);
      if (textureAdder.test(texture)) {
        return texture;
      }
    }
    // failed? return null
    return null;
  }

  /**
   * Gets a map of all models for the given tool
   *
   * @param smallModifierRoots  List of modifier roots for small tools
   * @param largeModifierRoots  List of modifier roots for large tools, null if the tool is not large
   * @return  Map of models
   */
  public static Map<Modifier,IBakedModifierModel> getModelsForTool(List<ResourceLocation> smallModifierRoots, List<ResourceLocation> largeModifierRoots, Collection<RenderMaterial> textures) {
    // if we have no modifier models, or both lists of modifier roots are empty, nothing to do
    if (modifierModels.isEmpty() || (smallModifierRoots.isEmpty() && largeModifierRoots.isEmpty())) {
      return Collections.emptyMap();
    }

    // start building the map
    ImmutableMap.Builder<Modifier,IBakedModifierModel> modelMap = ImmutableMap.builder();

    // create two texture adders, so we only log on the final option if missing
    Predicate<RenderMaterial> smallTextureAdder = smallModifierRoots.isEmpty() ? null
                                                  : MaterialModel.getTextureAdder(smallModifierRoots.get(0), textures, Config.CLIENT.logMissingModifierTextures.get());
    Predicate<RenderMaterial> largeTextureAdder = largeModifierRoots.isEmpty() ? null
                                                  : MaterialModel.getTextureAdder(largeModifierRoots.get(0), textures, Config.CLIENT.logMissingModifierTextures.get());

    // load each modifier
    for (Modifier modifier : TinkerRegistries.MODIFIERS.getValues()) {
      IUnbakedModifierModel model = modifierModels.get(modifier);
      if (model != null) {
        IBakedModifierModel toolModel = model.forTool(
          name -> getTexture(smallModifierRoots, smallTextureAdder, modifier.getId(), name),
          name -> getTexture(largeModifierRoots, largeTextureAdder, modifier.getId(), name));
        if (toolModel != null) {
          modelMap.put(modifier, toolModel);
        }
      }
    }

    // finished loading
    return modelMap.build();
  }

  /** Event fired when its time to register models */
  public static class ModifierModelRegistrationEvent extends Event implements IModBusEvent {
    /**
     * Register a unbaked model that modifiers can use
     * @param name   Modifier model name
     * @param model  Model instance
     */
    public void registerModel(ResourceLocation name, IUnbakedModifierModel model) {
      MODIFIER_MODEL_OPTIONS.put(name, model);
    }
  }
}
