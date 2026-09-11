package slimeknights.tconstruct.library.client.modifiers.block;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import slimeknights.mantle.data.listener.MergingJsonDataLoader;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.block.BlockModifierModelMapManager.Builder;
import slimeknights.tconstruct.library.client.modifiers.block.model.BlockModifierModel;
import slimeknights.tconstruct.library.client.modifiers.block.model.BlockModifierModelLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Manager for getting block modifier model maps
 */
public class BlockModifierModelMapManager extends MergingJsonDataLoader<Builder> {
  /** Folder for the block modifier models */
  public static final String MODEL_FOLDER = "tinkering/modifiers/models";
  /** Folder for the block modifier model maps */
  public static final String MAP_FOLDER = "tinkering/modifiers/block";
  /** Instance of this manager */
  public static final BlockModifierModelMapManager INSTANCE = new BlockModifierModelMapManager();
  /** List of unparsed models */
  private Map<ResourceLocation, JsonElement> unparsedModels = new HashMap<>();
  /** List of parsed models */
  private Map<ResourceLocation, BlockModifierModel> parsedModels = new HashMap<>();
  /** List of loaded model maps */
  private Map<ResourceLocation, BlockModifierModelMap> modelMaps = new HashMap<>();

  private BlockModifierModelMapManager() {
    super(JsonHelper.DEFAULT_GSON, MAP_FOLDER, id -> new Builder());
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
  private static <T> void parseModel(Map<T, BlockModifierModel> map, T key, JsonElement value, String errorPrefix, ResourceLocation id, BiFunction<ResourceLocation, T,TypedMap> context) {
    try {
      BlockModifierModel model = BlockModifierModelLoadable.DEFAULT.convert(value, key.toString(), context.apply(id, key));
      map.put(key, model);
    } catch (JsonSyntaxException | ResourceLocationException e) {
      TConstruct.LOG.error("Failed to parse modifier model map {} for {} {}", id, errorPrefix, key, e);
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation, Builder> map, ResourceManager manager) {
    BiFunction<ResourceLocation,String,TypedMap> constantContext = BlockModifierModelMapManager::context;
    BiFunction<ResourceLocation,ModifierId,TypedMap> modifierContext = BlockModifierModelMapManager::context;

    Map<ResourceLocation, BlockModifierModelMap> modelMaps = new HashMap<>();
    for (Entry<ResourceLocation, Builder> file : map.entrySet()) {
      ResourceLocation id = file.getKey();
      Map<String, BlockModifierModel> constant = new LinkedHashMap<>();
      Map<ModifierId, BlockModifierModel> modifiers = new HashMap<>();
      for (Entry<String,JsonElement> entry : file.getValue().constant.entrySet()) {
        parseModel(constant, entry.getKey(), entry.getValue(), "constant key", id, constantContext);
      }
      for (Entry<ModifierId,JsonElement> entry : file.getValue().modifiers.entrySet()) {
        parseModel(modifiers, entry.getKey(), entry.getValue(), "modifier", id, modifierContext);
      }
      // ensure we actually managed to parse something
      BlockModifierModelMap modelMap = BlockModifierModelMap.create(modifiers, constant);
      if (modelMap != BlockModifierModelMap.EMPTY) {
        modelMaps.put(id, modelMap);
      }
    }
    this.modelMaps = Map.copyOf(modelMaps);
  }

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    long time = System.nanoTime();
    unparsedModels.clear();
    parsedModels.clear();
    SimpleJsonResourceReloadListener.scanDirectory(manager, MODEL_FOLDER, JsonHelper.DEFAULT_GSON, unparsedModels);
    Iterator<Map.Entry<ResourceLocation, JsonElement>> iterator = unparsedModels.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<ResourceLocation, JsonElement> entry = iterator.next();
      JsonElement element = entry.getValue();
      if (!element.isJsonObject()) {
        TConstruct.LOG.error("Expected a JSON object for modifier model map '{}', but found {} (actual: {})", entry.getKey(), element.getClass().getSimpleName(), element);
        iterator.remove();
      }
    }
    super.onResourceManagerReload(manager);
    TConstruct.LOG.info("{} block modifier model maps in {} ms : {}", this.modelMaps.size(), (System.nanoTime() - time) / 1000000f, this.modelMaps.keySet());
  }

  private transient List<ResourceLocation> parsingStack = new ArrayList<>();

  public BlockModifierModel getModel(ResourceLocation id) {
    if(parsingStack.contains(id)) {
      TConstruct.LOG.warn("Circular reference of block modifier model: {} -> {}", Joiner.on("->").join(parsingStack), id);
      return null;
    }
    parsingStack.add(id);
    if (this.parsedModels.containsKey(id)) {
      parsingStack.remove(id);
      return this.parsedModels.get(id);
    }else if (this.unparsedModels.containsKey(id)) {
      try {
        BlockModifierModel model = BlockModifierModelLoadable.DEFAULT.convert(this.unparsedModels.get(id), id.toString());
        this.parsedModels.put(id, model);
        parsingStack.remove(id);
        return model;
      } catch (JsonSyntaxException | ResourceLocationException e) {
        parsingStack.remove(id);
        TConstruct.LOG.error("Failed to parse modifier model map {} for {}", id, e);
        return null;
      }
    }
    parsingStack.remove(id);
    return null;
  }

  public ResourceLocation getId(BlockModifierModel model) {
    for(Entry<ResourceLocation, BlockModifierModel> entry : this.parsedModels.entrySet()) {
      if(entry.getValue() == model) {
        return entry.getKey();
      }
    }
    return null;
  }

  /* Helpers */

  /** Predicate for removing empty modifier models */
  private static final Predicate<Entry<?,?>> EMPTY_ENTRY = entry -> entry.getValue() == BlockModifierModel.EMPTY;
  /** Predicate for removing empty modifier maps */
  private static final Predicate<BlockModifierModelMap> NOT_EMPTY_MAP = map -> !map.isEmpty();


  /** Gets a map of modifier models for the given tool */
  public BlockModifierModelMap getModelsForTool(List<ResourceLocation> options) {
    // quick exit: no options
    if (options.isEmpty()) {
      return BlockModifierModelMap.EMPTY;
    }
    // fetch options, filter to just those that exist
    List<BlockModifierModelMap> maps = options.stream().map(id -> this.modelMaps.getOrDefault(id, BlockModifierModelMap.EMPTY)).filter(NOT_EMPTY_MAP).toList();
    if (maps.isEmpty()) {
      return BlockModifierModelMap.EMPTY;
    }
    // if only one is requested, reuse that instance
    BlockModifierModelMap modelMap;
    if (maps.size() == 1) {
      modelMap = maps.get(0);
    } else {
      Map<String, BlockModifierModel> constant = new LinkedHashMap<>();
      Map<ModifierId, IBakedBlockModifierModel> modifiers = new HashMap<>();
      // loop backwards as we want the first that appears to take priority
      for (int i = maps.size() - 1; i >= 0; i--) {
        BlockModifierModelMap optionMap = maps.get(i);
        if (optionMap != null) {
          constant.putAll(optionMap.constant());
          modifiers.putAll(optionMap.modifiers());
        }
      }
      // remove empty models, we might have some if we were overriding for something like broken
      constant.entrySet().removeIf(EMPTY_ENTRY);
      modifiers.entrySet().removeIf(EMPTY_ENTRY);
      modelMap = BlockModifierModelMap.create(modifiers, constant);
    }
    // validate all model textures
    for (BlockModifierModel model : modelMap.constant().values()) {
      model.validate();
    }
    for (IBakedBlockModifierModel model : modelMap.modifiers().values()) {
      // we loaded this map in so know the type
      ((BlockModifierModel)model).validate();
    }
    return modelMap;
  }
}
