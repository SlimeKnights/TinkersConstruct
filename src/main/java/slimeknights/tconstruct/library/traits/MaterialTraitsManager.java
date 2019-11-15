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
import slimeknights.tconstruct.library.traits.json.TraitMappingJson;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The location inside datapacks is "materials/stats".
 * So if your mods name is "foobar", the location for your mads material stats is "data/foobar/materials/stats".
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

  private Map<ResourceLocation, List<ResourceLocation>> materialDefaultTraits = ImmutableMap.of();
  private Map<ResourceLocation, Map<ResourceLocation, List<ResourceLocation>>> materialToTraitsPerStatsType = ImmutableMap.of();

  public MaterialTraitsManager() {
    super(GSON, FOLDER);
  }

  public List<ResourceLocation> getDefaultTraits(ResourceLocation materialId) {
    return materialDefaultTraits.getOrDefault(materialId, Collections.emptyList());
  }

  public List<ResourceLocation> getTraitsForStats(ResourceLocation materialId, ResourceLocation statId) {
    return materialToTraitsPerStatsType.getOrDefault(materialId, Collections.emptyMap())
      .getOrDefault(statId, Collections.emptyList());
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    Map<ResourceLocation, TraitMappingJson> parsedSplashList = parseSplashlist(splashList);

    materialDefaultTraits = makeImmutable(collectTraits(parsedSplashList, TraitMappingJson::getDefaultTraits));
    materialToTraitsPerStatsType = collectTraits(parsedSplashList, TraitMappingJson::getPerStat).entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> makeImmutable(entry.getValue())));
  }

  private Map<ResourceLocation, TraitMappingJson> parseSplashlist(Map<ResourceLocation, JsonObject> splashList) {
    return splashList.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, this::parse))
      // all this just to not break when an invalid json is encountered...
      .entrySet().stream()
      .filter(entry -> entry.getValue().isPresent())
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
  }

  private Map<ResourceLocation, List<ResourceLocation>> collectDefaultTraits(Map<ResourceLocation, TraitMappingJson> parsedSplashList) {
    return parsedSplashList.entrySet().stream()
      .map(entry -> {
        Map<ResourceLocation, List<ResourceLocation>> theMap = new HashMap<>();
        theMap.put(entry.getKey(), entry.getValue().getDefaultTraits());
        return theMap;
      })
      .reduce((entry1, entry2) -> {
        entry1.putAll(entry2);
        return entry1;
      }).orElse(Collections.emptyMap());
  }

  private <T> Map<ResourceLocation, T> collectTraits(Map<ResourceLocation, TraitMappingJson> parsedSplashList, Function<TraitMappingJson, T> mapper) {
    return parsedSplashList.entrySet().stream()
      .map(entry -> {
        Map<ResourceLocation, T> theMap = new HashMap<>();
        theMap.put(entry.getKey(), mapper.apply(entry.getValue()));
        return theMap;
      })
      .reduce((entry1, entry2) -> {
        entry1.putAll(entry2);
        return entry1;
      }).orElse(Collections.emptyMap());
  }

  private Optional<TraitMappingJson> parse(Map.Entry<ResourceLocation, JsonObject> entry) {
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
