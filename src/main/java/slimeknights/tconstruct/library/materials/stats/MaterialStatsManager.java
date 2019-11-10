package slimeknights.tconstruct.library.materials.stats;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.json.MaterialStatJson;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class MaterialStatsManager extends JsonReloadListener {

  private static final Logger LOGGER = LogManager.getLogger();
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
  private Map<ResourceLocation, Class<? extends IMaterialStats>> materialStatClasses = new HashMap<>();

  private Map<ResourceLocation, Map<ResourceLocation, IMaterialStats>> materialToStatsPerType = ImmutableMap.of();

  public void registerMaterialStat(MaterialStatType materialStatType, Class<? extends IMaterialStats> statsClass) {
    ResourceLocation identifier = materialStatType.getIdentifier();
    if (materialStatClasses.containsKey(identifier)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(materialStatType);
    }
    materialStatClasses.put(identifier, statsClass);
  }

  public <T extends IMaterialStats> Optional<T> getStats(IMaterial material, MaterialStatType type) {
    ResourceLocation statId = type.getIdentifier();
    Map<ResourceLocation, IMaterialStats> materialStats = materialToStatsPerType.getOrDefault(material.getIdentifier(), ImmutableMap.of());
    IMaterialStats stats = materialStats.get(statId);
    // class will always match, since it's only filled by deserialization, which only puts it in if it's the registered type
    //noinspection unchecked
    return Optional.ofNullable((T) stats);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    // Combine all loaded material files
    Map<ResourceLocation, List<IMaterialStats>> reducedMaterials = splashList.entrySet().stream()
      .map(entry -> loadMaterialStats(entry.getKey(), entry.getValue()))
      .filter(Objects::nonNull)
      .reduce((resourceLocationListMap, resourceLocationListMap2) -> {
        resourceLocationListMap.putAll(resourceLocationListMap2);
        return resourceLocationListMap;
      }).orElse(Collections.emptyMap());

    // make them immutable and save them
    ImmutableMap.Builder<ResourceLocation, Map<ResourceLocation, IMaterialStats>> builder = ImmutableMap.builder();
    reducedMaterials.forEach((resourceLocation, iMaterialStatsList) -> builder.put(resourceLocation, convertToImmutableMap(iMaterialStatsList)));
  }

  private Map<ResourceLocation, IMaterialStats> convertToImmutableMap(List<IMaterialStats> stats) {
    return stats.stream()
      .collect(Collectors.toMap(iMaterialStats -> iMaterialStats.getIdentifier(), iMaterialStats -> iMaterialStats));
  }


  @Nullable
  private Map<ResourceLocation, List<IMaterialStats>> loadMaterialStats(ResourceLocation resourceLocation, JsonObject jsonObject) {
    try {
      MaterialStatJson overallJson = GSON.fromJson(jsonObject, MaterialStatJson.class);

      ResourceLocation materialId = Objects.requireNonNull(overallJson.getMaterialId());
      JsonArray statsJsonArray = jsonObject.getAsJsonArray("stats");
      List<IMaterialStats> stats = new ArrayList<>();

      if(statsJsonArray != null) {
        for (JsonElement statJson : statsJsonArray) {
          try {
            stats.add(deserializeMaterialStat(statJson));
          } catch (Exception e) {
            LOGGER.error("Could not deserialize material stats from file {}. JSON: {}", resourceLocation, statJson, e);
          }
        }
      }

      return new StatsForMaterial(materialId, stats);
    } catch (Exception e) {
      LOGGER.error("Could not deserialize material stats file {}. JSON: {}", resourceLocation, jsonObject, e);
      return null;
    }
  }

  private IMaterialStats deserializeMaterialStat(JsonElement statsJson) {
    MaterialStatJson.MaterialStatsWrapper materialStatsWrapper = GSON.fromJson(statsJson, MaterialStatJson.MaterialStatsWrapper.class);
    Class<? extends IMaterialStats> materialStatClass = materialStatClasses.get(materialStatsWrapper.getId());
    return GSON.fromJson(statsJson, materialStatClass);
  }

  private static class StatsForMaterial {

    private final ResourceLocation materialId;
    private final List<IMaterialStats> stats;

    public StatsForMaterial(ResourceLocation materialId, List<IMaterialStats> stats) {
      this.materialId = materialId;
      this.stats = stats;
    }
  }
}
