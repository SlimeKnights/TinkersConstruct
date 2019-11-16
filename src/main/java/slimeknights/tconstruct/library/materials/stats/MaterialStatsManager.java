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
import slimeknights.tconstruct.library.exception.TinkerJSONException;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJsonWrapper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Loads the different material stats from the datapacks.
 * The file locations match the materials they belong to, and contain one or multiple stats.
 * The stats must be registered with TiC before loading or it'll fail.
 *
 * todo: part about how to add stats to existing materials
 *
 * The location inside datapacks is "materials/stats".
 * So if your mods name is "foobar", the location for your mads material stats is "data/foobar/materials/stats".
 */
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
  private Map<MaterialStatsId, Class<? extends BaseMaterialStats>> materialStatClasses = new HashMap<>();

  private Map<MaterialId, Map<MaterialStatsId, BaseMaterialStats>> materialToStatsPerType = ImmutableMap.of();

  public MaterialStatsManager() {
    super(GSON, FOLDER);
  }

  public void registerMaterialStat(MaterialStatsId materialStatType, Class<? extends BaseMaterialStats> statsClass) {
    if (materialStatClasses.containsKey(materialStatType)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(materialStatType);
    }
    materialStatClasses.put(materialStatType, statsClass);
  }

  public <T extends BaseMaterialStats> Optional<T> getStats(MaterialId materialId, MaterialStatsId statId) {
    Map<MaterialStatsId, BaseMaterialStats> materialStats = materialToStatsPerType.getOrDefault(materialId, ImmutableMap.of());
    IMaterialStats stats = materialStats.get(statId);
    // class will always match, since it's only filled by deserialization, which only puts it in if it's the registered type
    //noinspection unchecked
    return Optional.ofNullable((T) stats);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    // Combine all loaded material files into one map
    Map<MaterialId, List<BaseMaterialStats>> reducedMaterials = splashList.entrySet().stream()
      .map(entry -> loadMaterialStats(entry.getKey(), entry.getValue()))
      .filter(Objects::nonNull)
      .reduce((resourceLocationListMap, resourceLocationListMap2) -> {
        resourceLocationListMap.putAll(resourceLocationListMap2);
        return resourceLocationListMap;
      }).orElse(Collections.emptyMap());

    // make them immutable and save them
    ImmutableMap.Builder<MaterialId, Map<MaterialStatsId, BaseMaterialStats>> builder = ImmutableMap.builder();
    reducedMaterials.forEach((resourceLocation, iMaterialStatsList) -> builder.put(resourceLocation, convertToImmutableMap(iMaterialStatsList)));
    materialToStatsPerType = builder.build();
  }

  private Map<MaterialStatsId, BaseMaterialStats> convertToImmutableMap(List<BaseMaterialStats> stats) {
    return stats.stream()
      .collect(Collectors.toMap(BaseMaterialStats::getIdentifier, Function.identity()));
  }


  @Nullable
  private Map<MaterialId, List<BaseMaterialStats>> loadMaterialStats(ResourceLocation materialId, JsonObject jsonObject) {
    try {
      JsonArray statsJsonArray = jsonObject.getAsJsonArray("stats");
      List<BaseMaterialStats> stats = new ArrayList<>();

      if (statsJsonArray != null) {
        for (JsonElement statJson : statsJsonArray) {
          try {
            stats.add(deserializeMaterialStat(materialId, statJson));
          } catch (Exception e) {
            LOGGER.error("Could not deserialize material stats from file {}. JSON: {}", materialId, statJson, e);
          }
        }
      }

      return ImmutableMap.of(new MaterialId(materialId), stats);
    } catch (Exception e) {
      LOGGER.error("Could not deserialize material stats file {}. JSON: {}", materialId, jsonObject, e);
      return null;
    }
  }

  private BaseMaterialStats deserializeMaterialStat(ResourceLocation materialId, JsonElement statsJson) {
    MaterialStatJsonWrapper.BaseMaterialStatsJson baseMaterialStatsJson = GSON.fromJson(statsJson, MaterialStatJsonWrapper.BaseMaterialStatsJson.class);
    ResourceLocation statsId = baseMaterialStatsJson.getId();
    if(statsId == null) {
      throw TinkerJSONException.materialStatsJsonWithoutId(materialId);
    }
    Class<? extends BaseMaterialStats> materialStatClass = materialStatClasses.get(new MaterialStatsId(statsId));
    if(materialStatClass == null) {
      throw TinkerAPIMaterialException.materialNotRegistered(statsId);
    }
    return GSON.fromJson(statsJson, materialStatClass);
  }
}
