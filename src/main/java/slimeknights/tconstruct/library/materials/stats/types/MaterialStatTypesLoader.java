package slimeknights.tconstruct.library.materials.stats.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.mantle.data.listener.MergingJsonDataLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.HashMap;
import java.util.Map;

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
public class MaterialStatTypesLoader extends MergingJsonDataLoader<MaterialStatTypeBuilder> {
  public static final String FOLDER = "tinkering/materials/stat_types";

  /** Map of material stat types, keyed by material ID */
  @Getter
  @Setter
  private Map<MaterialStatsId, DynamicMaterialStatType> statTypes;

  public MaterialStatTypesLoader() {
    super(JsonHelper.DEFAULT_GSON, FOLDER, MaterialStatTypeBuilder::begin);
  }

  @Override
  protected void parse(MaterialStatTypeBuilder builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException {
    try {
      JsonObject obj = element.getAsJsonObject();
      if (obj.has("can_repair"))
        builder.setCanRepair(obj.get("can_repair").getAsBoolean());
      if (obj.has("stats"))
        obj.get("stats").getAsJsonArray().forEach(field -> {
          builder.addField(DynamicStatField.deserializeSelf(field.getAsJsonObject(), id));
        });
    } catch (Exception e) {
      builder.shouldBuild(false);
      throw e;
    }
  }

  @Override
  protected void finishLoad(Map<ResourceLocation, MaterialStatTypeBuilder> map, ResourceManager manager) {
    // Take the final structure and actually load the different material stats. This
    // drops all invalid stats
    map.forEach((key, value) -> {
      if (value.shouldBuild())
        statTypes.put(new MaterialStatsId(key), value.build());
    });
  }

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    statTypes = new HashMap<>();
    long time = System.nanoTime();
    super.onResourceManagerReload(manager);
    log.info("Loaded {} Material Stat Types in {} ms", statTypes.size(), (System.nanoTime() - time) / 1000000f);
  }
}
