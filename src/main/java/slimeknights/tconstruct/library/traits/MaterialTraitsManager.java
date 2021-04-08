package slimeknights.tconstruct.library.traits;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.exception.TinkerJSONException;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.traits.json.TraitMappingJson;

import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads the different material traits from the datapacks.
 * The file locations do not matter per se, the file must specify for which material it contains traits.
 * Each file contains traits for one material.
 * The data is split in default traits and traits per stat type. If no traits are present for a stat type, the default traits will be used.
 * <p>
 * Note that this manager only contains the references per IDs. The actual combining is done in the registry.
 * <p>
 * The reason for the material id inside the file is so that multiple mods can add different traits to the same material.
 * Note that, as opposed to stats, this is <em>additive</em>, meaning if two mods add traits to the same material, both will be applied.
 * <p>
 * The location inside datapacks is "materials/traits".
 * So if your mods name is "foobar", the location for your mads material stats is "data/foobar/materials/traits".
 */
@Log4j2
public class MaterialTraitsManager extends JsonDataLoader {

  @VisibleForTesting
  protected static final String FOLDER = "materials/traits";
  @VisibleForTesting
  protected static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
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
  protected void apply(Map<Identifier,JsonElement> splashList, ResourceManager resourceManagerIn, Profiler profilerIn) {
    List<TraitMappingJson> parsedSplashList = parseSplashlist(splashList);

    materialDefaultTraits = parsedSplashList.stream()
      .collect(Collectors.toMap(
        TraitMappingJson::getMaterialId,
        TraitMappingJson::getDefaultTraits,
        this::concatLists
      ));

    materialToTraitsPerStatsType = parsedSplashList.stream()
      .collect(Collectors.toMap(
        TraitMappingJson::getMaterialId,
        TraitMappingJson::getPerStat,
        this::combineMaps));

    materialToTraitsPerStatsType.forEach((materialId, traitsMap) -> {
      log.debug("Loaded traits for material '{}': \n\tDefault - {}{}",
        materialId,
        Arrays.toString(materialDefaultTraits.getOrDefault(materialId, Collections.emptyList()).toArray()),
        Util.toIndentedStringList(traitsMap.entrySet().stream()
          .map(entry -> String.format("%s - %s", entry.getKey(), Arrays.toString(entry.getValue().toArray())))
          .collect(Collectors.toList())));
    });
  }

  private Map<MaterialStatsId, List<TraitId>> combineMaps(Map<MaterialStatsId, List<TraitId>> materialStatsIdListMap, Map<MaterialStatsId, List<TraitId>> materialStatsIdListMap2) {
    return Stream.concat(materialStatsIdListMap.keySet().stream(), materialStatsIdListMap2.keySet().stream())
      // take all keys combined in both maps, and just add everything from both sets at once for each key
      .distinct()
      .collect(Collectors.toMap(
        materialStatsId -> materialStatsId,
        materialStatsId -> concatLists(
          materialStatsIdListMap.getOrDefault(materialStatsId, Collections.emptyList()),
          materialStatsIdListMap2.getOrDefault(materialStatsId, Collections.emptyList())
        ))
      );
  }

  private List<TraitMappingJson> parseSplashlist(Map<Identifier, JsonElement> splashList) {
    return splashList.entrySet().stream()
      .map(this::parseJsonEntry)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  private <T> List<T> concatLists(List<T> list1, List<T> list2) {
    return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
  }

  @Nullable
  private TraitMappingJson parseJsonEntry(Map.Entry<Identifier, JsonElement> entry) {
    if (!entry.getValue().isJsonObject()) {
      return null;
    }
    try {
      TraitMappingJson traitMappingJson = GSON.fromJson(entry.getValue(), TraitMappingJson.class);
      if (traitMappingJson.getMaterialId() == null) {
        throw TinkerJSONException.materialTraitsJsonWithoutMaterial(entry.getKey());
      }
      return traitMappingJson;
    } catch (Exception e) {
      log.error("Could not deserialize material trait mapping from file {}. JSON: {}", entry.getKey(), entry.getValue(), e);
      return null;
    }
  }

}
