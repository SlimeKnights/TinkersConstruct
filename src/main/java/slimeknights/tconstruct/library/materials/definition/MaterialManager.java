package slimeknights.tconstruct.library.materials.definition;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.tconstruct.library.exception.TinkerJSONException;
import slimeknights.tconstruct.library.materials.json.MaterialJson;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNullElse;

/**
 * Loads the material data from datapacks and provides them to whatever needs them.
 * Contains only the very basic material information, craftability, traits, but no stats.
 * See {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager} for stats.
 * <p>
 * The location inside datapacks is "materials".
 * So if your mods name is "foobar", the location for your mods materials is "data/foobar/materials".
 */
@Log4j2
public class MaterialManager extends SimpleJsonResourceReloadListener {
  public static final String FOLDER = "tinkering/materials/definition";
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(ICondition.class, new ConditionSerializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Runnable to run after loading material stats */
  private final Runnable onLoaded;
  /** Map of all materials */
  private Map<MaterialId,IMaterial> materials = Collections.emptyMap();
  /** Map of material ID redirects */
  private Map<MaterialId,MaterialId> redirects = Collections.emptyMap();
  /** Sorted list of visible materials */
  private List<IMaterial> sortedMaterials = Collections.emptyList();

  public MaterialManager(Runnable onLoaded) {
    super(GSON, FOLDER);
    this.onLoaded = onLoaded;
  }

  @VisibleForTesting
  MaterialManager() {
    this(() -> {});
  }

  /**
   * Gets a collection of all loaded materials, sorted by tier and sort orders
   * @return  All loaded materials
   */
  public Collection<IMaterial> getVisibleMaterials() {
    return sortedMaterials;
  }

  /**
   * Gets a collection of all loaded materials, unsorted and including hidden materials
   * @return  All loaded materials
   */
  public Collection<IMaterial> getAllMaterials() {
    return materials.values();
  }

  /**
   * Gets a material based on its ID
   * @param materialId  Material ID
   * @return  Optional of material, empty if missing
   */
  public Optional<IMaterial> getMaterial(MaterialId materialId) {
    return Optional.ofNullable(materials.get(materialId));
  }

  /**
   * Resolves any redirect for the given material ID
   * @param materialId  Original material ID
   * @return  Redirected ID, or original if no redirect is set up for this ID
   */
  public MaterialId resolveRedirect(MaterialId materialId) {
    return redirects.getOrDefault(materialId, materialId);
  }

  /**
   * Recreates the fluid lookup and sorted list using the new materials list
   */
  private void onMaterialUpdate() {
    this.sortedMaterials = this.materials.values().stream()
                                         .filter(mat -> !mat.isHidden())
                                         .sorted().collect(Collectors.toList());
    onLoaded.run();
  }

  /**
   * Updates the material list from the server.list. Should only be called client side
   * @param materialList  Server material list
   * @param redirects     Map of material redirects
   */
  public void updateMaterialsFromServer(Collection<IMaterial> materialList, Map<MaterialId,MaterialId> redirects) {
    this.materials = materialList.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        Function.identity())
      );
    this.redirects = redirects;
    onMaterialUpdate();
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
    long time = System.nanoTime();
    Map<MaterialId, MaterialId> redirects = new HashMap<>();
    this.materials = splashList.entrySet().stream()
      .filter(entry -> entry.getValue().isJsonObject())
      .map(entry -> loadMaterial(entry.getKey(), entry.getValue().getAsJsonObject(), redirects))
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        material -> material)
      );
    // validate redirects
    Iterator<Entry<MaterialId,MaterialId>> redirectIterator = redirects.entrySet().iterator();
    while (redirectIterator.hasNext()) {
      Entry<MaterialId,MaterialId> entry = redirectIterator.next();
      if (!this.materials.containsKey(entry.getValue())) {
        log.error("Invalid material redirect {} as material {} does not exist", entry.getKey(), entry.getValue());
        redirectIterator.remove();
      }
    }
    this.redirects = redirects;
    onMaterialUpdate();
    
    log.debug("Loaded materials: {}", Util.toIndentedStringList(materials.keySet()));
    log.debug("Loaded redirects: {}", Util.toIndentedStringList(redirects.keySet()));
    log.info("Loaded {} materials in {} ms", materials.size(), (System.nanoTime() - time) / 1000000f);
  }

  /**
   * Gets the packet to send on player login
   * @return  Packet object
   */
  public UpdateMaterialsPacket getUpdatePacket() {
    return new UpdateMaterialsPacket(materials.values(), redirects);
  }

  @Nullable
  private IMaterial loadMaterial(ResourceLocation materialId, JsonObject jsonObject, Map<MaterialId, MaterialId> redirects) {
    try {
      MaterialJson materialJson = GSON.fromJson(jsonObject, MaterialJson.class);
      // condition
      ICondition condition = materialJson.getCondition();
      if (condition != null && !condition.test()) {
        log.debug("Skipped loading material {} as it did not match the condition", materialId);
        return null;
      }

      // if defined, the material will redirect to another material
      MaterialJson.Redirect[] redirectsJson = materialJson.getRedirect();
      if (redirectsJson != null) {
        for (MaterialJson.Redirect redirect : redirectsJson) {
          ICondition redirectCondition = redirect.getCondition();
          if (redirectCondition == null || redirectCondition.test()) {
            MaterialId redirectTarget = new MaterialId(redirect.getId());
            log.debug("Redirecting material {} to {}", materialId, redirectTarget);
            redirects.put(new MaterialId(materialId), redirectTarget);
            return null;
          }
        }
      }

      if (materialJson.getCraftable() == null) {
        throw TinkerJSONException.materialJsonWithoutCraftingInformation(materialId);
      }

      boolean isCraftable = Boolean.TRUE.equals(materialJson.getCraftable());
      boolean hidden = Boolean.TRUE.equals(materialJson.getHidden());

      // parse trait
      return new Material(materialId, requireNonNullElse(materialJson.getTier(), 0), requireNonNullElse(materialJson.getSortOrder(), 100), isCraftable, hidden);
    } catch (Exception e) {
      log.error("Could not deserialize material {}. JSON: {}", materialId, jsonObject, e);
      return null;
    }
  }

  private static class ConditionSerializer implements JsonDeserializer<ICondition>, JsonSerializer<ICondition> {
    @Override
    public ICondition deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
      return CraftingHelper.getCondition(GsonHelper.convertToJsonObject(json, "condition"));
    }

    @Override
    public JsonElement serialize(ICondition condition, Type type, JsonSerializationContext context) {
      return CraftingHelper.serialize(condition);
    }
  }
}
