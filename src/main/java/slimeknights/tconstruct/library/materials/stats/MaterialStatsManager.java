package slimeknights.tconstruct.library.materials.stats;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.exception.TinkerJSONException;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJsonWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads the different material stats from the datapacks.
 * The file locations do not matter per se, the file must specify which material it contains stats for.
 * Each file contains stats for exactly one one material.
 * The stats must be registered with TiC before loading or it'll fail.
 * <p>
 * The reason for the material id inside the file is so that multiple mods can add different stats to the same material.
 * If two different sources add the same stats to the same material the first one encountered will be used, and the second one will be skipped.
 * (e.g. having a 'Laser' stat type, and there are 2 mods who add Laser stat types to the iron material)
 * <p>
 * The location inside datapacks is "materials/stats".
 * So if your mods name is "foobar", the location for your mads material stats is "data/foobar/materials/stats".
 */
@Log4j2
public class MaterialStatsManager extends JsonReloadListener {
  public static final String FOLDER = "materials/stats";
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();
  /**
   * This map represents the known stats of the manager. Only known materials can be loaded.
   * Usually they're registered by the registry, when a new material stats type is registered.
   * It is not cleared on reload, since it does not represend loaded data. Think of it as a GSON type adapter.
   */
  private final Map<MaterialStatsId, Class<? extends IMaterialStats>> materialStatClasses = new HashMap<>();

  private Map<MaterialId, Map<MaterialStatsId, IMaterialStats>> materialToStatsPerType = ImmutableMap.of();

  public MaterialStatsManager() {
    super(GSON, FOLDER);
  }

  public void registerMaterialStat(MaterialStatsId materialStatType, Class<? extends IMaterialStats> statsClass) {
    if (materialStatClasses.containsKey(materialStatType)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(materialStatType);
    }
    materialStatClasses.put(materialStatType, statsClass);
  }

  public Class<? extends IMaterialStats> getClassForStat(MaterialStatsId id) {
    return materialStatClasses.get(id);
  }

  public <T extends IMaterialStats> Optional<T> getStats(MaterialId materialId, MaterialStatsId statId) {
    Map<MaterialStatsId, IMaterialStats> materialStats = materialToStatsPerType.getOrDefault(materialId, ImmutableMap.of());
    IMaterialStats stats = materialStats.get(statId);
    // class will always match, since it's only filled by deserialization, which only puts it in if it's the registered type
    //noinspection unchecked
    return Optional.ofNullable((T) stats);
  }

  public Collection<IMaterialStats> getAllStats(MaterialId materialId) {
    return materialToStatsPerType.getOrDefault(materialId, ImmutableMap.of()).values();
  }

  /**
   * Updates the material stats from the server, should only be called on the client
   * @param materialStats  Material stats list
   */
  public void updateMaterialStatsFromServer(Map<MaterialId, Collection<IMaterialStats>> materialStats) {
    this.materialToStatsPerType = materialStats.entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> entry.getValue().stream()
          .collect(Collectors.toMap(
            IMaterialStats::getIdentifier,
            Function.identity()
          )))
      );
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    // Combine all loaded material files into one map, removing possible conflicting stats and printing a warning about them
    // this map can't contain any duplicate material stats in one material anymore.
    Map<MaterialId, Map<MaterialStatsId, StatContent>> statContentMappedByMaterial = splashList.entrySet().stream()
      .filter(entry -> entry.getValue().isJsonObject())
      .map(entry -> loadFileContent(entry.getKey(), entry.getValue().getAsJsonObject()))
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        statsFileContent -> statsFileContent.materialId,
        statsFileContent -> transformAndCombineStatsForMaterial(statsFileContent.stats, Collections.emptyList()),
        (map1, map2) -> transformAndCombineStatsForMaterial(map1.values(), map2.values())
      ));

    // Take the final structure and actually load the different material stats. This drops all invalid stats
    materialToStatsPerType = statContentMappedByMaterial.entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> deserializeMaterialStatsFromContent(entry.getValue())
      ));

    log.debug("Loaded stats for materials:{}",
      Util.toIndentedStringList(materialToStatsPerType.entrySet().stream()
        .map(entry -> String.format("%s - %s", entry.getKey(), Arrays.toString(entry.getValue().keySet().toArray())))
        .collect(Collectors.toList())));
    log.info("{} stats loaded for {} materials",
      materialToStatsPerType.values().stream().mapToInt(stats -> stats.keySet().size()).sum(),
      materialToStatsPerType.size());
  }

  /**
   * Gets the packet to send on player login
   * @return  Packet object
   */
  public Object getUpdatePacket() {
    Map<MaterialId, Collection<IMaterialStats>> networkPayload =
      materialToStatsPerType.entrySet().stream()
                            .collect(Collectors.toMap(
                              Map.Entry::getKey,
                              entry -> entry.getValue().values()));
    return new UpdateMaterialStatsPacket(networkPayload);
  }

  private Map<MaterialStatsId, IMaterialStats> deserializeMaterialStatsFromContent(Map<MaterialStatsId, StatContent> contents) {
    Map<MaterialStatsId, Optional<IMaterialStats>> loadedStats = contents.entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> deserializeMaterialStat(entry.getValue().statsId, entry.getValue().json)
      ));

    // drop all entries that couldn't be deserialized
    return loadedStats.entrySet().stream()
      .filter(entry -> entry.getValue().isPresent())
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> entry.getValue().get()
      ));
  }

  private Map<MaterialStatsId, StatContent> transformAndCombineStatsForMaterial(Collection<StatContent> statsList1, Collection<StatContent> statsList2) {
    return Stream.concat(statsList1.stream(), statsList2.stream())
      .collect(Collectors.toMap(
        o -> o.statsId,
        statContent -> statContent,
        (statContent, statContent2) -> {
          log.error("Duplicate stats {} for a material, ignoring additional definitions. " +
            "Some mod is probably trying to add duplicate stats to another mods material.", statContent.statsId);
          return statContent;
        }
      ));
  }

  @Nullable
  private StatsFileContent loadFileContent(ResourceLocation file, JsonObject jsonObject) {
    try {
      MaterialStatJsonWrapper materialStatJsonWrapper = GSON.fromJson(jsonObject, MaterialStatJsonWrapper.class);
      MaterialId materialId = materialStatJsonWrapper.getMaterialId();
      if (materialId == null) {
        throw TinkerJSONException.materialStatsJsonWithoutMaterial();
      }
      JsonArray statsJsonArray = jsonObject.getAsJsonArray("stats");
      List<StatContent> stats = new ArrayList<>();

      if (statsJsonArray != null) {
        for (JsonElement statJson : statsJsonArray) {
          try {
            stats.add(loadStatContent(statJson));
          } catch (Exception e) {
            log.error("Could not deserialize material stats from file {}. JSON: {}", file, statJson, e);
          }
        }
      }

      return new StatsFileContent(new MaterialId(materialId), stats);
    } catch (Exception e) {
      log.error("Could not deserialize material stats file {}. JSON: {}", file, jsonObject, e);
      return null;
    }
  }

  private StatContent loadStatContent(JsonElement statsJson) {
    MaterialStatJsonWrapper.BaseMaterialStatsJson IMaterialStatsJson = GSON.fromJson(statsJson, MaterialStatJsonWrapper.BaseMaterialStatsJson.class);
    ResourceLocation statsId = IMaterialStatsJson.getId();
    if (statsId == null) {
      throw TinkerJSONException.materialStatsJsonWithoutId();
    }

    return new StatContent(new MaterialStatsId(statsId), statsJson);
  }

  private Optional<IMaterialStats> deserializeMaterialStat(MaterialStatsId statsId, JsonElement statsJson) {
    Class<? extends IMaterialStats> materialStatClass = materialStatClasses.get(statsId);
    if (materialStatClass == null) {
      log.error("The material stat of type '" + statsId + "' has not been registered");
      return Optional.empty();
    }
    return Optional.ofNullable(GSON.fromJson(statsJson, materialStatClass));
  }

  @AllArgsConstructor
  private static class StatContent {

    private final MaterialStatsId statsId;
    private final JsonElement json;
  }

  @AllArgsConstructor
  private static class StatsFileContent {

    private final MaterialId materialId;
    private final List<StatContent> stats;
  }
}
