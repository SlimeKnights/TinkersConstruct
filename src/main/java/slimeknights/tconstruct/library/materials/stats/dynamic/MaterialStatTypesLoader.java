package slimeknights.tconstruct.library.materials.stats.dynamic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Loads the different material stat types from the datapacks.
 * The file location determines the material it contains stat types for, each
 * file contains stat types for exactly one material.
 * The stat types must be registered with TiC before loading or it'll fail.
 * <p>
 * Files with the same name are merged in a similar way to tags, so multiple
 * mods can add different stat types to the same material.
 * If two different sources add the same stat types to the same material the
 * first one encountered will be used, and the second one will be skipped.
 * (e.g. having a 'Laser' stat type, and there are 2 mods who add Laser stat
 * types to the iron material)
 * <p>
 * The location inside datapacks is "materials/stat_types".
 * So if the material's mod name is "foobar", the location for your material's
 * stat types is "data/foobar/materials/stat_types".
 */
@Log4j2
public class MaterialStatTypesLoader extends SimpleJsonResourceReloadListener {
  public static final String FOLDER = "tinkering/materials/stat_types";
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  /** Map of material stat types, keyed by material ID */
  @Getter
  @Setter
  private Map<MaterialStatsId, DynamicMaterialStatType> statTypes = new HashMap<>();

  public MaterialStatTypesLoader() {
    super(GSON, FOLDER);
  }
  
  @Internal
  public void loadResources(ResourceManager manager) {
    ProfilerFiller profiler = InactiveProfiler.INSTANCE;
    Map<ResourceLocation, JsonElement> jsonMap = this.prepare(manager, profiler);
    this.apply(jsonMap, manager, profiler);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
    this.statTypes.clear();
    long time = System.nanoTime();
    Map<MaterialStatsId, DynamicMaterialStatType> newStatTypes = new HashMap<>();
    splashList.entrySet().stream()
      .filter(entry -> entry.getValue().isJsonObject())
      .forEach(entry -> {
        MaterialStatsId id = new MaterialStatsId(entry.getKey());
        JsonObject obj = entry.getValue().getAsJsonObject();
        obj.addProperty("id", id.toString());
        try {
          newStatTypes.put(id, DynamicMaterialStatType.LOADER.deserialize(obj));
        } catch (Exception e) {
          log.error("Failed to parse material stat type {}: {}", id, e.toString());
        }
      });

    this.statTypes = newStatTypes;
    log.info("Loaded {} Material Stat Types in {} ms", statTypes.size(), (System.nanoTime() - time) / 1000000f);
  }

  @TestOnly
  public void apply(Map<ResourceLocation, JsonElement> splashList) {
    this.statTypes.clear();
    long time = System.nanoTime();
    Map<MaterialStatsId, DynamicMaterialStatType> newStatTypes = new HashMap<>();
    splashList.entrySet().stream()
      .filter(entry -> entry.getValue().isJsonObject())
      .forEach(entry -> {
                MaterialStatsId id = new MaterialStatsId(entry.getKey());
        JsonObject obj = entry.getValue().getAsJsonObject();
        obj.addProperty("id", id.toString());
        try {
          newStatTypes.put(id, DynamicMaterialStatType.LOADER.deserialize(obj));
        } catch (Exception e) {
          log.error("Failed to parse material stat type {}: {}", id, e.getMessage());
        }
      });

    this.statTypes = newStatTypes;
    log.info("Loaded {} Material Stat Types in {} ms", statTypes.size(), (System.nanoTime() - time) / 1000000f);
  }
}