package slimeknights.tconstruct.library.materials;

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
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.exception.TinkerJSONException;
import slimeknights.tconstruct.library.materials.json.MaterialJson;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Loads the material data from datapacks and provides them to whatever needs them.
 * Contains only the very basic material information, craftability, traits, but no stats.
 * See {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager} for stats.
 * <p>
 * The location inside datapacks is "materials".
 * So if your mods name is "foobar", the location for your mods materials is "data/foobar/materials".
 */
@Log4j2
public class MaterialManager extends JsonReloadListener {
  public static final String FOLDER = "materials/definition";
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeAdapter(ICondition.class, new ConditionSerializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Runnable to run after loading material stats */
  private final Runnable onLoaded;
  /** Map of all materials */
  private Map<MaterialId, IMaterial> materials = Collections.emptyMap();
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
   */
  public void updateMaterialsFromServer(Collection<IMaterial> materialList) {
    this.materials = materialList.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        Function.identity())
      );
    onMaterialUpdate();
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
    this.materials = splashList.entrySet().stream()
      .filter(entry -> entry.getValue().isJsonObject())
      .map(entry -> loadMaterial(entry.getKey(), entry.getValue().getAsJsonObject()))
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(
        IMaterial::getIdentifier,
        material -> material)
      );
    onMaterialUpdate();
    
    log.debug("Loaded materials: {}", Util.toIndentedStringList(materials.keySet()));
    log.info("{} materials loaded", materials.size());
  }

  /**
   * Gets the packet to send on player login
   * @return  Packet object
   */
  public Object getUpdatePacket() {
    return new UpdateMaterialsPacket(materials.values());
  }

  /** Gets an int value or a default */
  private static int orDefault(@Nullable Integer integer, int def) {
    return integer == null ? def : integer;
  }

  @Nullable
  private IMaterial loadMaterial(ResourceLocation materialId, JsonObject jsonObject) {
    try {
      MaterialJson materialJson = GSON.fromJson(jsonObject, MaterialJson.class);
      // condition
      ICondition condition = materialJson.getCondition();
      if (condition != null && !condition.test()) {
        log.debug("Skipped loading material {} as it did not match the condition", materialId);
        return null;
      }

      if (materialJson.getCraftable() == null) {
        throw TinkerJSONException.materialJsonWithoutCraftingInformation(materialId);
      }

      boolean isCraftable = Boolean.TRUE.equals(materialJson.getCraftable());
      boolean hidden = Boolean.TRUE.equals(materialJson.getHidden());

      // parse color from string
      Color color = Optional.ofNullable(materialJson.getTextColor())
                            .filter(str -> !str.isEmpty())
                            .map(Color::fromHex)
                            .orElse(Material.WHITE);


      // parse trait
      return new Material(materialId, orDefault(materialJson.getTier(), 0), orDefault(materialJson.getSortOrder(), 100), isCraftable, color, hidden);
    } catch (Exception e) {
      log.error("Could not deserialize material {}. JSON: {}", materialId, jsonObject, e);
      return null;
    }
  }

  private static class ConditionSerializer implements JsonDeserializer<ICondition>, JsonSerializer<ICondition> {
    @Override
    public ICondition deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
      return CraftingHelper.getCondition(JSONUtils.getJsonObject(json, "condition"));
    }

    @Override
    public JsonElement serialize(ICondition condition, Type type, JsonSerializationContext context) {
      return CraftingHelper.serialize(condition);
    }
  }
}
