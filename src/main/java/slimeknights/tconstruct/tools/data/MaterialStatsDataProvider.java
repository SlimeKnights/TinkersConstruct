package slimeknights.tconstruct.tools.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.library.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.json.MaterialStatJsonWrapper;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialStatsDataProvider extends GenericDataProvider {

  private static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(JsonStatWrapper.class, new StatSerializer(MaterialStatsManager.GSON))
    .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  public MaterialStatsDataProvider(DataGenerator gen) {
    super(gen, MaterialStatsManager.FOLDER, GSON);
  }

  @Override
  public void run(DataCache cache) {
    MaterialStats.allMaterialStats.forEach(
      (materialId, iMaterialStats) -> saveThing(cache, materialId, convert(materialId, iMaterialStats))
    );
  }

  private JsonWrapper convert(MaterialId material, List<IMaterialStats> stats) {
    List<JsonStatWrapper> wrappedStats = stats.stream()
      .map(stat -> new JsonStatWrapper(stat.getIdentifier(), stat))
      .collect(Collectors.toList());
    return new JsonWrapper(material, wrappedStats);
  }

  @Override
  public String getName() {
    return "TConstruct Material Stats";
  }

  /**
   * Separate json wrapper for serialization, since we know the types here.
   * See {@link MaterialStatJsonWrapper} for its deserialization counterpart.
   */
  @AllArgsConstructor
  private static class JsonWrapper {

    private final Identifier materialId;
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
