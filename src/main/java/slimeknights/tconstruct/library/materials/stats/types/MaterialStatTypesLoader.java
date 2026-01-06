package slimeknights.tconstruct.library.materials.stats.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.mantle.data.listener.MergingJsonDataLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import java.util.Map;

/**
 * Loads the different material stat types from the datapacks.
 * The file location determines the material it contains stat types for, each file contains stat types for exactly one material.
 * The stat types must be registered with TiC before loading or it'll fail.
 * <p>
 * Files with the same name are merged in a similar way to tags, so multiple mods can add different stat types to the same material.
 * If two different sources add the same stat types to the same material the first one encountered will be used, and the second one will be skipped.
 * (e.g. having a 'Laser' stat type, and there are 2 mods who add Laser stat types to the iron material)
 * <p>
 * The location inside datapacks is "materials/stat_types".
 * So if the material's mod name is "foobar", the location for your material's stat types is "data/foobar/materials/stat_types".
 */
@Log4j2
public class MaterialStatTypesLoader extends MergingJsonDataLoader<MaterialStatTypeBuilder> {
  public static final String FOLDER = "tinkering/materials/stat_types";

  /** Runnable to run after loading material stat types */
  private final Runnable onLoaded;

  private int counter;

  public MaterialStatTypesLoader(Runnable onLoaded) {
    super(JsonHelper.DEFAULT_GSON, FOLDER, MaterialStatTypeBuilder::begin);
    this.onLoaded = onLoaded;
  }

  @Override
  protected void parse(MaterialStatTypeBuilder builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException {
    counter++;
    try {
    JsonObject obj=element.getAsJsonObject();
    if(obj.has("can_repair")) builder.setCanRepair(obj.get("can_repair").getAsBoolean());
    if(obj.has("stats")) builder.setStats(obj.get("stats").getAsJsonObject());
    } catch (Exception e) {
      counter--;
      log.error("Error parsing material stat type {}: {}", id, e.getMessage());
      builder.shouldBuild(false);
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation,MaterialStatTypeBuilder> map, ResourceManager manager) {
    // Take the final structure and actually load the different material stats. This drops all invalid stats
    IMaterialRegistry materialRegistry = MaterialRegistry.getInstance();
    materialRegistry.clearDynamicStatTypes();
    map.forEach((key,value)->{
      if(value.shouldBuild()) materialRegistry.registerDynamicStatType(value.build(key));
    });
    onLoaded.run();
  }

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    long time = System.nanoTime();
    counter=0;
    super.onResourceManagerReload(manager);
    log.info("Loaded {} Material Stat Types in {} ms", counter, (System.nanoTime() - time) / 1000000f);
  }
}
