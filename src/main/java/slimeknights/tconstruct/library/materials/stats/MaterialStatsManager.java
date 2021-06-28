package slimeknights.tconstruct.library.materials.stats;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.data.MergingJsonDataLoader;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJson;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Loads the different material stats from the datapacks.
 * The file location determines the material it contains stats for, each file contains stats for exactly one one material.
 * The stats must be registered with TiC before loading or it'll fail.
 * <p>
 * Files with the same name are merged in a similar way to tags, so multiple mods can add different stats to the same material.
 * If two different sources add the same stats to the same material the first one encountered will be used, and the second one will be skipped.
 * (e.g. having a 'Laser' stat type, and there are 2 mods who add Laser stat types to the iron material)
 * <p>
 * The location inside datapacks is "materials/stats".
 * So if the material's mod name is "foobar", the location for your material's stats is "data/foobar/materials/stats".
 */
@Log4j2
public class MaterialStatsManager extends MergingJsonDataLoader<Map<ResourceLocation,JsonObject>> {
  public static final String FOLDER = "materials/stats";
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Runnable to run after loading material stats */
  private final Runnable onLoaded;

  /**
   * This map represents the known stats of the manager. Only known materials can be loaded.
   * Usually they're registered by the registry, when a new material stats type is registered.
   * It is not cleared on reload, since it does not represend loaded data. Think of it as a GSON type adapter.
   */
  private final Map<MaterialStatsId, MaterialStatType<?>> materialStatTypes = new HashMap<>();

  /** Final map of material ID to material stat ID to material stats */
  private Map<MaterialId, Map<MaterialStatsId, IMaterialStats>> materialToStatsPerType = Collections.emptyMap();

  public MaterialStatsManager(Runnable onLoaded) {
    super(GSON, FOLDER, id -> new HashMap<>());
    this.onLoaded = onLoaded;
  }

  @VisibleForTesting
  MaterialStatsManager() {
    this(() -> {});
  }

  /**
   * Registers a new material stat type
   * @param defaultStats   Default stats for the material
   * @param statsClass     Class representing the type
   */
  public <T extends IMaterialStats> void registerMaterialStat(T defaultStats, Class<T> statsClass) {
    MaterialStatsId materialStatType = defaultStats.getIdentifier();
    if (materialStatTypes.containsKey(materialStatType)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(materialStatType);
    }
    // todo: implement check if class is compatible with the requirements for a network syncable stats class
    materialStatTypes.put(materialStatType, new MaterialStatType<T>(materialStatType, statsClass, defaultStats, defaultStats instanceof IRepairableMaterialStats));
  }

  /**
   * Gets the class for the given stats ID
   * @param id  Stats class
   * @return  Stats ID
   */
  @Nullable
  public Class<? extends IMaterialStats> getClassForStat(MaterialStatsId id) {
    MaterialStatType<?> type = materialStatTypes.get(id);
    return type == null ? null : type.getStatsClass();
  }

  /**
   * Checks if the given stats ID can repair
   * @param id  ID
   * @return  True if it can repair
   */
  public boolean canRepair(MaterialStatsId id) {
    MaterialStatType<?> type = materialStatTypes.get(id);
    return type != null && type.canRepair();
  }

  /**
   * Gets the default stats for the given stats ID
   * @param statsId  Stats ID
   * @param <T>  Stats type
   * @return  Default stats
   */
  @Nullable
  public <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
    MaterialStatType<?> type = materialStatTypes.get(statsId);
    //noinspection unchecked
    return type == null ? null : (T) type.getDefaultStats();
  }

  /**
   * Gets the stats for the given material and stats ID
   * @param materialId  Material
   * @param statId      Stats
   * @param <T>  Stats type
   * @return  Optional containing the stats, empty if no stats
   */
  public <T extends IMaterialStats> Optional<T> getStats(MaterialId materialId, MaterialStatsId statId) {
    Map<MaterialStatsId, IMaterialStats> materialStats = materialToStatsPerType.getOrDefault(materialId, ImmutableMap.of());
    IMaterialStats stats = materialStats.get(statId);
    // class will always match, since it's only filled by deserialization, which only puts it in if it's the registered type
    //noinspection unchecked
    return Optional.ofNullable((T) stats);
  }

  /**
   * Gets all stats for the given material ID
   * @param materialId  Material
   * @return  Collection of all stats
   */
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
    onLoaded.run();
  }

  @Override
  protected void parse(Map<ResourceLocation, JsonObject> builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException {
    MaterialStatJson json = GSON.fromJson(element, MaterialStatJson.class);
    for (Entry<ResourceLocation, JsonObject> entry : json.getStats().entrySet()) {
      builder.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation,Map<ResourceLocation, JsonObject>> map, IResourceManager manager) {
    // Take the final structure and actually load the different material stats. This drops all invalid stats
    materialToStatsPerType = map.entrySet().stream()
                                .collect(Collectors.toMap(
                                  entry -> new MaterialId(entry.getKey()),
                                  entry -> deserializeMaterialStatsFromContent(entry.getValue())));

    log.debug("Loaded stats for materials:{}",
              Util.toIndentedStringList(materialToStatsPerType.entrySet().stream()
                                                              .map(entry -> String.format("%s - %s", entry.getKey(), Arrays.toString(entry.getValue().keySet().toArray())))
                                                              .collect(Collectors.toList())));
    log.info("{} stats loaded for {} materials",
             materialToStatsPerType.values().stream().mapToInt(stats -> stats.keySet().size()).sum(),
             materialToStatsPerType.size());
    onLoaded.run();
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

  /**
   * Builds a map of stat IDs and stat contents into material stats
   * @param contentsMap  Contents of the JSON
   * @return  Stats map
   */
  private Map<MaterialStatsId, IMaterialStats> deserializeMaterialStatsFromContent(Map<ResourceLocation, JsonObject> contentsMap) {
    ImmutableMap.Builder<MaterialStatsId, IMaterialStats> builder = ImmutableMap.builder();
    contentsMap.forEach((loc, contents) -> {
      MaterialStatsId id = new MaterialStatsId(loc);
      deserializeMaterialStat(id, contents).ifPresent(stats -> builder.put(id, stats));
    });
    return builder.build();
  }

  /**
   * Deserializes the json element and stats ID into material stats
   * @param statsId    Stats ID
   * @param statsJson  Stats JSON
   * @return  Optional of the element, empty if the stats failed to parse
   */
  private Optional<IMaterialStats> deserializeMaterialStat(MaterialStatsId statsId, JsonElement statsJson) {
    MaterialStatType<?> type = materialStatTypes.get(statsId);
    if (type == null) {
      log.error("The material stat of type '" + statsId + "' has not been registered");
      return Optional.empty();
    }
    return Optional.ofNullable(GSON.fromJson(statsJson, type.getStatsClass()));
  }
}
