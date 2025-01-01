package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import slimeknights.mantle.data.listener.IEarlySafeManagerReloadListener;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.DynamicTextureLoader;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class handling the loading of modifier models
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ModifierModelManager implements IEarlySafeManagerReloadListener {
  /** Modifier file to load, has merging behavior but forge prevents multiple mods from loading the same file */
  private static final String VISIBLE_MODIFIERS = "tinkering/modifiers.json";
  /** Instance of this manager */
  public static final ModifierModelManager INSTANCE = new ModifierModelManager();

  /** If true, the registration event has been fired */
  private static boolean eventFired = false;

  /** Model overrides, if not in this map the default is used */
  private static final Map<ResourceLocation,IUnbakedModifierModel> MODIFIER_MODEL_OPTIONS = new HashMap<>();

  /** Map of models for each modifier */
  private static Map<ModifierId,IUnbakedModifierModel> modifierModels = Collections.emptyMap();

  /**
   * Initializes this manager, registering it with the resource manager
   * @param manager  Manager
   */
  public static void init(RegisterClientReloadListenersEvent manager) {
    manager.registerReloadListener(INSTANCE);
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
    ResourceLocation loader = ResourceLocation.tryParse(name);
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
  public void onReloadSafe(ResourceManager manager) {
    // fire an event so people can register loaders, was the easiest way to do so after modifiers are registered but before models load
    if (!eventFired) {
      ModLoader.get().postEvent(new ModifierModelRegistrationEvent());
      eventFired = true;
    }

    // start building the model map
    Map<ModifierId,IUnbakedModifierModel> models = new HashMap<>();

    // get a list of files from all namespaces
    List<JsonObject> jsonFiles = JsonHelper.getFileInAllDomainsAndPacks(manager, VISIBLE_MODIFIERS, null);
    // first object is bottom most pack, so upper resource packs will replace it
    for (int i = jsonFiles.size() - 1; i >= 0; i--) {
      JsonObject json = jsonFiles.get(i);
      // right now just do simply key value pairs
      for (Entry<String,JsonElement> entry : json.entrySet()) {
        // get a valid name
        String key = entry.getKey();
        ModifierId name = ModifierId.tryParse(key);
        if (name == null) {
          log.error("Skipping invalid modifier key " + key + " as it is not a valid resource location");
        } else {
          // ensure it's not already parsed
          if (!models.containsKey(name)) {
            // get a valid element, remove if null, error if not primitive
            JsonElement element = entry.getValue();
            if (element.isJsonNull()) {
              models.remove(name);
              // object means we configure the unbaked model
            } else if (element.isJsonObject()) {
              JsonObject object = element.getAsJsonObject();
              IUnbakedModifierModel model = getLoader(key, GsonHelper.getAsString(object, "type"));
              // configure the model with the given JSON data
              if (model != null) {
                models.put(name, model.configure(object));
              }
              // primitive means a string loader name
            } else if (element.isJsonPrimitive()) {
              IUnbakedModifierModel model = getLoader(key, element.getAsString());
              if (model != null) {
                models.put(name, model);
              }
            } else {
              log.error("Skipping key " + key + " as the value is not a string or object");
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
  private static Material getModifierTexture(ResourceLocation modifierRoot, ResourceLocation modifierId, String suffix) {
    return new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(modifierRoot.getNamespace(), modifierRoot.getPath() + modifierId.getNamespace() + "_" + modifierId.getPath() + suffix));
  }

  /**
   * Gets the texture for the given parameters
   * @param modifierRoots List of modifier root locations to try
   * @param textureAdder  Logic to add a texture to the model
   * @param modifier      Modifier to fetch
   * @param suffix        Additional suffix for the fetched texture
   * @return  Texture, or null if missing
   */
  @Nullable
  private static Material getTexture(List<ResourceLocation> modifierRoots, Predicate<Material> textureAdder, ResourceLocation modifier, String suffix) {
    if (modifierModels.isEmpty()) {
      return null;
    }

    // try the non-logging ones first
    for (ResourceLocation root : modifierRoots) {
      Material texture = getModifierTexture(root, modifier, suffix);
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
  public static Map<ModifierId,IBakedModifierModel> getModelsForTool(Function<Material,TextureAtlasSprite> spriteGetter, List<ResourceLocation> smallModifierRoots, List<ResourceLocation> largeModifierRoots) {
    // if we have no modifier models, or both lists of modifier roots are empty, nothing to do
    if (modifierModels.isEmpty() || (smallModifierRoots.isEmpty() && largeModifierRoots.isEmpty())) {
      return Collections.emptyMap();
    }

    // start building the map
    ImmutableMap.Builder<ModifierId,IBakedModifierModel> modelMap = ImmutableMap.builder();

    // create two texture adders, so we only log on the final option if missing
    Predicate<Material> validator = DynamicTextureLoader.getTextureValidator(spriteGetter, Config.CLIENT.logMissingModifierTextures.get());

    // load each modifier
    for (Entry<ModifierId, IUnbakedModifierModel> entry : modifierModels.entrySet()) {
      ModifierId id = entry.getKey();
      IUnbakedModifierModel model = entry.getValue();
      IBakedModifierModel toolModel = model.forTool(
        name -> getTexture(smallModifierRoots, validator, id, name),
        name -> getTexture(largeModifierRoots, validator, id, name));
      if (toolModel != null) {
        modelMap.put(id, toolModel);
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
