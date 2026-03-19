package slimeknights.tconstruct.library.materials.stats;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.Level;
import slimeknights.mantle.data.listener.MergingJsonDataLoader;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJson;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
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
  public static final String FOLDER = "tinkering/materials/stats";

  /** Runnable to run after loading material stats */
  private final Runnable onLoaded;

  /**
   * This registry represents the known stats of the manager. Only known material types can be loaded.
   * Usually they're registered by the registry, when a new material stats type is registered.
   * It is not cleared on reload, since it does not represent loaded data. Think of it as a GSON type adapter.
   */
  @Getter
  private final IdAwareComponentRegistry<MaterialStatType<?>> statTypes = new IdAwareComponentRegistry<>("Unknown Material Stat Type");

  /** Final map of material ID to material stat ID to material stats */
  private Map<MaterialId, Map<MaterialStatsId, IMaterialStats>> materialToStatsPerType = Collections.emptyMap();

  public MaterialStatsManager(Runnable onLoaded) {
    super(JsonHelper.DEFAULT_GSON, FOLDER, id -> new HashMap<>());
    this.onLoaded = onLoaded;
  }

  /**
   * Registers a new material stat type
   * @param type   Type object
   */
  public <T extends IMaterialStats> void registerStatType(MaterialStatType<T> type) {
    statTypes.register(type);
  }

  /** Gets a lit of all material stat IDs */
  public Collection<ResourceLocation> getAllStatTypeIds() {
    return statTypes.getKeys();
  }

  /**
   * Gets the stat type for the given ID
   * @param id  Material stat ID
   * @return  Stat type, or null if unknown
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public <T extends IMaterialStats> MaterialStatType<T> getStatType(MaterialStatsId id) {
    return (MaterialStatType<T>) statTypes.getValue(id);
  }

  /**
   * Gets the stats for the given material and stats ID, or null if the pair has no stats.
   * @param materialId  Material ID
   * @param statId      Stat type
   * @param <T>  Stats type
   * @return  Stats if present, null if this material lacks the stat type.
   */
  @Nullable
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> T getStatsOrNull(MaterialId materialId, MaterialStatsId statId) {
    Map<MaterialStatsId, IMaterialStats> materialStats = materialToStatsPerType.getOrDefault(materialId, Map.of());
    IMaterialStats stats = materialStats.get(statId);
    // class will always match, since it's only filled by deserialization, which only puts it in if it's the registered type
    return (T) stats;
  }

  /**
   * Gets the stats for the given material and stats ID
   * @param materialId  Material ID
   * @param statId      Stat type
   * @param <T>  Stats type
   * @return  Optional containing the stats, empty if no stats.
   */
  public <T extends IMaterialStats> Optional<T> getStats(MaterialId materialId, MaterialStatsId statId) {
    return Optional.ofNullable(getStatsOrNull(materialId, statId));
  }

  /**
   * Gets the stats for the given material and stats ID, or the default stats if the material has no stats.
   * @param materialId  Material ID
   * @param statId      Stat type
   * @param <T>  Stats type
   * @return  Stats if present, default if the type is valid, or null if the type is invalid.
   */
  @Nullable
  public <T extends IMaterialStats> T getStatsOrDefault(MaterialId materialId, MaterialStatsId statId) {
    T stats = getStatsOrNull(materialId, statId);
    if (stats != null) {
      return stats;
    }
    MaterialStatType<T> type = getStatType(statId);
    return type != null ? type.getDefaultStats() : null;
  }

  /**
   * Gets all stats for the given material ID
   * @param materialId  Material
   * @return  Collection of all stats
   */
  public Collection<IMaterialStats> getAllStats(MaterialId materialId) {
    return materialToStatsPerType.getOrDefault(materialId, Map.of()).values();
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
    MaterialStatJson json = JsonHelper.DEFAULT_GSON.fromJson(element, MaterialStatJson.class);
    // instead of simply replacing the whole JSON object, merge the two together
    for (Entry<ResourceLocation,JsonElement> entry : json.getStats().entrySet()) {
      ResourceLocation key = entry.getKey();
      JsonElement valueElement = entry.getValue();
      if (valueElement.isJsonNull()) {
        builder.remove(key);
      } else {
        JsonObject value = GsonHelper.convertToJsonObject(valueElement, key.toString());
        JsonObject existing = builder.get(key);
        if (existing != null) {
          for (Entry<String,JsonElement> jsonEntry : value.entrySet()) {
            existing.add(jsonEntry.getKey(), jsonEntry.getValue());
          }
        } else {
          builder.put(key, value);
        }
      }
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation,Map<ResourceLocation, JsonObject>> map, ResourceManager manager) {
    // Take the final structure and actually load the different material stats. This drops all invalid stats
    materialToStatsPerType = map.entrySet().stream()
                                .collect(Collectors.toMap(
                                  entry -> new MaterialId(entry.getKey()),
                                  entry -> deserializeMaterialStatsFromContent(entry.getKey(), entry.getValue())));

    log.debug("Loaded stats for materials:{}",
              Util.toIndentedStringList(materialToStatsPerType.entrySet().stream()
                .sorted(Entry.comparingByKey())
                .map(entry -> String.format("%s - [%s]", entry.getKey(), entry.getValue().keySet().stream().sorted().map(Object::toString).collect(Collectors.joining(", "))))
                .collect(Collectors.toList())));
    onLoaded.run();
  }

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    long time = System.nanoTime();
    super.onResourceManagerReload(manager);
    log.info("{} stats loaded for {} materials in {} ms",
             materialToStatsPerType.values().stream().mapToInt(Map::size).sum(),
             materialToStatsPerType.size(), (System.nanoTime() - time) / 1000000f);
  }

  /**
   * Gets the packet to send on player login
   * @return  Packet object
   */
  public UpdateMaterialStatsPacket getUpdatePacket() {
    Map<MaterialId, Collection<IMaterialStats>> networkPayload =
      materialToStatsPerType.entrySet().stream()
                            .collect(Collectors.toMap(
                              Map.Entry::getKey,
                              entry -> entry.getValue().values()));
    return new UpdateMaterialStatsPacket(networkPayload);
  }

  /**
   * Builds a map of stat IDs and stat contents into material stats
   *
   * @param id          Material ID
   * @param contentsMap Contents of the JSON
   * @return Stats map
   */
  private Map<MaterialStatsId, IMaterialStats> deserializeMaterialStatsFromContent(ResourceLocation id, Map<ResourceLocation, JsonObject> contentsMap) {
    ImmutableMap.Builder<MaterialStatsId, IMaterialStats> builder = ImmutableMap.builder();
    for (Entry<ResourceLocation, JsonObject> entry : contentsMap.entrySet()) {
      MaterialStatsId statType = new MaterialStatsId(entry.getKey());
      JsonObject json = entry.getValue();
      MaterialStatType<?> type = getStatType(statType);
      if (type == null) {
        try {
          boolean optional = GsonHelper.getAsBoolean(json, "optional", false);
          log.log(optional ? Level.DEBUG : Level.ERROR, "Skipping unregistered material stat type '{}' for material '{}'. {}", statType, id, optional
            ? "It was marked as optional, so it is likely disabled compatability."
            : "This likely indicates a broken mod or datapack.");
        } catch (JsonSyntaxException e) {
          log.error("Failed to parse optional status for missing stat type '{}' on material '{}'", statType, id, e);
        }
        continue;
      }
      builder.put(statType, type.getLoadable().deserialize(json, TypedMapBuilder.builder().put(MaterialStatType.CONTEXT_KEY, type).build()));
    }
    return builder.build();
  }
}
