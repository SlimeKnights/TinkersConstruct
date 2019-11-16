package slimeknights.tconstruct.library.traits;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.traits.json.TraitMappingJson;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Loads the different material traits from the datapacks.
 * The file locations match the materials they belong to, and contain default traits and traits per stat type.
 * If no traits are present for a stat type, the default traits will be used.
 *
 * Note that this manager only contains the references per IDs. The actual combining is done in the registry.
 *
 * todo: part about how to add traits for new stats to existing materials
 *
 * The location inside datapacks is "materials/traits".
 * So if your mods name is "foobar", the location for your mads material stats is "data/foobar/materials/traits".
 */
public class MaterialTraitsManager extends JsonReloadListener {

  private static final Logger LOGGER = LogManager.getLogger();
  @VisibleForTesting
  protected static final String FOLDER = "materials/traits";
  @VisibleForTesting
  protected static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  private Map<MaterialId, List<TraitId>> materialDefaultTraits = ImmutableMap.of();
  private Map<MaterialId, Map<MaterialStatsId, List<TraitId>>> materialToTraitsPerStatsType = ImmutableMap.of();

  public MaterialTraitsManager() {
    super(GSON, FOLDER);
  }

  public List<TraitId> getDefaultTraits(MaterialId materialId) {
    return materialDefaultTraits.getOrDefault(materialId, Collections.emptyList());
  }

  public List<TraitId> getTraitsForStats(MaterialId materialId, MaterialStatsId statId) {
    return materialToTraitsPerStatsType.getOrDefault(materialId, Collections.emptyMap())
      .getOrDefault(statId, Collections.emptyList());
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    Map<MaterialId, TraitMappingJson> parsedSplashList = parseSplashlist(splashList);

    materialDefaultTraits = makeImmutable(collectTraits(parsedSplashList, TraitMappingJson::getDefaultTraits));
    materialToTraitsPerStatsType = collectTraits(parsedSplashList, TraitMappingJson::getPerStat).entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> makeImmutable(entry.getValue())));
  }

  private Map<MaterialId, TraitMappingJson> parseSplashlist(Map<ResourceLocation, JsonObject> splashList) {
    return splashList.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, this::parseJsonEntry))
      // all this just to not break when an invalid json is encountered...
      .entrySet().stream()
      .filter(entry -> entry.getValue().isPresent())
      .collect(Collectors.toMap(entry -> new MaterialId(entry.getKey()), entry -> entry.getValue().get()));
  }

  private <T> Map<MaterialId, T> collectTraits(Map<MaterialId, TraitMappingJson> parsedSplashList, Function<TraitMappingJson, T> mapper) {
    return parsedSplashList.entrySet().stream()
      .map(entry -> {
        Map<MaterialId, T> theMap = new HashMap<>();
        T value = mapper.apply(entry.getValue());
        // value might be null if defaults or stats field is missing
        if(value != null) {
          theMap.put(entry.getKey(), value);
        }
        return theMap;
      })
      .reduce((entry1, entry2) -> {
        entry1.putAll(entry2);
        return entry1;
      }).orElse(Collections.emptyMap());
  }

  private Optional<TraitMappingJson> parseJsonEntry(Map.Entry<ResourceLocation, JsonObject> entry) {
    try {
      return Optional.ofNullable(GSON.fromJson(entry.getValue(), TraitMappingJson.class));
    } catch (Exception e) {
      LOGGER.error("Could not deserialize material trait mapping from file {}. JSON: {}", entry.getKey(), entry.getValue(), e);
      return Optional.empty();
    }
  }

  private <K, V> Map<K, List<V>> makeImmutable(Map<K, List<V>> collectTraits) {
    return collectTraits.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> ImmutableList.copyOf(entry.getValue())));
  }

}
