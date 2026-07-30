package slimeknights.tconstruct.library.client.modifiers.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;

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
import slimeknights.tconstruct.library.client.modifiers.IBakedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelMap;
import slimeknights.tconstruct.library.client.modifiers.block.BlockModifierModelMapManager.Builder;
import slimeknights.tconstruct.library.client.modifiers.block.model.BlockModifierModel;
import slimeknights.tconstruct.library.client.modifiers.block.model.CompoundBlockModifierModel;
import slimeknights.tconstruct.library.client.modifiers.block.model.ElementBlockModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Manager for getting block modifier model maps
 */
public class BlockModifierModelMapManager extends MergingJsonDataLoader<Builder> {
  /** Folder for the block modifier model maps */
  public static final String FOLDER = "tinkering/modifiers/block";
  /** Instance of this manager */
  public static final BlockModifierModelMapManager INSTANCE = new BlockModifierModelMapManager();

  /** List of loaded models */
  private Map<ResourceLocation, ModifierModelMap> models = new HashMap<>();

  private BlockModifierModelMapManager() {
    super(JsonHelper.DEFAULT_GSON, FOLDER, id -> new Builder());
  }

  @Override
  public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    // run in the first stage instead of the second stage
    return CompletableFuture.runAsync(() -> {
      if (ModLoader.isLoadingStateValid()) {
        // load block modifier models first
        BlockModifierModelManager.INSTANCE.load(resourceManager);
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
      BlockModifierModel model;
      if (value.isJsonArray()) {
        // for simplicity, treat an array as a compound
        model = CompoundBlockModifierModel.create(CompoundBlockModifierModel.LIST_LOADABLE.convert(value, key.toString(), context.apply(id, key)));
      } else if (value.isJsonPrimitive()) {
        model = Objects.requireNonNull(BlockModifierModelManager.INSTANCE.getModel(new ResourceLocation(value.getAsString())));
      } else {
        // if it's an object, it's a inline model. may be used for add transforms
        JsonObject json = value.getAsJsonObject();
        if (!json.has("type")) {
          model = CompoundBlockModifierModel.LOADER.deserialize(json, context.apply(id, key));
        } else {
          model = BlockModifierModel.LOADER.deserialize(json, context.apply(id, key));
        }
      }
      map.put(key, model);
    } catch (RuntimeException e) {
      TConstruct.LOG.error("Failed to parse modifier model map {} for {} {}", id, errorPrefix, key, e);
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation, Builder> map, ResourceManager manager) {
    BiFunction<ResourceLocation,String,TypedMap> constantContext = BlockModifierModelMapManager::context;
    BiFunction<ResourceLocation,ModifierId,TypedMap> modifierContext = BlockModifierModelMapManager::context;

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
    TConstruct.LOG.info("{} block modifier model maps in {} ms : {}", this.models.size(), (System.nanoTime() - time) / 1000000f, this.models.keySet());
  }


  /* Helpers */

  /** Predicate for removing empty modifier models */
  private static final Predicate<Entry<?,? extends IBakedModifierModel>> EMPTY_ENTRY = entry -> entry.getValue() == BlockModifierModel.EMPTY;
  /** Predicate for removing empty modifier maps */
  private static final Predicate<ModifierModelMap> NOT_EMPTY_MAP = map -> !map.isEmpty();


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
      ((BlockModifierModel)model).validate(spriteGetter);
    }
    return modelMap;
  }
}
