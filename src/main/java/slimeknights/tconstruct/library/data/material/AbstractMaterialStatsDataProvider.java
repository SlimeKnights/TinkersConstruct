package slimeknights.tconstruct.library.data.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJsonWrapper;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Base data generator for use in addons, depends on the regular material provider */
public abstract class AbstractMaterialStatsDataProvider extends GenericDataProvider {
  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(JsonStatWrapper.class, new StatSerializer(MaterialStatsManager.GSON))
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** All material stats generated so far */
  private final Map<MaterialId,List<IMaterialStats>> allMaterialStats = new HashMap<>();
  /* Materials data provider for validation */
  private final AbstractMaterialDataProvider materials;

  public AbstractMaterialStatsDataProvider(DataGenerator gen, AbstractMaterialDataProvider materials) {
    super(gen, MaterialStatsManager.FOLDER, GSON);
    this.materials = materials;
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialStats();

  @Override
  public void act(DirectoryCache cache) {
    addMaterialStats();

    Set<MaterialId> materialsGenerated = materials.getAllMaterials();
    // ensure we have stats for all materials
    for (MaterialId material : materialsGenerated) {
      if (!allMaterialStats.containsKey(material)) {
        throw new IllegalStateException(String.format("Missing material stats for '%s'", material));
      }
    }
    // ensure we have materials for all stats
    for (MaterialId material : allMaterialStats.keySet()) {
      if (!materialsGenerated.contains(material)) {
        throw new IllegalStateException(String.format("Adding stats for unknown material for '%s'", material));
      }
    }
    // generate finally
    allMaterialStats.forEach(
      (materialId, iMaterialStats) -> saveThing(cache, materialId, convert(materialId, iMaterialStats)));
  }


  /* Helpers */

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param stats     Stats to add
   */
  protected void addMaterialStats(MaterialId location, IMaterialStats... stats) {
    allMaterialStats.computeIfAbsent(location, materialId -> new ArrayList<>())
                    .addAll(Arrays.asList(stats));
  }

  /* Internal */

  /** Converts a material and stats list to a JSON */
  private JsonWrapper convert(MaterialId material, List<IMaterialStats> stats) {
    List<JsonStatWrapper> wrappedStats = stats.stream()
      .map(stat -> new JsonStatWrapper(stat.getIdentifier(), stat))
      .collect(Collectors.toList());
    return new JsonWrapper(material, wrappedStats);
  }

  /**
   * Separate json wrapper for serialization, since we know the types here.
   * See {@link MaterialStatJsonWrapper} for its deserialization counterpart.
   */
  @SuppressWarnings("unused")
  @AllArgsConstructor
  private static class JsonWrapper {
    private final ResourceLocation materialId;
    private final List<JsonStatWrapper> stats;
  }

  @Getter
  private static class JsonStatWrapper extends MaterialStatJsonWrapper.BaseMaterialStatsJson {
    private final IMaterialStats stat;

    public JsonStatWrapper(MaterialStatsId id, IMaterialStats stat) {
      super(id);
      this.stat = stat;
    }
  }

  @AllArgsConstructor
  private static class StatSerializer implements JsonSerializer<JsonStatWrapper> {
    private final Gson gson;

    @Override
    public JsonElement serialize(JsonStatWrapper src, Type typeOfSrc, JsonSerializationContext context) {
      JsonElement original = gson.toJsonTree(src);

      JsonObject output = new JsonObject();
      // id is a primitive, stat is a sub-object, so we take over all primitives
      original.getAsJsonObject().entrySet().stream()
        .filter(entry -> entry.getValue().isJsonPrimitive())
        .forEach(entry -> output.add(entry.getKey(), entry.getValue()));
      // copy over everything from the stat serialization. The "stat" here is the variable name of the JsonStatWrapper above
      original.getAsJsonObject().get("stat").getAsJsonObject().entrySet().forEach(entry -> output.add(entry.getKey(), entry.getValue()));
      return output;
    }
  }
}
