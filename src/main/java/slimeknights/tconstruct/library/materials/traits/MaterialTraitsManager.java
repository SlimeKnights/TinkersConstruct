package slimeknights.tconstruct.library.materials.traits;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.mantle.data.listener.MergingJsonDataLoader;
import slimeknights.tconstruct.library.exception.TinkerAPIMaterialException;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialTraitsJson;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
public class MaterialTraitsManager extends MergingJsonDataLoader<MaterialTraits.Builder> {
  public static final String FOLDER = "tinkering/materials/traits";
  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(ModifierEntry.class, ModifierEntry.LOADABLE)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Runnable to run after loading traits */
  private final Runnable onLoaded;

  /** Map of fallback stat ID to use if the stat ID lacks traits */
  private final Map<MaterialStatsId, MaterialStatsId> statTypeFallbacks = new HashMap<>();

  /** Map of material ID to all relevant trait data */
  @VisibleForTesting
  protected Map<MaterialId, MaterialTraits> materialTraits = Collections.emptyMap();

  public MaterialTraitsManager(Runnable onLoaded) {
    super(GSON, FOLDER, id -> new MaterialTraits.Builder());
    this.onLoaded = onLoaded;
  }

  @VisibleForTesting
  MaterialTraitsManager() {
    this(() -> {});
  }

  /**
   * Registers a fallback to use when a stat type has no traits. if the fallback does not have it, will use the default.
   * Fallbacks are not recursive, they are intended to be categories.
   */
  public <T extends IMaterialStats> void registerStatTypeFallback(MaterialStatsId statType, MaterialStatsId fallback) {
    if (statTypeFallbacks.containsKey(statType)) {
      throw TinkerAPIMaterialException.materialStatsTypeRegisteredTwice(statType);
    }
    statTypeFallbacks.put(statType, fallback);
  }

  /**
   * Gets the default traits for the given material
   * @param materialId  Material
   * @return  List of default traits
   */
  public List<ModifierEntry> getDefaultTraits(MaterialId materialId) {
    MaterialTraits traits = materialTraits.get(materialId);
    return traits == null ? Collections.emptyList() : traits.getDefaultTraits();
  }

  /**
   * Checks if the material has traits for the given stat type
   * @param materialId  Material ID
   * @param statId      Stats type
   * @return  True if the given stat type has traits
   */
  public boolean hasUniqueTraits(MaterialId materialId, MaterialStatsId statId) {
    MaterialTraits traits = materialTraits.get(materialId);
    return traits != null && traits.hasUniqueTraits(statId);
  }

  /**
   * Gets the traits for the given stat type, or the default if the stat type does not have unique traits
   * @param materialId  Material ID
   * @param statId      Stats type
   * @return  List of traits
   */
  public List<ModifierEntry> getTraits(MaterialId materialId, MaterialStatsId statId) {
    MaterialTraits traits = materialTraits.get(materialId);
    return traits == null ? Collections.emptyList() : traits.getTraits(statId);
  }

  /**
   * Gets the packet to send on player login
   * @return  Packet object
   */
  public UpdateMaterialTraitsPacket getUpdatePacket() {
    return new UpdateMaterialTraitsPacket(materialTraits);
  }

  /**
   * Updates the traits from the server
   * @param materialToTraits  Traits map
   */
  public void updateFromServer(Map<MaterialId,MaterialTraits> materialToTraits) {
    this.materialTraits = materialToTraits;
    onLoaded.run();
  }

  @Override
  protected void parse(MaterialTraits.Builder builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException {
    MaterialTraitsJson json = GSON.fromJson(element, MaterialTraitsJson.class);
    builder.setDefaultTraits(json.getDefaultTraits());
    for (Entry<MaterialStatsId,List<ModifierEntry>> entry : json.getPerStat().entrySet()) {
      builder.setTraits(entry.getKey(), entry.getValue());
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation,MaterialTraits.Builder> map, ResourceManager manager) {
    ImmutableMap.Builder<MaterialId,MaterialTraits> builder = ImmutableMap.builder();
    for (Entry<ResourceLocation,MaterialTraits.Builder> entry : map.entrySet()) {
      MaterialTraits traits = entry.getValue().build(statTypeFallbacks);
      builder.put(new MaterialId(entry.getKey()), traits);
      log.debug("Loaded traits for material '{}': \n\tDefault - {}{}",
                entry.getKey(),
                Arrays.toString(traits.getDefaultTraits().toArray()),
                Util.toIndentedStringList(traits.getTraitsPerStats().entrySet().stream()
                                                .map(entry2 -> String.format("%s - %s", entry2.getKey(), Arrays.toString(entry2.getValue().toArray())))
                                                .collect(Collectors.toList())));
    }
    materialTraits = builder.build();
    onLoaded.run();
  }

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    long time = System.nanoTime();
    super.onResourceManagerReload(manager);
    log.info("{} traits loaded for {} materials in {} ms",
             materialTraits.values().stream().mapToInt(traits -> traits.getTraitsPerStats().size() + (traits.getDefaultTraits().isEmpty() ? 0 : 1)).sum(),
             materialTraits.size(), (System.nanoTime() - time) / 1000000f);
  }
}
