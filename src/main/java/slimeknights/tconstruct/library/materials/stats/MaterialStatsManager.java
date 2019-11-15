package slimeknights.tconstruct.library.materials.stats;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.json.MaterialStatJsonWrapper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MaterialStatsManager extends JsonReloadListener {

  private static final Logger LOGGER = LogManager.getLogger();
  @VisibleForTesting
  protected static final String FOLDER = "materials/stats";
  @VisibleForTesting
  protected static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /**
   * This map represents the known stats of the manager. Only known materials can be loaded.
   * Usually they're registered by the registry, when a new material stats type is registered.
   * It is not cleared on reload, since it does not represend loaded data. Think of it as a GSON type adapter.
   */
  private Map<ResourceLocation, Class<? extends BaseMaterialStats>> materialStatClasses = new HashMap<>();

  private Map<ResourceLocation, Map<ResourceLocation, BaseMaterialStats>> materialToStatsPerType = ImmutableMap.of();

  public MaterialStatsManager() {
    super(GSON, FOLDER);
  }

  public void registerMaterialStat(ResourceLocation materialStatType, Class<? extends BaseMaterialStats> statsClass) {
    if (materialStatClasses.containsKey(materialStatType)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(materialStatType);
    }
    materialStatClasses.put(materialStatType, statsClass);
  }

  public <T extends BaseMaterialStats> Optional<T> getStats(ResourceLocation materialId, ResourceLocation statId) {
    Map<ResourceLocation, BaseMaterialStats> materialStats = materialToStatsPerType.getOrDefault(materialId, ImmutableMap.of());
    IMaterialStats stats = materialStats.get(statId);
    // class will always match, since it's only filled by deserialization, which only puts it in if it's the registered type
    //noinspection unchecked
    return Optional.ofNullable((T) stats);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    // Combine all loaded material files
    Map<ResourceLocation, List<BaseMaterialStats>> reducedMaterials = splashList.entrySet().stream()
      .map(entry -> loadMaterialStats(entry.getKey(), entry.getValue()))
      .filter(Objects::nonNull)
      .reduce((resourceLocationListMap, resourceLocationListMap2) -> {
        resourceLocationListMap.putAll(resourceLocationListMap2);
        return resourceLocationListMap;
      }).orElse(Collections.emptyMap());

    // make them immutable and save them
    ImmutableMap.Builder<ResourceLocation, Map<ResourceLocation, BaseMaterialStats>> builder = ImmutableMap.builder();
    reducedMaterials.forEach((resourceLocation, iMaterialStatsList) -> builder.put(resourceLocation, convertToImmutableMap(iMaterialStatsList)));
    materialToStatsPerType = builder.build();
  }

  private Map<ResourceLocation, BaseMaterialStats> convertToImmutableMap(List<BaseMaterialStats> stats) {
    return stats.stream()
      .collect(Collectors.toMap(BaseMaterialStats::getIdentifier, Function.identity()));
  }


  @Nullable
  private Map<ResourceLocation, List<BaseMaterialStats>> loadMaterialStats(ResourceLocation materialId, JsonObject jsonObject) {
    try {
      JsonArray statsJsonArray = jsonObject.getAsJsonArray("stats");
      List<BaseMaterialStats> stats = new ArrayList<>();

      if (statsJsonArray != null) {
        for (JsonElement statJson : statsJsonArray) {
          try {
            stats.add(deserializeMaterialStat(statJson));
          } catch (Exception e) {
            LOGGER.error("Could not deserialize material stats from file {}. JSON: {}", materialId, statJson, e);
          }
        }
      }

      return ImmutableMap.of(materialId, stats);
    } catch (Exception e) {
      LOGGER.error("Could not deserialize material stats file {}. JSON: {}", materialId, jsonObject, e);
      return null;
    }
  }

  private BaseMaterialStats deserializeMaterialStat(JsonElement statsJson) {
    MaterialStatJsonWrapper.BaseMaterialStatsJson baseMaterialStatsJson = GSON.fromJson(statsJson, MaterialStatJsonWrapper.BaseMaterialStatsJson.class);
    Class<? extends BaseMaterialStats> materialStatClass = materialStatClasses.get(baseMaterialStatsJson.getId());
    if(materialStatClass == null) {
      throw TinkerAPIMaterialException.materialNotRegistered(baseMaterialStatsJson.getId());
    }
    return GSON.fromJson(statsJson, materialStatClass);
  }
}
