package slimeknights.tconstruct.library.client.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import slimeknights.mantle.data.listener.MergingJsonDataLoader;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelMapManager.Builder;
import slimeknights.tconstruct.library.client.modifiers.model.CompoundModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Manager for getting modifier models
 */
public class ModifierModelMapManager extends MergingJsonDataLoader<Builder> {
  /** Folder for the modifier models */
  public static final String FOLDER = "tinkering/modifier_models";
  /** Instance of this manager */
  public static final ModifierModelMapManager INSTANCE = new ModifierModelMapManager();

  /** List of loaded models */
  private Map<ResourceLocation, ModifierModelMap> models = new HashMap<>();

  private ModifierModelMapManager() {
    super(JsonHelper.DEFAULT_GSON, FOLDER, id -> new Builder());
  }

  @Override
  public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    // run in the first stage instead of the second stage
    return CompletableFuture.runAsync(() -> {
      if (ModLoader.isLoadingStateValid()) {
        this.onResourceManagerReload(resourceManager);
      }
    }, backgroundExecutor).thenCompose(stage::wait);
  }

  /** Builder for a given tool model */
  protected static class Builder {
    private final Map<String, JsonElement> constant = new LinkedHashMap<>();
    private final Map<ModifierId, JsonElement> modifiers = new LinkedHashMap<>();
  }

  /** Inserts the given element into the map */
  private static <T> void insert(Map<T, JsonElement> map, T key, JsonElement value, String errorPrefix, ResourceLocation id) {
    // null means discard this model
    if (value.isJsonNull()) {
      map.remove(key);
    } else {
      map.put(key, value);
    }
  }

  @Override
  protected void parse(Builder builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException {
    JsonObject json = GsonHelper.convertToJsonObject(element, id.toString());

    // fixed entries merge at a top level
    if (json.has("constant")) {
      for (Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "constant").entrySet()) {
        insert(builder.constant, entry.getKey(), entry.getValue(), "constant key", id);
      }
    }

    // each entry in the set is a modifier to model pair. We only merge at the top level
    if (json.has("modifiers")) {
      for (Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "modifiers").entrySet()) {
        ModifierId modifier = ModifierId.tryParse(entry.getKey());
        if (modifier == null) {
          TConstruct.LOG.error("Invalid modifier ID {} while parsing modifier models {}", entry.getKey(), id);
        } else {
          insert(builder.modifiers, modifier, entry.getValue(), "modifier", id);
        }
      }
    }
  }

  /** Creates context for constant key parsing */
  private static TypedMap context(ResourceLocation file, String key) {
    return TypedMapBuilder.builder()
      .put(ContextKey.ID, file)
      .put(ContextKey.DEBUG, "Model Map " + file + " for constant key " + key)
      .build();
  }

  /** Creates context for modifier parsing */
  private static TypedMap context(ResourceLocation file, ModifierId modifier) {
    return TypedMapBuilder.builder()
      .put(ContextKey.ID, file)
      .put(ModifierId.CONTEXT_KEY, modifier)
      .put(ContextKey.DEBUG, "Model Map " + file + " for Modifier " + modifier)
      .build();
  }

  /** Parses the given model from the map */
  @SuppressWarnings("removal")
  private static <T> void parseModel(Map<T, ModifierModel> map, T key, JsonElement value, String errorPrefix, ResourceLocation id, BiFunction<ResourceLocation, T,TypedMap> context) {
    try {
      // if it's an object, it's a single model
      ModifierModel model;
      if (value.isJsonArray()) {
        // for simplicity, treat an array as a compound
        model = CompoundModifierModel.create(CompoundModifierModel.LIST_LOADABLE.convert(value, key.toString(), context.apply(id, key)));
      } else if (value.isJsonPrimitive()) {
        model = new NormalModifierModel(ModifierModel.blockAtlas(new ResourceLocation(value.getAsString())), null);
      } else {
        JsonObject json = value.getAsJsonObject();
        if (!json.has("type")) {
          model = NormalModifierModel.LOADER.deserialize(json, context.apply(id, key));
        } else {
          model = ModifierModel.LOADER.deserialize(json, context.apply(id, key));
        }
      }
      map.put(key, model);
    } catch (JsonSyntaxException | ResourceLocationException e) {
      TConstruct.LOG.error("Failed to parse modifier model map {} for {} {}", id, errorPrefix, key, e);
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation, Builder> map, ResourceManager manager) {
    BiFunction<ResourceLocation,String,TypedMap> constantContext = ModifierModelMapManager::context;
    BiFunction<ResourceLocation,ModifierId,TypedMap> modifierContext = ModifierModelMapManager::context;

    Map<ResourceLocation, ModifierModelMap> modelMaps = new HashMap<>();
    for (Entry<ResourceLocation, Builder> file : map.entrySet()) {
      ResourceLocation id = file.getKey();
      Map<String, ModifierModel> constant = new LinkedHashMap<>();
      Map<ModifierId, ModifierModel> modifiers = new HashMap<>();
      for (Entry<String,JsonElement> entry : file.getValue().constant.entrySet()) {
        parseModel(constant, entry.getKey(), entry.getValue(), "constant key", id, constantContext);
      }
      for (Entry<ModifierId,JsonElement> entry : file.getValue().modifiers.entrySet()) {
        parseModel(modifiers, entry.getKey(), entry.getValue(), "modifier", id, modifierContext);
      }
      // ensure we actually managed to parse something
      ModifierModelMap modelMap = ModifierModelMap.create(constant, modifiers);
      if (modelMap != ModifierModelMap.EMPTY) {
        modelMaps.put(id, modelMap);
      }
    }
    this.models = Map.copyOf(modelMaps);
  }

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    long time = System.nanoTime();
    super.onResourceManagerReload(manager);
    TConstruct.LOG.info("{} modifier model maps in {} ms", this.models.size(), (System.nanoTime() - time) / 1000000f);
  }


  /* Helpers */

  /** Models in this blacklist are skipped from the legacy system. Used to prevent singletons that don't check textures from causing legacy warnings on every tools */
  private static final Set<IUnbakedModifierModel> LEGACY_BLACKLIST = new HashSet<>();
  /** Predicate for removing empty modifier models */
  private static final Predicate<Entry<?,? extends IBakedModifierModel>> EMPTY_ENTRY = entry -> entry.getValue() == ModifierModel.EMPTY;
  /** Predicate for removing empty modifier maps */
  private static final Predicate<ModifierModelMap> NOT_EMPTY_MAP = map -> !map.isEmpty();

  /** Blacklists the given model from being included in the legacy system */
  public static void legacyBlacklist(IUnbakedModifierModel model) {
    LEGACY_BLACKLIST.add(model);
  }

  /** Gets a map of modifier models for the given tool */
  public ModifierModelMap getModelsForTool(Function<Material, TextureAtlasSprite> spriteGetter, List<ResourceLocation> options) {
    // quick exit: no options
    if (options.isEmpty()) {
      return ModifierModelMap.EMPTY;
    }
    // fetch options, filter to just those that exist
    List<ModifierModelMap> maps = options.stream().map(id -> this.models.getOrDefault(id, ModifierModelMap.EMPTY)).filter(NOT_EMPTY_MAP).toList();
    if (maps.isEmpty()) {
      return ModifierModelMap.EMPTY;
    }
    // if only one is requested, reuse that instance
    ModifierModelMap modelMap;
    if (maps.size() == 1) {
      modelMap = maps.get(0);
    } else {
      Map<String, ModifierModel> constant = new LinkedHashMap<>();
      Map<ModifierId, IBakedModifierModel> modifiers = new HashMap<>();
      // loop backwards as we want the first that appears to take priority
      for (int i = maps.size() - 1; i >= 0; i--) {
        ModifierModelMap optionMap = maps.get(i);
        if (optionMap != null) {
          constant.putAll(optionMap.constant());
          modifiers.putAll(optionMap.modifiers());
        }
      }
      // remove empty models, we might have some if we were overriding for something like broken
      constant.entrySet().removeIf(EMPTY_ENTRY);
      modifiers.entrySet().removeIf(EMPTY_ENTRY);
      modelMap = ModifierModelMap.create(constant, modifiers);
    }
    // validate all model textures
    for (ModifierModel model : modelMap.constant().values()) {
      model.validate(spriteGetter);
    }
    for (IBakedModifierModel model : modelMap.modifiers().values()) {
      // we loaded this map in so know the type
      ((ModifierModel)model).validate(spriteGetter);
    }
    return modelMap;
  }

  /** Gets a map of modifier models for the given tool, considering the legacy model system */
  public ModifierModelMap getModelsForTool(Function<Material, TextureAtlasSprite> spriteGetter, List<ResourceLocation> options, List<ResourceLocation> smallRoots, List<ResourceLocation> largeRoots, ResourceLocation modelLocation) {
    ModifierModelMap models = getModelsForTool(spriteGetter, options);
    // if not using the legacy system, we are done
    if (smallRoots.isEmpty() && largeRoots.isEmpty()) {
      return models;
    }
    Map<ModifierId, IBakedModifierModel> legacy = ModifierModelManager.getModelsForTool(spriteGetter, smallRoots, largeRoots, models.modifiers().keySet(), options.isEmpty() ? Set.of() : LEGACY_BLACKLIST);
    // nothing on the old system? nothing to do
    if (legacy.isEmpty()) {
      return models;
    }
    // if nothing is on the new system, just return the legacy one with a warning
    if (models.isEmpty()) {
      TConstruct.LOG.warn("Tool model {} is using deprecated system for modifier models instead of modifier model maps for {}", modelLocation, legacy.keySet());
      return ModifierModelMap.create(Map.of(), legacy);
    }
    // have both so we need to combine
    Map<ModifierId, IBakedModifierModel> builder = new HashMap<>(models.modifiers());
    Set<ModifierId> legacyIds = new HashSet<>();
    for (Entry<ModifierId, IBakedModifierModel> entry : legacy.entrySet()) {
      ModifierId id = entry.getKey();
      if (builder.putIfAbsent(id, entry.getValue()) == null) {
        legacyIds.add(id);
      }
    }
    // only warn of legacy usage if we have legacy models
    if (!legacyIds.isEmpty()) {
      TConstruct.LOG.warn("Tool model {} is using deprecated system for modifier models instead of modifier model maps for {}", modelLocation, legacyIds);
    }
    return ModifierModelMap.create(models.constant(), builder);
  }
}
